package ru.nikita.lab2.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.nikita.lab2.domain.User;
import ru.nikita.lab2.port.UserStore;
import ru.nikita.lab2.service.FriendService;
import ru.nikita.lab2.service.exception.*;

import java.util.*;

@Service
@Transactional(readOnly = true)
public class FriendServiceImpl implements FriendService {
    private final UserStore users;

    public FriendServiceImpl(UserStore users) {
        this.users = users;
    }

    @Transactional
    public void addFriend(UUID id, UUID friendId) {
        checkPair(id, friendId);
        users.addFriend(id, friendId);
    }

    @Transactional
    public void removeFriend(UUID id, UUID friendId) {
        checkPair(id, friendId);
        users.removeFriend(id, friendId);
    }

    public List<User> getFriends(UUID id) {
        users.find(id).orElseThrow(() -> new NoUserFoundException(id));
        return users.friends(id);
    }

    private void checkPair(UUID id, UUID friendId) {
        if (id == null || friendId == null || id.equals(friendId))
            throw new InvalidOperationException("Friend IDs must be different");
        users.lock(id).orElseThrow(() -> new NoUserFoundException(id));
        users.find(friendId).orElseThrow(() -> new NoUserFoundException(friendId));
    }
}
