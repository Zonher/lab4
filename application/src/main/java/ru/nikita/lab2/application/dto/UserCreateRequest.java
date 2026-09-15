package ru.nikita.lab2.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import ru.nikita.lab2.application.dto.enumeration.Gender;
import ru.nikita.lab2.application.dto.enumeration.HairColor;

public record UserCreateRequest(
        @Schema(description = "Логин юзера", example = "nikita") @NotBlank @Size(max = 20)
                String login,
        @Schema(description = "Пароль юзера", example = "password123", accessMode = Schema.AccessMode.WRITE_ONLY)
        @NotBlank
        String password,
        @Schema(description = "Имя юзера", example = "Nikita") @NotBlank @Size(max = 50)
                String name,
        @Schema(description = "Возраст юзера", example = "20") @NotNull @Min(1) Integer age,
        @Schema(description = "Пол юзера") Gender gender,
        @Schema(description = "Цвет волос") HairColor hairColor) {}
