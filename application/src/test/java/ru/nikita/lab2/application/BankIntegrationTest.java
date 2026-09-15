package ru.nikita.lab2.application;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import org.hibernate.SessionFactory;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import ru.nikita.lab2.dao.adapter.JpaOperationStore;
import ru.nikita.lab2.domain.*;
import ru.nikita.lab2.service.*;
import ru.nikita.lab2.service.exception.*;

import tools.jackson.databind.json.JsonMapper;

import java.math.BigDecimal;
import java.net.URI;
import java.net.http.*;
import java.util.*;
import java.util.concurrent.*;

@Tag("integration")
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
            "spring.datasource.url=${TEST_DB_URL:jdbc:postgresql://localhost:5432/lab3test}",
            "spring.datasource.username=${TEST_DB_USER:app}",
            "spring.datasource.password=${TEST_DB_PASSWORD:app}",
            "spring.jpa.properties.hibernate.generate_statistics=true"
        })
class BankIntegrationTest {
    @LocalServerPort int port;
    @Autowired UserCRUDService users;
    @Autowired AccountCRUDService accounts;
    @Autowired FriendService friends;
    @Autowired OperationService operations;
    @Autowired JdbcTemplate jdbc;
    @Autowired EntityManagerFactory entityManagerFactory;
    @Autowired EntityManager entityManager;
    @MockitoSpyBean JpaOperationStore operationStore;
    final HttpClient http = HttpClient.newHttpClient();
    final JsonMapper json = new JsonMapper();

    @BeforeEach
    void cleanTestDatabase() {
        // Эти проверки удаляют данные только в явно выделенной тестовой базе.
        String database = jdbc.queryForObject("select current_database()", String.class);
        assertTrue(
                database != null && database.endsWith("test"),
                "Use a separate database whose name ends with 'test'");
        jdbc.update("delete from lab2.users");
        for (String table : List.of("users_aud", "accounts_aud", "operations_aud"))
            jdbc.update("delete from lab2." + table);
    }

    record Reply(int status, String body) {}

    Reply request(String method, String path, Object body) throws Exception {
        var request =
                HttpRequest.newBuilder(URI.create("http://localhost:" + port + path))
                        .header("Content-Type", "application/json")
                        .method(
                                method,
                                body == null
                                        ? HttpRequest.BodyPublishers.noBody()
                                        : HttpRequest.BodyPublishers.ofString(
                                                json.writeValueAsString(body)))
                        .build();
        var response = http.send(request, HttpResponse.BodyHandlers.ofString());
        return new Reply(response.statusCode(), response.body());
    }

    Map<?, ?> object(Reply reply, int status) {
        assertEquals(status, reply.status(), reply.body());
        return json.readValue(reply.body(), Map.class);
    }

    List<?> list(String path) throws Exception {
        var reply = request("GET", path, null);
        assertEquals(200, reply.status(), reply.body());
        return json.readValue(reply.body(), List.class);
    }

    BigDecimal money(Object value) {
        return new BigDecimal(value.toString()).setScale(2);
    }

    User user() {
        return users.createUser(
                new User(
                        null,
                        "u" + UUID.randomUUID().toString().substring(0, 12),
                        "User",
                        20,
                        null,
                        null));
    }

    Account account(User user) {
        return accounts.createAccount(user.id());
    }

