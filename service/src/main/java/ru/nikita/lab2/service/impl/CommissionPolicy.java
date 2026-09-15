package ru.nikita.lab2.service.impl;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class CommissionPolicy {
    public BigDecimal commission(BigDecimal amount, boolean sameOwner, boolean friend) {
        var rate = sameOwner ? BigDecimal.ZERO : new BigDecimal(friend ? "0.03" : "0.10");
        return amount.multiply(rate).setScale(2, RoundingMode.HALF_UP);
    }
}
