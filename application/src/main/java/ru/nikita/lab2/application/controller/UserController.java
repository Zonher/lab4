package ru.nikita.lab2.application.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.*;

import jakarta.validation.Valid;

import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import ru.nikita.lab2.application.dto.*;
import ru.nikita.lab2.application.dto.enumeration.*;
import ru.nikita.lab2.application.mapper.ApiEnumMapper;
import ru.nikita.lab2.application.mapper.ApiMapper;
import ru.nikita.lab2.application.security.SecurityPrincipal;
import ru.nikita.lab2.service.*;

import java.net.URI;
import java.util.*;

@RestController
@RequestMapping(value = "/api/users", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserController {

    private final UserCRUDService users;
    private final AccountCRUDService accounts;
    private final FriendService friends;
    private final ApiMapper mapper;
    private final AuthAccountService authAccounts;
    private final PasswordEncoder passwordEncoder;

    public UserController(
            UserCRUDService users,
            AccountCRUDService accounts,
            FriendService friends,
            ApiMapper mapper, AuthAccountService authAccounts,
            PasswordEncoder passwordEncoder) {
        this.users = users;
        this.accounts = accounts;
        this.friends = friends;
        this.mapper = mapper;
        this.authAccounts = authAccounts;
        this.passwordEncoder = passwordEncoder;
    }

    @Operation(summary = "Создать пользователя")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Пользователь создан"),
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
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserCreateRequest request) {
        var created = authAccounts.createClient(
                mapper.toDomain(request),
                passwordEncoder.encode(request.password())
        );

        var user = mapper.toResponse(created);

        return ResponseEntity
                .created(URI.create("/api/users/" + user.id()))
                .body(user);
    }

    @Operation(summary = "Получить пользователя")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Пользователь найден"),
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
    @GetMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse getUser(@PathVariable UUID userId) {
        return mapper.toResponse(users.getUser(userId));
    }

    @Operation(summary = "Получить пользователей с фильтрацией по цвету волос и полу")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Список пользователей"),
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
    public List<UserResponse> getUsers(
            @RequestParam(required = false) HairColor hairColor,
            @RequestParam(required = false) Gender gender) {
        return users
                .getUsers(ApiEnumMapper.toDomain(hairColor), ApiEnumMapper.toDomain(gender))
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Operation(summary = "Полностью обновить изменяемые поля пользователя")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Пользователь обновлён"),
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
    @PutMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse updateUser(
            @PathVariable UUID userId, @Valid @RequestBody UserUpdateRequest request) {
        return mapper.toResponse(users.updateUser(mapper.toDomain(userId, request)));
    }

    @Operation(summary = "Частично обновить пользователя; null очищает пол или цвет волос")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Пользователь обновлён"),
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
    @PatchMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse patchUser(
            @PathVariable UUID userId, @RequestBody UserPatchRequest request) {
        return mapper.toResponse(users.patchUser(mapper.toDomain(userId, request)));
    }

    @Operation(summary = "Удалить пользователя вместе с его счетами")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Пользователь удалён"),
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
    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteUser(@PathVariable UUID userId) {
        users.removeUser(userId);
    }

    @Operation(summary = "Получить счета пользователя")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Список счетов пользователя"),
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
    @GetMapping("/{userId}/accounts")
    @PreAuthorize("hasRole('ADMIN')")
    public List<AccountResponse> getAccountsByUserId(@PathVariable UUID userId) {
        return accounts.getAccountsByUserId(userId).stream().map(mapper::toResponse).toList();
    }

    @Operation(summary = "Получить друзей пользователя")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Список друзей"),
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
    @GetMapping("/{userId}/friends")
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponse> getFriends(@PathVariable UUID userId) {
        return friends.getFriends(userId).stream().map(mapper::toResponse).toList();
    }

    @Operation(summary = "Добавить друга в направленный список пользователя")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Друг добавлен"),
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
    @PutMapping("/{userId}/friends/{friendId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('CLIENT')")
    public void addFriend(@PathVariable UUID userId, @PathVariable UUID friendId,
                          @AuthenticationPrincipal SecurityPrincipal principal) {
        if (!userId.equals(principal.userId())){
            throw new AuthorizationDeniedException("Forbidden");
        }
        friends.addFriend(userId, friendId);

    }

    @Operation(summary = "Удалить друга из списка пользователя")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Друг удалён"),
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
    @DeleteMapping("/{userId}/friends/{friendId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('CLIENT')")
    public void removeFriend(
            @PathVariable UUID userId,
            @PathVariable UUID friendId,
            @AuthenticationPrincipal SecurityPrincipal principal) {
        if(!users.equals(principal.userId())){
            throw new AuthorizationDeniedException("Forbidden");
        }
        friends.removeFriend(userId, friendId);
    }

    @Operation(summary = "Получить информацию о себе")
    @GetMapping("/me")
    @PreAuthorize("hasRole('CLIENT')")
    public UserResponse getMe(
            @AuthenticationPrincipal SecurityPrincipal principal){
        return mapper.toResponse(
                users.getUser(principal.userId())
        );
    }

    @Operation(summary = "Получить свои счета")
    @GetMapping("/me/accounts")
    @PreAuthorize("hasRole('CLIENT')")
    public List<AccountResponse> getMyAccounts(
            @AuthenticationPrincipal SecurityPrincipal principal
    ){
        return accounts.getAccountsByUserId(principal.userId())
                .stream()
                .map(mapper::toResponse)
                .toList();
    }
}
