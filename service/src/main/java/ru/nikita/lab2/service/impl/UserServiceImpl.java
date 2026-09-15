package ru.nikita.lab2.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.nikita.lab2.domain.*;
import ru.nikita.lab2.domain.enumeration.*;
import ru.nikita.lab2.port.UserStore;
import ru.nikita.lab2.service.UserCRUDService;
import ru.nikita.lab2.service.exception.*;

import java.util.*;

@Service
@Transactional(readOnly = true)
public class UserServiceImpl implements UserCRUDService {
    private final UserStore users;

    public UserServiceImpl(UserStore users) {
        this.users = users;
    }

    @Transactional
    public User createUser(User user) {
        String login = text(user.login(), "login", 20);
        String name = text(user.name(), "name", 50);
        int age = age(user.age());
        if (users.existsByLogin(login)) throw new UserAlreadyExistsException(login);
        return users.create(new User(null, login, name, age, user.gender(), user.hairColor()));
    }

    @Transactional
    public User updateUser(User user) {
        return apply(
                new UserUpdate(
                        user.id(),
                        FieldPatch.of(user.name()),
                        FieldPatch.of(user.age()),
                        FieldPatch.of(user.gender()),
                        FieldPatch.of(user.hairColor())));
    }

    @Transactional
    public User patchUser(UserUpdate update) {
        return apply(update);
    }

    private User apply(UserUpdate patch) {
        var old = users.lock(patch.id()).orElseThrow(() -> new NoUserFoundException(patch.id()));
        return users.update(
                new User(
                        old.id(),
                        old.login(),
                        patch.name().present()
                                ? text(patch.name().value(), "name", 50)
                                : old.name(),
                        patch.age().present() ? age(patch.age().value()) : old.age(),
                        patch.gender().present() ? patch.gender().value() : old.gender(),
                        patch.hairColor().present() ? patch.hairColor().value() : old.hairColor()));
    }

    @Transactional
    public void removeUser(UUID id) {
        users.lock(id).orElseThrow(() -> new NoUserFoundException(id));
        users.delete(id);
    }

    public User getUser(UUID id) {
        return users.find(id).orElseThrow(() -> new NoUserFoundException(id));
    }

    public List<User> getUsers(HairColor color, Gender gender) {
        return users.findAll(color, gender);
    }

    private static String text(String value, String field, int max) {
        if (value == null || value.isBlank() || value.strip().length() > max)
            throw new InvalidUserDataException(field + " must contain 1.." + max + " characters");
        return value.strip();
    }

    private static int age(Integer age) {
        if (age == null || age <= 0) throw new InvalidUserDataException("age must be positive");
        return age;
    }
}
