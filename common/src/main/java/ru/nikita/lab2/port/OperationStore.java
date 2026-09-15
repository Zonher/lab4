package ru.nikita.lab2.port;

import ru.nikita.lab2.domain.*;
import ru.nikita.lab2.domain.enumeration.*;

import java.time.Instant;
import java.util.*;

public interface OperationStore {
    Operation create(Operation operation);

    List<Operation> findAll(OpType type, UUID accountId, Instant from, Instant to);
}
