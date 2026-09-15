package ru.nikita.lab2.service;

import ru.nikita.lab2.domain.*;
import ru.nikita.lab2.domain.enumeration.*;

import java.util.*;

public interface FriendService {
    void addFriend(UUID userId, UUID friendId);

    void removeFriend(UUID userId, UUID friendId);

    List<User> getFriends(UUID userId);
}
