package ru.nikita.lab2.application.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.*;

import jakarta.validation.Valid;

import org.springframework.http.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import ru.nikita.lab2.application.dto.*;
import ru.nikita.lab2.application.dto.enumeration.*;
import ru.nikita.lab2.application.mapper.ApiMapper;
import ru.nikita.lab2.application.security.SecurityPrincipal;
import ru.nikita.lab2.domain.enumeration.Role;
import ru.nikita.lab2.service.*;

import java.net.URI;
import java.time.Instant;
import java.util.*;

@RestController
@RequestMapping(value = "/api/accounts", produces = MediaType.APPLICATION_JSON_VALUE)
public class AccountController {

    private final AccountCRUDService accounts;
    private final OperationService operations;
    private final ApiMapper mapper;

    public AccountController(
            AccountCRUDService accounts, OperationService operations, ApiMapper mapper) {
        this.accounts = accounts;
        this.operations = operations;
        this.mapper = mapper;
    }

    @Operation(summary = "Создать счёт")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Счёт создан"),
        @ApiResponse(
                responseCode = "400",
                description = "Некорректный запрос",
                content =
                        @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = ErrorResponse.class),
                                examples =
                                        @ExampleObject(
                                                value =
                                                        "{\"status\": 400, \"message\": \"Bad"
                                                                + " Request\"}"))),
        @ApiResponse(
                responseCode = "404",
                description = "Ресурс не найден",
                content =
                        @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = ErrorResponse.class),
                                examples =
                                        @ExampleObject(
                                                value =
                                                        "{\"status\": 404, \"message\": \"Not"
                                                                + " Found\"}"))),
        @ApiResponse(
                responseCode = "409",
                description = "Конфликт с текущим состоянием данных",
                content =
                        @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = ErrorResponse.class),
                                examples =
                                        @ExampleObject(
                                                value =
                                                        "{\"status\": 409, \"message\": \"Data"
                                                                + " conflict; check the current"
                                                                + " resource state\"}"))),
        @ApiResponse(
                responseCode = "500",
                description = "Ошибка сервера",
                content =
                        @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = ErrorResponse.class),
                                examples =
                                        @ExampleObject(
                                                value =
                                                        "{\"status\": 500, \"message\": \"Internal"
                                                                + " server error\"}")))
    })
    @PostMapping
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<AccountResponse> createAccount(
            @Valid @RequestBody AccountCreateRequest request,
            @AuthenticationPrincipal SecurityPrincipal principal) {
        if (!request.userId().equals(principal.userId())){
            throw new AuthorizationDeniedException("Forbidden");
        }

        var account = mapper.toResponse(accounts.createAccount(request.userId()));

        return ResponseEntity
                .created(URI.create("/api/accounts/" + account.id()))
                .body(account);
    }

    @Operation(summary = "Получить все счета")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Список счетов"),
        @ApiResponse(
                responseCode = "500",
                description = "Ошибка сервера",
                content =
                        @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = ErrorResponse.class),
                                examples =
                                        @ExampleObject(
                                                value =
                                                        "{\"status\": 500, \"message\": \"Internal"
                                                                + " server error\"}")))
    })
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<AccountResponse> getAccounts() {
        return accounts.getAccounts().stream().map(mapper::toResponse).toList();
    }

    @Operation(summary = "Получить счёт и его баланс")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Счёт найден"),
        @ApiResponse(
                responseCode = "400",
                description = "Некорректный запрос",
                content =
                        @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = ErrorResponse.class),
                                examples =
                                        @ExampleObject(
                                                value =
                                                        "{\"status\": 400, \"message\": \"Bad"
                                                                + " Request\"}"))),
        @ApiResponse(
                responseCode = "404",
                description = "Ресурс не найден",
                content =
                        @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = ErrorResponse.class),
                                examples =
                                        @ExampleObject(
                                                value =
                                                        "{\"status\": 404, \"message\": \"Not"
                                                                + " Found\"}"))),
        @ApiResponse(
                responseCode = "500",
                description = "Ошибка сервера",
                content =
                        @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = ErrorResponse.class),
                                examples =
                                        @ExampleObject(
                                                value =
                                                        "{\"status\": 500, \"message\": \"Internal"
                                                                + " server error\"}")))
    })
    @GetMapping("/{accountId}")
    public AccountResponse getAccount(@PathVariable UUID accountId,
                                      @AuthenticationPrincipal SecurityPrincipal principal) {
        var account = accounts.getAccount(accountId);

        if (principal.role() == Role.CLIENT && !account.userId().equals(principal.userId())){
            throw new AuthorizationDeniedException("Forbidden");
        }
        return mapper.toResponse(account);
    }

    @Operation(summary = "Удалить счёт с нулевым балансом")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Счёт удалён"),
        @ApiResponse(
                responseCode = "400",
                description = "Некорректный запрос",
                content =
                        @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = ErrorResponse.class),
                                examples =
                                        @ExampleObject(
                                                value =
                                                        "{\"status\": 400, \"message\": \"Bad"
                                                                + " Request\"}"))),
        @ApiResponse(
                responseCode = "404",
                description = "Ресурс не найден",
                content =
                        @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = ErrorResponse.class),
                                examples =
                                        @ExampleObject(
                                                value =
                                                        "{\"status\": 404, \"message\": \"Not"
                                                                + " Found\"}"))),
        @ApiResponse(
                responseCode = "409",
                description = "Конфликт с текущим состоянием данных",
                content =
                        @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = ErrorResponse.class),
                                examples =
                                        @ExampleObject(
                                                value =
                                                        "{\"status\": 409, \"message\": \"Data"
                                                                + " conflict; check the current"
                                                                + " resource state\"}"))),
        @ApiResponse(
                responseCode = "500",
                description = "Ошибка сервера",
                content =
                        @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = ErrorResponse.class),
                                examples =
                                        @ExampleObject(
                                                value =
                                                        "{\"status\": 500, \"message\": \"Internal"
                                                                + " server error\"}")))
    })
    @DeleteMapping("/{accountId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('CLIENT')")
    public void deleteAccount(@PathVariable UUID accountId,
                              @AuthenticationPrincipal SecurityPrincipal principal) {
        var account = accounts.getAccount(accountId);

        if (!account.userId().equals(principal.userId())){
            throw new AuthorizationDeniedException("Forbidden");
        }

        accounts.removeAccount(accountId);
    }

    @Operation(summary = "Пополнить счёт и записать операцию")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Пополнение выполнено"),
        @ApiResponse(
                responseCode = "400",
                description = "Некорректный запрос",
                content =
                        @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = ErrorResponse.class),
                                examples =
                                        @ExampleObject(
                                                value =
                                                        "{\"status\": 400, \"message\": \"Bad"
                                                                + " Request\"}"))),
        @ApiResponse(
                responseCode = "404",
                description = "Ресурс не найден",
                content =
                        @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = ErrorResponse.class),
                                examples =
                                        @ExampleObject(
                                                value =
                                                        "{\"status\": 404, \"message\": \"Not"
                                                                + " Found\"}"))),
        @ApiResponse(
                responseCode = "409",
                description = "Конфликт с текущим состоянием данных",
                content =
                        @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = ErrorResponse.class),
                                examples =
                                        @ExampleObject(
                                                value =
                                                        "{\"status\": 409, \"message\": \"Data"
                                                                + " conflict; check the current"
                                                                + " resource state\"}"))),
        @ApiResponse(
                responseCode = "500",
                description = "Ошибка сервера",
                content =
                        @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = ErrorResponse.class),
                                examples =
                                        @ExampleObject(
                                                value =
                                                        "{\"status\": 500, \"message\": \"Internal"
                                                                + " server error\"}")))
    })
    @PostMapping("/{accountId}/deposits")
    @ResponseStatus(HttpStatus.CREATED)
    public OperationResponse deposit(
            @PathVariable UUID accountId, @Valid @RequestBody AmountRequest request,
            @AuthenticationPrincipal SecurityPrincipal principal) {
        var account = accounts.getAccount(accountId);

        if (!account.userId().equals(principal.userId())){
            throw new AuthorizationDeniedException("Forbidden");
        }
        return mapper.toResponse(operations.deposit(accountId, request.amount()));

    }

    @Operation(summary = "Снять деньги и записать операцию")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Снятие выполнено"),
        @ApiResponse(
                responseCode = "400",
                description = "Некорректный запрос",
                content =
                        @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = ErrorResponse.class),
                                examples =
                                        @ExampleObject(
                                                value =
                                                        "{\"status\": 400, \"message\": \"Bad"
                                                                + " Request\"}"))),
        @ApiResponse(
                responseCode = "404",
                description = "Ресурс не найден",
                content =
                        @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = ErrorResponse.class),
                                examples =
                                        @ExampleObject(
                                                value =
                                                        "{\"status\": 404, \"message\": \"Not"
                                                                + " Found\"}"))),
        @ApiResponse(
                responseCode = "409",
                description = "Конфликт с текущим состоянием данных",
                content =
                        @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = ErrorResponse.class),
                                examples =
                                        @ExampleObject(
                                                value =
                                                        "{\"status\": 409, \"message\": \"Data"
                                                                + " conflict; check the current"
                                                                + " resource state\"}"))),
        @ApiResponse(
                responseCode = "500",
                description = "Ошибка сервера",
                content =
                        @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = ErrorResponse.class),
                                examples =
                                        @ExampleObject(
                                                value =
                                                        "{\"status\": 500, \"message\": \"Internal"
                                                                + " server error\"}")))
    })
    @PostMapping("/{accountId}/withdrawals")
    @ResponseStatus(HttpStatus.CREATED)
    public OperationResponse withdraw(
            @PathVariable UUID accountId, @Valid @RequestBody AmountRequest request,
            @AuthenticationPrincipal SecurityPrincipal principal) {
        var account = accounts.getAccount(accountId);

        if (!account.userId().equals(principal.userId())){
            throw new AuthorizationDeniedException("Forbidden");
        }
        return mapper.toResponse(
                operations.withdraw(accountId, request.amount())
        );

    }

    @Operation(
            summary = "Получить историю счёта, включая входящие переводы; границы времени включены")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "История операций"),
        @ApiResponse(
                responseCode = "400",
                description = "Некорректный запрос",
                content =
                        @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = ErrorResponse.class),
                                examples =
                                        @ExampleObject(
                                                value =
                                                        "{\"status\": 400, \"message\": \"Bad"
                                                                + " Request\"}"))),
        @ApiResponse(
                responseCode = "404",
                description = "Ресурс не найден",
                content =
                        @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = ErrorResponse.class),
                                examples =
                                        @ExampleObject(
                                                value =
                                                        "{\"status\": 404, \"message\": \"Not"
                                                                + " Found\"}"))),
        @ApiResponse(
                responseCode = "500",
                description = "Ошибка сервера",
                content =
                        @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = ErrorResponse.class),
                                examples =
                                        @ExampleObject(
                                                value =
                                                        "{\"status\": 500, \"message\": \"Internal"
                                                                + " server error\"}")))
    })
    @GetMapping("/{accountId}/operations")
    public List<OperationResponse> history(
            @PathVariable UUID accountId,
            @RequestParam(required = false) Instant from,
            @RequestParam(required = false) Instant to,
            @AuthenticationPrincipal SecurityPrincipal principal) {
        var account = accounts.getAccount(accountId);

        if (principal.role() == Role.CLIENT &&
        !account.userId().equals((principal.userId()))){
            throw new AccessDeniedException("Forbidden");
        }
        return operations.getHistory(accountId, from, to)
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Operation(summary = "Получить счёт вместе с операциями")
    @GetMapping("/{accountId}/details")
    @PreAuthorize("hasRole('ADMIN')")
    public AccountWithOperationsResponse getAccountWithOperations(
            @PathVariable UUID accountId
    ){
        var account = mapper.toResponse(accounts.getAccount(accountId));

        var accountOperations = operations
                .getHistory(accountId, null, null)
                .stream()
                .map(mapper::toResponse)
                .toList();
        return new AccountWithOperationsResponse(
                account,
                accountOperations
        );

    }


}
