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

import java.util.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock UserStore users;
    UserServiceImpl service;
    UUID id = UUID.randomUUID();
    User old;

    @BeforeEach
    void setup() {
        service = new UserServiceImpl(users);
        old = new User(id, "nikita", "Nikita", 20, Gender.MALE, HairColor.BLACK);
    }

    @Test
    void creationNormalizesTextAndAllowsOptionalFields() {
        service.createUser(new User(null, " nikita ", " Nikita ", 20, null, null));
        verify(users).create(new User(null, "nikita", "Nikita", 20, null, null));
    }

    @Test
    void duplicateLoginRejected() {
        when(users.existsByLogin("nikita")).thenReturn(true);
        assertThrows(UserAlreadyExistsException.class, () -> service.createUser(old));
        verify(users, never()).create(any());
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", " ", "abcdefghijklmnopqrstu"})
    void invalidLoginRejected(String login) {
        assertThrows(
                InvalidUserDataException.class,
                () -> service.createUser(new User(null, login, "Nikita", 20, null, null)));
        verifyNoInteractions(users);
    }

    @Test
    void patchPreservesMissingFieldsAndClearsExplicitNull() {
        when(users.lock(id)).thenReturn(Optional.of(old));
        service.patchUser(
                new UserUpdate(
                        id,
                        FieldPatch.absent(),
                        FieldPatch.absent(),
                        FieldPatch.of(null),
                        FieldPatch.absent()));
        verify(users).update(new User(id, "nikita", "Nikita", 20, null, HairColor.BLACK));
    }

    @Test
    void requiredNameCannotBeCleared() {
        when(users.lock(id)).thenReturn(Optional.of(old));
        assertThrows(
                InvalidUserDataException.class,
                () ->
                        service.patchUser(
                                new UserUpdate(
                                        id,
                                        FieldPatch.of(null),
                                        FieldPatch.absent(),
                                        FieldPatch.absent(),
                                        FieldPatch.absent())));
        verify(users, never()).update(any());
    }

    @Test
    void requiredAgeCannotBeCleared() {
        when(users.lock(id)).thenReturn(Optional.of(old));
        assertThrows(
                InvalidUserDataException.class,
                () ->
                        service.patchUser(
                                new UserUpdate(
                                        id,
                                        FieldPatch.absent(),
                                        FieldPatch.of(null),
                                        FieldPatch.absent(),
                                        FieldPatch.absent())));
    }

    @Test
    void fullUpdateNeverChangesLogin() {
        when(users.lock(id)).thenReturn(Optional.of(old));
        service.updateUser(new User(id, "hacked", "New", 21, null, null));
        verify(users).update(new User(id, "nikita", "New", 21, null, null));
    }

    @Test
    void missingUserIsDomainError() {
        assertThrows(NoUserFoundException.class, () -> service.getUser(id));
    }
}
