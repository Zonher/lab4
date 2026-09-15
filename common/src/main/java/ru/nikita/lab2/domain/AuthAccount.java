package ru.nikita.lab2.domain;

import ru.nikita.lab2.domain.enumeration.Role;

import java.util.UUID;

public record AuthAccount(
        UUID id,
        String login,
        String passwordHash,
        Role role,
        UUID userId,
        boolean enabled
) {
}
