package ru.nikita.lab2.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.provider.*;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import ru.nikita.lab2.domain.*;
import ru.nikita.lab2.domain.enumeration.*;
import ru.nikita.lab2.port.*;
import ru.nikita.lab2.service.exception.*;

import java.math.BigDecimal;
import java.util.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceImplTest {
    @Mock AccountStore accounts;
    @Mock UserStore users;
    AccountServiceImpl service;
    UUID id = UUID.randomUUID(), owner = UUID.randomUUID();

    @BeforeEach
    void setup() {
        service = new AccountServiceImpl(accounts, users);
    }

    @Test
    void nonzeroAccountCannotBeDeleted() {
        when(accounts.lock(id)).thenReturn(Optional.of(new Account(id, owner, BigDecimal.ONE)));
        assertThrows(AccountNotEmptyException.class, () -> service.removeAccount(id));
        verify(accounts, never()).delete(any());
    }

    @Test
    void zeroAccountCanBeDeleted() {
        when(accounts.lock(id)).thenReturn(Optional.of(new Account(id, owner, BigDecimal.ZERO)));
        service.removeAccount(id);
        verify(accounts).delete(id);
    }

    @Test
    void creationRequiresExistingOwner() {
        assertThrows(NoUserFoundException.class, () -> service.createAccount(owner));
        verifyNoInteractions(accounts);
    }

    @Test
    void accountsFilterUsesOwnerId() {
        when(users.find(owner)).thenReturn(Optional.of(new User(owner, "n", "N", 20, null, null)));
        service.getAccountsByUserId(owner);
        verify(accounts).findByUser(owner);
    }
}
