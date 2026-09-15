package ru.nikita.lab2.port;

import ru.nikita.lab2.domain.*;
import ru.nikita.lab2.domain.enumeration.*;

import java.util.*;

public interface AccountStore {
    Optional<Account> find(UUID id);

    Optional<Account> lock(UUID id);

    List<Account> findAll();

    List<Account> findByUser(UUID userId);

    Account create(UUID userId);

    void update(Account account);

    void delete(UUID id);
}
