package ru.nikita.lab2.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.nikita.lab2.domain.Account;
import ru.nikita.lab2.port.*;
import ru.nikita.lab2.service.AccountCRUDService;
import ru.nikita.lab2.service.exception.*;

import java.util.*;

@Service
@Transactional(readOnly = true)
public class AccountServiceImpl implements AccountCRUDService {
    private final AccountStore accounts;
    private final UserStore users;

    public AccountServiceImpl(AccountStore accounts, UserStore users) {
        this.accounts = accounts;
        this.users = users;
    }

    @Transactional
    public Account createAccount(UUID userId) {
        users.lock(userId).orElseThrow(() -> new NoUserFoundException(userId));
        return accounts.create(userId);
    }

    @Transactional
    public void removeAccount(UUID id) {
        var account = accounts.lock(id).orElseThrow(() -> new NoAccountFoundException(id));
        if (account.balance().signum() != 0) throw new AccountNotEmptyException(id);
        accounts.delete(id);
    }

    public Account getAccount(UUID id) {
        return accounts.find(id).orElseThrow(() -> new NoAccountFoundException(id));
    }

    public List<Account> getAccounts() {
        return accounts.findAll();
    }

    public List<Account> getAccountsByUserId(UUID id) {
        users.find(id).orElseThrow(() -> new NoUserFoundException(id));
        return accounts.findByUser(id);
    }
}
