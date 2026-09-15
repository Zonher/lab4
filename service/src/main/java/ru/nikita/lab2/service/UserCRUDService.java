package ru.nikita.lab2.service;

import ru.nikita.lab2.domain.*;
import ru.nikita.lab2.domain.enumeration.*;

import java.util.*;

public interface UserCRUDService {
    User createUser(User user);

    User updateUser(User user);

    User patchUser(UserUpdate update);

    void removeUser(UUID id);

    User getUser(UUID id);

    List<User> getUsers(HairColor hairColor, Gender gender);
}
