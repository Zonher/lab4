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
import org.springframework.web.bind.annotation.*;

import ru.nikita.lab2.application.dto.*;
import ru.nikita.lab2.application.dto.enumeration.*;
import ru.nikita.lab2.application.mapper.ApiEnumMapper;
import ru.nikita.lab2.application.mapper.ApiMapper;
import ru.nikita.lab2.application.security.SecurityPrincipal;
import ru.nikita.lab2.service.*;

import java.util.*;

@RestController
@RequestMapping(value = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
public class OperationController {

    private final OperationService operations;
    private final ApiMapper mapper;
    private final AccountCRUDService accounts;

    public OperationController(OperationService operations, ApiMapper mapper, AccountCRUDService accounts) {
        this.operations = operations;
        this.mapper = mapper;
        this.accounts = accounts;
    }

    @Operation(summary = "Перевести деньги: комиссия 0% своим счетам, 3% другу, 10% остальным")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Перевод выполнен"),
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
    @PostMapping("/transfers")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('CLIENT')")
    public OperationResponse transfer(@Valid @RequestBody TransferRequest request,
                                      @AuthenticationPrincipal SecurityPrincipal principal) {
        var fromAccount = accounts.getAccount(request.fromAccountId());

        if (!fromAccount.userId().equals(principal.userId())){
            throw new AuthorizationDeniedException("Forbidden");
        }
        return mapper.toResponse(
                operations.transfer(
                        request.fromAccountId(), request.toAccountId(), request.amount()));
    }

    @Operation(summary = "Получить операции с фильтрами по типу и любой стороне перевода")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Список операций"),
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
    @GetMapping("/operations")
    @PreAuthorize("hasRole('ADMIN')")
    public List<OperationResponse> getOperations(
            @RequestParam(required = false) OpType type,
            @RequestParam(required = false) UUID accountId) {
        return operations.getOperations(ApiEnumMapper.toDomain(type), accountId).stream()
                .map(mapper::toResponse)
                .toList();
    }
}
