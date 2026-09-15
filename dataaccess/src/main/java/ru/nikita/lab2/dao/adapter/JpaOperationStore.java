package ru.nikita.lab2.dao.adapter;

import jakarta.persistence.criteria.Predicate;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import ru.nikita.lab2.dao.entity.OperationEntity;
import ru.nikita.lab2.dao.mapper.EntityEnumMapper;
import ru.nikita.lab2.dao.mapper.EntityMapper;
import ru.nikita.lab2.dao.repository.*;
import ru.nikita.lab2.domain.Operation;
import ru.nikita.lab2.domain.enumeration.OpType;
import ru.nikita.lab2.port.OperationStore;

import java.time.Instant;
import java.util.*;

@Repository
public class JpaOperationStore implements OperationStore {
    private final OperationRepository operations;
    private final AccountRepository accounts;

    public JpaOperationStore(OperationRepository operations, AccountRepository accounts) {
        this.operations = operations;
        this.accounts = accounts;
    }

    public Operation create(Operation operation) {
        var account = accounts.getReferenceById(operation.accountId());
        OperationEntity entity =
                switch (EntityEnumMapper.toEntity(operation.opType())) {
                    case DEPOSIT -> OperationEntity.deposit(account, operation.amount());
                    case WITHDRAW -> OperationEntity.withdraw(account, operation.amount());
                    case TRANSFER ->
                            OperationEntity.transfer(
                                    account,
                                    accounts.getReferenceById(operation.destinationId()),
                                    operation.amount(),
                                    operation.commission());
                };
        return EntityMapper.operation(operations.save(entity));
    }

    public List<Operation> findAll(OpType type, UUID accountId, Instant from, Instant to) {
        return operations
                .findAll(
                        (root, query, cb) -> {
                            List<Predicate> filters = new ArrayList<>();
                            if (type != null)
                                filters.add(
                                        cb.equal(
                                                root.get("opType"),
                                                EntityEnumMapper.toEntity(type)));
                            if (accountId != null)
                                filters.add(
                                        cb.or(
                                                cb.equal(root.get("account").get("id"), accountId),
                                                cb.equal(
                                                        root.get("destination").get("id"),
                                                        accountId)));
                            if (from != null)
                                filters.add(cb.greaterThanOrEqualTo(root.get("operationAt"), from));
                            if (to != null)
                                filters.add(cb.lessThanOrEqualTo(root.get("operationAt"), to));
                            return cb.and(filters.toArray(Predicate[]::new));
                        },
                        Sort.by("operationAt", "id"))
                .stream()
                .map(EntityMapper::operation)
                .toList();
    }
}
