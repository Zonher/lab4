package ru.nikita.lab2.dao.adapter;

import org.springframework.stereotype.Repository;

import ru.nikita.lab2.dao.entity.AccountEntity;
import ru.nikita.lab2.dao.mapper.EntityMapper;
import ru.nikita.lab2.dao.repository.*;
import ru.nikita.lab2.domain.Account;
import ru.nikita.lab2.port.AccountStore;

import java.util.*;

@Repository
public class JpaAccountStore implements AccountStore {
    private final AccountRepository accounts;
    private final UserRepository users;

    public JpaAccountStore(AccountRepository accounts, UserRepository users) {
        this.accounts = accounts;
        this.users = users;
    }

    public Optional<Account> find(UUID id) {
        return accounts.findById(id).map(EntityMapper::account);
    }

    public Optional<Account> lock(UUID id) {
        return accounts.lock(id).map(EntityMapper::account);
    }

    public List<Account> findAll() {
        return accounts.findAllByOrderById().stream().map(EntityMapper::account).toList();
    }

    public List<Account> findByUser(UUID id) {
        return accounts.findByUser_IdOrderById(id).stream().map(EntityMapper::account).toList();
    }

    public Account create(UUID userId) {
        return EntityMapper.account(
                accounts.save(new AccountEntity(users.getReferenceById(userId))));
    }

    public void update(Account account) {
        accounts.getReferenceById(account.id()).setBalance(account.balance());
    }

    public void delete(UUID id) {
        accounts.delete(accounts.getReferenceById(id));
    }
}
