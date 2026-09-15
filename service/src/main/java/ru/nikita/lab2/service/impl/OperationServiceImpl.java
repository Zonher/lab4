package ru.nikita.lab2.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.nikita.lab2.domain.*;
import ru.nikita.lab2.domain.enumeration.OpType;
import ru.nikita.lab2.port.*;
import ru.nikita.lab2.service.OperationService;
import ru.nikita.lab2.service.exception.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;

@Service
@Transactional(readOnly = true)
public class OperationServiceImpl implements OperationService {
    private final AccountStore accounts;
    private final OperationStore operations;
    private final UserStore users;
    private final CommissionPolicy policy;

    public OperationServiceImpl(
            AccountStore accounts,
            OperationStore operations,
            UserStore users,
            CommissionPolicy policy) {
        this.accounts = accounts;
        this.operations = operations;
        this.users = users;
        this.policy = policy;
    }

    @Transactional
    public Operation deposit(UUID id, BigDecimal amount) {
        var sum = Money.positive(amount);
        var account = lock(id);
        accounts.update(account.withBalance(Money.balance(account.balance().add(sum))));
        return record(id, null, OpType.DEPOSIT, sum, new BigDecimal("0.00"));
    }

    @Transactional
    public Operation withdraw(UUID id, BigDecimal amount) {
        var sum = Money.positive(amount);
        var account = lock(id);
        requireFunds(account, sum);
        accounts.update(account.withBalance(account.balance().subtract(sum)));
        return record(id, null, OpType.WITHDRAW, sum, new BigDecimal("0.00"));
    }

    @Transactional
    public Operation transfer(UUID fromId, UUID toId, BigDecimal amount) {
        var sum = Money.positive(amount);
        if (fromId == null || toId == null || fromId.equals(toId))
            throw new InvalidOperationException("Transfer requires two different account IDs");
        // Одинаковый порядок блокировок предотвращает deadlock встречных переводов.
        Account first = lock(fromId.compareTo(toId) < 0 ? fromId : toId);
        Account second = lock(fromId.compareTo(toId) < 0 ? toId : fromId);
        Account from = first.id().equals(fromId) ? first : second;
        Account to = first.id().equals(toId) ? first : second;
        boolean sameOwner = from.userId().equals(to.userId());
        var commission =
                policy.commission(
                        sum, sameOwner, !sameOwner && users.isFriend(from.userId(), to.userId()));
        var debit = sum.add(commission);
        requireFunds(from, debit);
        var credited = Money.balance(to.balance().add(sum));
        accounts.update(from.withBalance(from.balance().subtract(debit)));
        accounts.update(to.withBalance(credited));
        return record(fromId, toId, OpType.TRANSFER, sum, commission);
    }

    public List<Operation> getOperations(OpType type, UUID accountId) {
        if (accountId != null) requireAccount(accountId);
        return operations.findAll(type, accountId, null, null);
    }

    public List<Operation> getHistory(UUID id, Instant from, Instant to) {
        if (from != null && to != null && from.isAfter(to))
            throw new InvalidOperationException("from must not be after to");
        requireAccount(id);
        return operations.findAll(null, id, from, to);
    }

    private Operation record(
            UUID accountId,
            UUID destinationId,
            OpType type,
            BigDecimal sum,
            BigDecimal commission) {
        return operations.create(
                new Operation(null, accountId, destinationId, type, sum, commission, null));
    }

    private Account lock(UUID id) {
        if (id == null) throw new InvalidOperationException("Account ID is required");
        return accounts.lock(id).orElseThrow(() -> new NoAccountFoundException(id));
    }

    private void requireAccount(UUID id) {
        if (id == null) throw new InvalidOperationException("Account ID is required");
        accounts.find(id).orElseThrow(() -> new NoAccountFoundException(id));
    }

    private void requireFunds(Account account, BigDecimal amount) {
        if (account.balance().compareTo(amount) < 0)
            throw new InsufficientFundsException(account.id());
    }
}
