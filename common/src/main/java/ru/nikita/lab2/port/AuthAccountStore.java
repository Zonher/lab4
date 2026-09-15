package ru.nikita.lab2.port;

import ru.nikita.lab2.domain.AuthAccount;
import ru.nikita.lab2.domain.enumeration.Role;

import java.util.Optional;
import java.util.UUID;

public interface AuthAccountStore {
    Optional<AuthAccount> findByLogin(String login);
    boolean existsByLogin(String login);
    boolean existsByRole(Role role);
    boolean existsByUserId(UUID userId);
    AuthAccount create(AuthAccount account);
    void setEnabledByUserId(UUID userId, boolean enabled);
}
