package ru.nikita.lab2.service.impl;

import ru.nikita.lab2.service.exception.InvalidAmountException;

import java.math.BigDecimal;

final class Money {
    private static final BigDecimal MAX = new BigDecimal("99999999.99");

    private Money() {}

    static BigDecimal positive(BigDecimal amount) {
        if (amount == null
                || amount.signum() <= 0
                || amount.stripTrailingZeros().scale() > 2
                || amount.compareTo(MAX) > 0)
            throw new InvalidAmountException(
                    "Amount must be positive, at most 99999999.99, with at most two decimal"
                            + " places");
        return amount.setScale(2);
    }

    static BigDecimal balance(BigDecimal amount) {
        if (amount.signum() < 0 || amount.compareTo(MAX) > 0)
            throw new InvalidAmountException("Resulting balance is outside NUMERIC(10,2) range");
        return amount.setScale(2);
    }
}
