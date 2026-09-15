package ru.nikita.lab2.dao.adapter;

import org.springframework.stereotype.Repository;

import ru.nikita.lab2.dao.entity.UserEntity;
import ru.nikita.lab2.dao.mapper.EntityEnumMapper;
import ru.nikita.lab2.dao.mapper.EntityMapper;
import ru.nikita.lab2.dao.repository.UserRepository;
import ru.nikita.lab2.domain.User;
import ru.nikita.lab2.domain.enumeration.*;
import ru.nikita.lab2.port.UserStore;

import java.util.*;

@Repository
public class JpaUserStore implements UserStore {
    private final UserRepository repository;

    public JpaUserStore(UserRepository repository) {
        this.repository = repository;
    }

    public Optional<User> find(UUID id) {
        return repository.findById(id).map(EntityMapper::user);
    }

    public Optional<User> lock(UUID id) {
        return repository.lock(id).map(EntityMapper::user);
    }

    public boolean existsByLogin(String login) {
        return repository.existsByLogin(login);
    }

    public List<User> findAll(HairColor hairColor, Gender gender) {
        return repository
                .findFiltered(
                        EntityEnumMapper.toEntity(hairColor), EntityEnumMapper.toEntity(gender))
                .stream()
                .map(EntityMapper::user)
                .toList();
    }

    public User create(User user) {
        return EntityMapper.user(
                repository.save(
                        new UserEntity(
                                user.login(),
                                user.name(),
                                user.age(),
                                EntityEnumMapper.toEntity(user.gender()),
                                EntityEnumMapper.toEntity(user.hairColor()))));
    }

    public User update(User user) {
        UserEntity entity = repository.getReferenceById(user.id());
        entity.setName(user.name());
        entity.setAge(user.age());
        entity.setGender(EntityEnumMapper.toEntity(user.gender()));
        entity.setHairColor(EntityEnumMapper.toEntity(user.hairColor()));
        return EntityMapper.user(entity);
    }

    public void delete(UUID id) {
        repository.delete(repository.getReferenceById(id));
    }

    public List<User> friends(UUID id) {
        return repository.findFriends(id).stream().map(EntityMapper::user).toList();
    }

    public boolean isFriend(UUID ownerId, UUID friendId) {
        return repository.countFriendship(ownerId, friendId) > 0;
    }

    public void addFriend(UUID ownerId, UUID friendId) {
        repository.getReferenceById(ownerId).addFriend(repository.getReferenceById(friendId));
    }

    public void removeFriend(UUID ownerId, UUID friendId) {
        repository.getReferenceById(ownerId).removeFriend(repository.getReferenceById(friendId));
    }
}