    @Test
    void completeHttpScenarioAndAudit() throws Exception {
        var created =
                object(
                        request(
                                "POST",
                                "/api/users",
                                Map.of("login", "nikita", "name", "Nikita", "age", 20)),
                        201);
        UUID id = UUID.fromString(created.get("id").toString());
        assertEquals(7, id.version());
        assertEquals("nikita", object(request("GET", "/api/users/" + id, null), 200).get("login"));
        object(
                request(
                        "PUT",
                        "/api/users/" + id,
                        Map.of(
                                "name",
                                "Nikita",
                                "age",
                                21,
                                "gender",
                                "MALE",
                                "hairColor",
                                "BLACK")),
                200);
        var patch = new HashMap<String, Object>();
        patch.put("gender", null);
        var updated = object(request("PATCH", "/api/users/" + id, patch), 200);
        assertNull(updated.get("gender"));
        assertEquals("BLACK", updated.get("hairColor"));
        assertEquals(1, list("/api/users?hairColor=BLACK").size());
        assertEquals(0, list("/api/users?gender=MALE").size());
        var a = object(request("POST", "/api/accounts", Map.of("userId", id)), 201);
        var b = object(request("POST", "/api/accounts", Map.of("userId", id)), 201);
        UUID aId = UUID.fromString(a.get("id").toString()),
                bId = UUID.fromString(b.get("id").toString());
        assertEquals(7, aId.version());
        assertEquals(2, list("/api/users/" + id + "/accounts").size());
        assertEquals(2, list("/api/accounts").size());
        object(
                request("POST", "/api/accounts/" + aId + "/deposits", Map.of("amount", "100.00")),
                201);
        var transfer =
                object(
                        request(
                                "POST",
                                "/api/transfers",
                                Map.of(
                                        "fromAccountId",
                                        aId,
                                        "toAccountId",
                                        bId,
                                        "amount",
                                        "10.05")),
                        201);
        assertEquals(bId.toString(), transfer.get("destinationId"));
        assertEquals(new BigDecimal("0.00"), money(transfer.get("commission")));
        assertEquals(7, UUID.fromString(transfer.get("id").toString()).version());
        object(
                request("POST", "/api/accounts/" + aId + "/withdrawals", Map.of("amount", "9.95")),
                201);
        assertEquals(
                new BigDecimal("80.00"),
                money(object(request("GET", "/api/accounts/" + aId, null), 200).get("balance")));
        assertEquals(1, list("/api/operations?type=TRANSFER&accountId=" + bId).size());
        assertEquals(1, list("/api/accounts/" + bId + "/operations").size());
        assertEquals(
                3,
                list("/api/accounts/"
                                + aId
                                + "/operations?from=2000-01-01T00:00:00Z&to=2100-01-01T00:00:00Z")
                        .size());
        assertEquals(
                0, list("/api/accounts/" + aId + "/operations?to=2000-01-01T00:00:00Z").size());
        assertEquals(409, request("DELETE", "/api/accounts/" + aId, null).status());
        var empty = account(users.getUser(id));
        assertEquals(204, request("DELETE", "/api/accounts/" + empty.id(), null).status());
        assertTrue(jdbc.queryForObject("select count(*) from lab2.users_aud", Integer.class) >= 3);
        assertTrue(
                jdbc.queryForObject("select count(*) from lab2.accounts_aud", Integer.class) >= 5);
        assertEquals(
                3, jdbc.queryForObject("select count(*) from lab2.operations_aud", Integer.class));
        assertEquals(204, request("DELETE", "/api/users/" + id, null).status());
        assertEquals(404, request("GET", "/api/users/" + id, null).status());
        assertEquals(0, list("/api/accounts").size());
    }

    @Test
    void enumValuesRoundTripThroughHttpDomainAndDatabase() throws Exception {
        for (String gender : List.of("MALE", "FEMALE")) {
            for (String color : List.of("BLACK", "BLONDE", "RED", "COLORED")) {
                var created =
                        object(
                                request(
                                        "POST",
                                        "/api/users",
                                        Map.of(
                                                "login",
                                                gender + color,
                                                "name",
                                                "Enum test",
                                                "age",
                                                20,
                                                "gender",
                                                gender,
                                                "hairColor",
                                                color)),
                                201);
                UUID id = UUID.fromString(created.get("id").toString());
                assertEquals(gender, created.get("gender"));
                assertEquals(color, created.get("hairColor"));
                var stored =
                        jdbc.queryForMap(
                                "select gender, hair_color from lab2.users where id = ?", id);
                assertEquals(gender, stored.get("gender"));
                assertEquals(color, stored.get("hair_color"));
                var filtered = list("/api/users?gender=" + gender + "&hairColor=" + color);
                assertEquals(1, filtered.size());
                assertEquals(id.toString(), ((Map<?, ?>) filtered.get(0)).get("id"));
                var patch =
                        object(
                                request(
                                        "PATCH",
                                        "/api/users/" + id,
                                        Map.of("gender", gender, "hairColor", color)),
                                200);
                assertEquals(gender, patch.get("gender"));
                assertEquals(color, patch.get("hairColor"));
                var cleared =
                        object(
                                request(
                                        "PATCH",
                                        "/api/users/" + id,
                                        Collections.singletonMap("hairColor", null)),
                                200);
                assertEquals(gender, cleared.get("gender"));
                assertNull(cleared.get("hairColor"));
                object(request("PATCH", "/api/users/" + id, Map.of("name", "Changed")), 200);
                var unchanged = object(request("GET", "/api/users/" + id, null), 200);
                assertEquals(gender, unchanged.get("gender"));
                assertNull(unchanged.get("hairColor"));
            }
        }
        User owner = user();
        Account a = account(owner), b = account(owner);
        operations.deposit(a.id(), new BigDecimal("10.00"));
        operations.withdraw(a.id(), BigDecimal.ONE);
        operations.transfer(a.id(), b.id(), BigDecimal.ONE);
        for (String type : List.of("DEPOSIT", "WITHDRAW", "TRANSFER")) {
            var filtered = list("/api/operations?type=" + type);
            assertEquals(1, filtered.size());
            var operation = (Map<?, ?>) filtered.get(0);
            assertEquals(type, operation.get("opType"));
            assertEquals(
                    type,
                    jdbc.queryForObject(
                            "select operation_type from lab2.operations where id = ?",
                            String.class,
                            UUID.fromString(operation.get("id").toString())));
        }
    }

