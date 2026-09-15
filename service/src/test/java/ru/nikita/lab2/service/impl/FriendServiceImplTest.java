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

import java.util.*;

@ExtendWith(MockitoExtension.class)
class FriendServiceImplTest {
    @Mock UserStore users;
    FriendServiceImpl service;
    UUID a = UUID.randomUUID(), b = UUID.randomUUID();

    @BeforeEach
    void setup() {
        service = new FriendServiceImpl(users);
    }

    void pair() {
        when(users.lock(a)).thenReturn(Optional.of(new User(a, "a", "A", 20, null, null)));
        when(users.find(b)).thenReturn(Optional.of(new User(b, "b", "B", 20, null, null)));
    }

    @Test
    void friendshipIsDirected() {
        pair();
        service.addFriend(a, b);
        verify(users).addFriend(a, b);
        verify(users, never()).addFriend(b, a);
    }

    @Test
    void removesFromCorrectOwner() {
        pair();
        service.removeFriend(a, b);
        verify(users).removeFriend(a, b);
        verify(users, never()).removeFriend(b, a);
    }

    @Test
    void selfFriendshipRejected() {
        assertThrows(InvalidOperationException.class, () -> service.addFriend(a, a));
        verifyNoInteractions(users);
    }

    @Test
    void missingOwnerRejected() {
        assertThrows(NoUserFoundException.class, () -> service.addFriend(a, b));
        verify(users, never()).addFriend(any(), any());
    }
}
