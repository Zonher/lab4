package ru.nikita.lab2.service;

import ru.nikita.lab2.domain.*;
import ru.nikita.lab2.domain.enumeration.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;

public interface OperationService {
    Operation deposit(UUID accountId, BigDecimal amount);

    Operation withdraw(UUID accountId, BigDecimal amount);

    Operation transfer(UUID fromAccountId, UUID toAccountId, BigDecimal amount);

    List<Operation> getOperations(OpType type, UUID accountId);

    List<Operation> getHistory(UUID accountId, Instant from, Instant to);
}
