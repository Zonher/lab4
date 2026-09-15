package ru.nikita.lab2.service;

import ru.nikita.lab2.domain.*;
import ru.nikita.lab2.domain.enumeration.*;

import java.util.*;

public interface AccountCRUDService {
    Account createAccount(UUID userId);

    void removeAccount(UUID accountId);

    Account getAccount(UUID accountId);

    List<Account> getAccounts();

    List<Account> getAccountsByUserId(UUID userId);
}