    @Test
    void directedFriendshipRemovalAndAllCommissions() throws Exception {
        User a = user(), b = user();
        var source = account(a);
        var destination = account(b);
        operations.deposit(source.id(), new BigDecimal("100.00"));
        String path = "/api/users/" + a.id() + "/friends/" + b.id();
        assertEquals(204, request("PUT", path, null).status());
        assertEquals(204, request("PUT", path, null).status());
        assertEquals(1, list("/api/users/" + a.id() + "/friends").size());
        assertEquals(0, list("/api/users/" + b.id() + "/friends").size());
        assertEquals(
                new BigDecimal("0.02"),
                operations
                        .transfer(source.id(), destination.id(), new BigDecimal("0.50"))
                        .commission());
        assertEquals(204, request("DELETE", path, null).status());
        assertEquals(204, request("DELETE", path, null).status());
        assertEquals(0, list("/api/users/" + a.id() + "/friends").size());
        assertEquals(
                new BigDecimal("0.05"),
                operations
                        .transfer(source.id(), destination.id(), new BigDecimal("0.50"))
                        .commission());
    }

    @Test
    void httpErrorsHaveCorrectStatuses() throws Exception {
        User user = user();
        Account account = account(user);
        object(request("GET", "/api/users/bad-id", null), 400);
        object(request("GET", "/api/users?gender=BAD", null), 400);
        object(request("GET", "/api/operations?type=BAD", null), 400);
        object(request("GET", "/api/missing", null), 404);
        object(request("POST", "/api/users/" + user.id(), Map.of()), 405);
        object(request("GET", "/api/accounts/" + UUID.randomUUID(), null), 404);
        object(
                request(
                        "POST",
                        "/api/users",
                        Map.of("login", user.login(), "name", "User", "age", 20)),
                409);
        object(
                request("PATCH", "/api/users/" + user.id(), Collections.singletonMap("name", null)),
                400);
        object(request("PATCH", "/api/users/" + user.id(), Map.of("login", "changed")), 400);
        object(
                request(
                        "POST",
                        "/api/accounts/" + account.id() + "/deposits",
                        Map.of("amount", "10.001")),
                400);
        object(
                request(
                        "POST",
                        "/api/accounts/" + account.id() + "/withdrawals",
                        Map.of("amount", "1.00")),
                400);
        object(
                request(
                        "GET",
                        "/api/accounts/"
                                + account.id()
                                + "/operations?from=2026-01-02T00:00:00Z&to=2026-01-01T00:00:00Z",
                        null),
                400);
    }

    @Test
    void failureAfterFlushRollsBackBothBalancesHistoryAndAudit() {
        var owner = user();
        var a = account(owner);
        var b = account(owner);
        operations.deposit(a.id(), new BigDecimal("100.00"));
        int audits = jdbc.queryForObject("select count(*) from lab2.accounts_aud", Integer.class);
        doAnswer(
                        invocation -> {
                            invocation.callRealMethod();
                            entityManager.flush();
                            throw new InvalidOperationException(
                                    "Simulated failure after database writes");
                        })
                .when(operationStore)
                .create(any());
        try {
            assertThrows(
                    InvalidOperationException.class,
                    () -> operations.transfer(a.id(), b.id(), new BigDecimal("10.00")));
        } finally {
            reset(operationStore);
        }
        assertEquals(new BigDecimal("100.00"), accounts.getAccount(a.id()).balance());
        assertEquals(new BigDecimal("0.00"), accounts.getAccount(b.id()).balance());
        assertEquals(1, operations.getOperations(null, null).size());
        assertEquals(
                audits,
                jdbc.queryForObject("select count(*) from lab2.accounts_aud", Integer.class));
        assertEquals(
                1, jdbc.queryForObject("select count(*) from lab2.operations_aud", Integer.class));
    }

