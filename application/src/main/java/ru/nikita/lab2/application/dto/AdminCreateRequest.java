package ru.nikita.lab2.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AdminCreateRequest(
        @NotBlank
        @Size(max = 20)
        String login,

        @NotBlank
        String password) {}
