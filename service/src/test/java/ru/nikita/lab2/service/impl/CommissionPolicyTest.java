package ru.nikita.lab2.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;
import org.mockito.*;

import ru.nikita.lab2.domain.*;
import ru.nikita.lab2.domain.enumeration.*;
import ru.nikita.lab2.port.*;
import ru.nikita.lab2.service.exception.*;

import java.math.BigDecimal;
import java.util.*;

class CommissionPolicyTest {
    @ParameterizedTest
    @CsvSource({
        "0.05,false,false,0.01",
        "0.50,false,true,0.02",
        "10.05,false,true,0.30",
        "10.05,false,false,1.01",
        "10.05,true,true,0.00"
    })
    void roundsHalfUp(String amount, boolean sameOwner, boolean friend, String expected) {
        assertEquals(
                new BigDecimal(expected),
                new CommissionPolicy().commission(new BigDecimal(amount), sameOwner, friend));
    }
}