    @Test
    void concurrentDepositsAndOppositeTransfersDoNotLoseMoney() throws Exception {
        var owner = user();
        var a = account(owner);
        var b = account(owner);
        try (var executor = Executors.newFixedThreadPool(8)) {
            List<Callable<Void>> deposits = new ArrayList<>();
            for (int i = 0; i < 30; i++)
                deposits.add(
                        () -> {
                            operations.deposit(a.id(), BigDecimal.ONE);
                            return null;
                        });
            for (var future : executor.invokeAll(deposits, 20, TimeUnit.SECONDS)) future.get();
            assertEquals(new BigDecimal("30.00"), accounts.getAccount(a.id()).balance());
            operations.deposit(b.id(), new BigDecimal("30.00"));
            List<Callable<Void>> transfers = new ArrayList<>();
            for (int i = 0; i < 40; i++) {
                UUID from = i % 2 == 0 ? a.id() : b.id(), to = i % 2 == 0 ? b.id() : a.id();
                transfers.add(
                        () -> {
                            operations.transfer(from, to, BigDecimal.ONE);
                            return null;
                        });
            }
            for (var future : executor.invokeAll(transfers, 20, TimeUnit.SECONDS)) future.get();
        }
        assertEquals(new BigDecimal("30.00"), accounts.getAccount(a.id()).balance());
        assertEquals(new BigDecimal("30.00"), accounts.getAccount(b.id()).balance());
        assertEquals(71, operations.getOperations(null, null).size());
    }

    @Test
    void queryCountsStayConstantWhenListsGrow() throws Exception {
        User owner = user();
        var statistics = entityManagerFactory.unwrap(SessionFactory.class).getStatistics();
        for (int size : List.of(2, 8)) {
            for (int i = 0; i < size; i++) {
                User friend = user();
                friends.addFriend(owner.id(), friend.id());
                operations.deposit(account(friend).id(), BigDecimal.ONE);
                operations.deposit(account(owner).id(), BigDecimal.ONE);
            }
            for (var entry :
                    Map.of(
                                    "/api/users",
                                    1L,
                                    "/api/accounts",
                                    1L,
                                    "/api/operations",
                                    1L,
                                    "/api/users/" + owner.id() + "/friends",
                                    2L,
                                    "/api/users/" + owner.id() + "/accounts",
                                    2L)
                            .entrySet()) {
                statistics.clear();
                list(entry.getKey());
                assertEquals(
                        entry.getValue().longValue(),
                        statistics.getPrepareStatementCount(),
                        entry.getKey());
            }
        }
    }

    @Test
    void swaggerDescribesEveryEndpointAndDto() throws Exception {
        assertEquals(200, request("GET", "/swagger-ui/index.html", null).status());
        var spec = object(request("GET", "/v3/api-docs", null), 200);
        var paths = (Map<?, ?>) spec.get("paths");
        int count = 0;
        for (Object value : paths.values()) {
            for (Object operation : ((Map<?, ?>) value).values()) {
                var method = (Map<?, ?>) operation;
                assertNotNull(method.get("summary"));
                var responses = (Map<?, ?>) method.get("responses");
                assertTrue(responses.containsKey("500"));
                assertTrue(
                        responses.containsKey("200")
                                || responses.containsKey("201")
                                || responses.containsKey("204"));
                count++;
            }
        }
        assertEquals(19, count);
        var schemas = (Map<?, ?>) ((Map<?, ?>) spec.get("components")).get("schemas");
        for (String name :
                List.of(
                        "UserCreateRequest",
                        "UserUpdateRequest",
                        "UserPatchRequest",
                        "UserResponse",
                        "AccountCreateRequest",
                        "AccountResponse",
                        "AmountRequest",
                        "TransferRequest",
                        "OperationResponse")) {
            var properties = (Map<?, ?>) ((Map<?, ?>) schemas.get(name)).get("properties");
            assertNotNull(properties, name);
            for (Object property : properties.values())
                assertNotNull(((Map<?, ?>) property).get("description"), name);
        }
    }
}
