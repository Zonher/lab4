package ru.nikita.lab2.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import ru.nikita.lab2.application.dto.enumeration.Gender;
import ru.nikita.lab2.application.dto.enumeration.HairColor;

import java.util.UUID;

public record UserResponse(
        @Schema(description = "ID юзера") UUID id,
        @Schema(description = "Логин юзера") String login,
        @Schema(description = "Имя юзера") String name,
        @Schema(description = "Возраст юзера") int age,
        @Schema(description = "Пол юзера") Gender gender,
        @Schema(description = "Цвет волос") HairColor hairColor) {}
