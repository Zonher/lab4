package ru.nikita.lab2.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record ErrorResponse(
        @Schema(description = "HTTP статус", example = "404") int status,
        @Schema(description = "Описание ошибки", example = "User not found") String message) {}
