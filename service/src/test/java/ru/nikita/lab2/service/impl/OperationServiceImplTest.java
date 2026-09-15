package ru.nikita.lab2.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import ru.nikita.lab2.domain.*;
import ru.nikita.lab2.domain.enumeration.*;
import ru.nikita.lab2.port.*;
import ru.nikita.lab2.service.exception.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;

@ExtendWith(MockitoExtension.class)
class OperationServiceImplTest {
    @Mock AccountStore accounts;
    @Mock UserStore users;
    @Mock OperationStore operations;
    OperationServiceImpl service;
    final UUID a = new UUID(0, 1),
            b = new UUID(0, 2),
            owner = UUID.randomUUID(),
            other = UUID.randomUUID();

    @BeforeEach
    void setup() {
        service = new OperationServiceImpl(accounts, operations, users, new CommissionPolicy());
    }

    Account account(UUID id, UUID user, String balance) {
        return new Account(id, user, new BigDecimal(balance));
    }

    @Test
    void depositStoresBalanceAndHistory() {
        when(accounts.lock(a)).thenReturn(Optional.of(account(a, owner, "10.00")));
        service.deposit(a, new BigDecimal("5.25"));
        verify(accounts).update(account(a, owner, "15.25"));
        verify(operations)
                .create(
                        new Operation(
                                null,
                                a,
                                null,
                                OpType.DEPOSIT,
                                new BigDecimal("5.25"),
                                new BigDecimal("0.00"),
                                null));
    }

    @Test
    void withdrawalStoresBalanceAndHistory() {
        when(accounts.lock(a)).thenReturn(Optional.of(account(a, owner, "10.00")));
        service.withdraw(a, new BigDecimal("10"));
        verify(accounts).update(account(a, owner, "0.00"));
        verify(operations)
                .create(
                        new Operation(
                                null,
                                a,
                                null,
                                OpType.WITHDRAW,
                                new BigDecimal("10.00"),
                                new BigDecimal("0.00"),
                                null));
    }

    @Test
    void insufficientFundsChangesNothing() {
        when(accounts.lock(a)).thenReturn(Optional.of(account(a, owner, "10.00")));
        assertThrows(
                InsufficientFundsException.class,
                () -> service.withdraw(a, new BigDecimal("10.01")));
        verify(accounts, never()).update(any());
        verifyNoInteractions(operations);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"0", "-1", "0.001", "100000000"})
    void invalidAmountsAreRejectedBeforeStorage(String amount) {
        var value = amount == null ? null : new BigDecimal(amount);
        assertThrows(InvalidAmountException.class, () -> service.deposit(a, value));
        assertThrows(InvalidAmountException.class, () -> service.withdraw(a, value));
        assertThrows(InvalidAmountException.class, () -> service.transfer(a, b, value));
        verifyNoInteractions(accounts, operations, users);
    }

    @ParameterizedTest
    @CsvSource({"true,false,0.00,90.00", "false,true,0.30,89.70", "false,false,1.00,89.00"})
    void transferAppliesCommissionAndCreatesOneRecord(
            boolean sameOwner, boolean friend, String fee, String remainder) {
        when(accounts.lock(a)).thenReturn(Optional.of(account(a, owner, "100.00")));
        when(accounts.lock(b))
                .thenReturn(Optional.of(account(b, sameOwner ? owner : other, "0.00")));
        if (!sameOwner) when(users.isFriend(owner, other)).thenReturn(friend);
        service.transfer(a, b, new BigDecimal("10"));
        verify(accounts).update(account(a, owner, remainder));
        verify(accounts).update(account(b, sameOwner ? owner : other, "10.00"));
        verify(operations)
                .create(
                        new Operation(
                                null,
                                a,
                                b,
                                OpType.TRANSFER,
                                new BigDecimal("10.00"),
                                new BigDecimal(fee),
                                null));
        verifyNoMoreInteractions(operations);
    }

    @Test
    void oppositeTransferLocksInSameOrder() {
        when(accounts.lock(a)).thenReturn(Optional.of(account(a, owner, "10.00")));
        when(accounts.lock(b)).thenReturn(Optional.of(account(b, owner, "10.00")));
        service.transfer(b, a, BigDecimal.ONE);
        var order = inOrder(accounts);
        order.verify(accounts).lock(a);
        order.verify(accounts).lock(b);
    }

    @Test
    void sameAccountRejected() {
        assertThrows(InvalidOperationException.class, () -> service.transfer(a, a, BigDecimal.ONE));
        verifyNoInteractions(accounts, operations);
    }

    @Test
    void commissionIncludedInFundsCheck() {
        when(accounts.lock(a)).thenReturn(Optional.of(account(a, owner, "10.00")));
        when(accounts.lock(b)).thenReturn(Optional.of(account(b, other, "0.00")));
        assertThrows(
                InsufficientFundsException.class,
                () -> service.transfer(a, b, new BigDecimal("10.00")));
        verify(accounts, never()).update(any());
        verifyNoInteractions(operations);
    }

    @Test
    void recipientOverflowChangesNothing() {
        when(accounts.lock(a)).thenReturn(Optional.of(account(a, owner, "10.00")));
        when(accounts.lock(b)).thenReturn(Optional.of(account(b, owner, "99999999.99")));
        assertThrows(InvalidAmountException.class, () -> service.transfer(a, b, BigDecimal.ONE));
        verify(accounts, never()).update(any());
        verifyNoInteractions(operations);
    }

    @Test
    void depositOverflowChangesNothing() {
        when(accounts.lock(a)).thenReturn(Optional.of(account(a, owner, "99999999.99")));
        assertThrows(InvalidAmountException.class, () -> service.deposit(a, BigDecimal.ONE));
        verify(accounts, never()).update(any());
        verifyNoInteractions(operations);
    }

    @Test
    void missingAccountIsDomainError() {
        assertThrows(NoAccountFoundException.class, () -> service.deposit(a, BigDecimal.ONE));
        verifyNoInteractions(operations);
    }

    @Test
    void historyValidatesDateOrder() {
        assertThrows(
                InvalidOperationException.class,
                () ->
                        service.getHistory(
                                a,
                                Instant.parse("2026-01-02T00:00:00Z"),
                                Instant.parse("2026-01-01T00:00:00Z")));
        verifyNoInteractions(accounts, operations);
    }

    @Test
    void historyPassesBothBounds() {
        when(accounts.find(a)).thenReturn(Optional.of(account(a, owner, "0.00")));
        var from = Instant.parse("2026-01-01T00:00:00Z");
        var to = from.plusSeconds(10);
        service.getHistory(a, from, to);
        verify(operations).findAll(null, a, from, to);
    }

    @Test
    void operationFiltersAreForwarded() {
        when(accounts.find(a)).thenReturn(Optional.of(account(a, owner, "0.00")));
        service.getOperations(OpType.TRANSFER, a);
        verify(operations).findAll(OpType.TRANSFER, a, null, null);
    }
}
