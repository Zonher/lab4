package ru.nikita.lab2.service;

import ru.nikita.lab2.domain.AuthAccount;
import ru.nikita.lab2.domain.User;

import java.util.Optional;
import java.util.UUID;

public interface AuthAccountService {
    Optional<AuthAccount> findByLogin(String login);
    User createClient(User user, String passwordHash);
    AuthAccount createAdmin(String login, String passwordHash);
    boolean hasAdmin();
    void activateClient(UUID userId);
    void deactivateClient(UUID userId);
}
