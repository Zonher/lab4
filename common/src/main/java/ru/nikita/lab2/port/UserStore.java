package ru.nikita.lab2.port;

import ru.nikita.lab2.domain.*;
import ru.nikita.lab2.domain.enumeration.*;

import java.util.*;

public interface UserStore {
    Optional<User> find(UUID id);

    Optional<User> lock(UUID id);

    boolean existsByLogin(String login);

    List<User> findAll(HairColor hairColor, Gender gender);

    User create(User user);

    User update(User user);

    void delete(UUID id);

    List<User> friends(UUID id);

    boolean isFriend(UUID ownerId, UUID friendId);

    void addFriend(UUID ownerId, UUID friendId);

    void removeFriend(UUID ownerId, UUID friendId);
}
