package ru.nikita.lab2.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record AmountRequest(
        @Schema(description = "Сумма операции", example = "500.0")
                @NotNull
                @Positive
                @Digits(integer = 8, fraction = 2)
                BigDecimal amount) {}
