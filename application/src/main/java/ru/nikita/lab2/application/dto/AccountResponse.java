package ru.nikita.lab2.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.UUID;

public record AccountResponse(
        @Schema(description = "ID счёта") UUID id,
        @Schema(description = "ID владельца счёта") UUID userId,
        @Schema(description = "Баланс счёта", example = "1000.0") BigDecimal balance) {}
