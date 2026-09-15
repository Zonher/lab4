package ru.nikita.lab2.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.UUID;

public record TransferRequest(
        @Schema(description = "ID счёта отправителя") @NotNull UUID fromAccountId,
        @Schema(description = "ID счёта получателя") @NotNull UUID toAccountId,
        @Schema(description = "Сумма перевода без комиссии", example = "100.00")
                @NotNull
                @Positive
                @Digits(integer = 8, fraction = 2)
                BigDecimal amount) {}
