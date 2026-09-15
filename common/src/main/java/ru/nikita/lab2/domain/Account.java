package ru.nikita.lab2.domain;

import java.math.BigDecimal;
import java.util.UUID;

public record Account(UUID id, UUID userId, BigDecimal balance) {
    public Account withBalance(BigDecimal value) {
        return new Account(id, userId, value);
    }
}
