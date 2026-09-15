package ru.nikita.lab2.domain;

import ru.nikita.lab2.domain.enumeration.OpType;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record Operation(
        UUID id,
        UUID accountId,
        UUID destinationId,
        OpType opType,
        BigDecimal amount,
        BigDecimal commission,
        Instant operationInstant) {}
