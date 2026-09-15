package ru.nikita.lab2.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import ru.nikita.lab2.application.dto.enumeration.OpType;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record OperationResponse(
        @Schema(description = "ID операции") UUID id,
        @Schema(description = "ID счёта") UUID accountId,
        @Schema(description = "ID счёта получателя перевода; null для пополнения и снятия")
                UUID destinationId,
        @Schema(description = "Тип операции") OpType opType,
        @Schema(description = "Сумма операции") BigDecimal amount,
        @Schema(description = "Комиссия") BigDecimal commission,
        @Schema(description = "Время операции") Instant operationInstant) {}
