package ru.nikita.lab2.dao.repository;

import jakarta.persistence.LockModeType;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import ru.nikita.lab2.dao.entity.UserEntity;
import ru.nikita.lab2.dao.entity.enumeration.*;

import java.util.*;

public interface UserRepository extends JpaRepository<UserEntity, UUID> {
    boolean existsByLogin(String login);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select u from UserEntity u where u.id = :id")
    Optional<UserEntity> lock(@Param("id") UUID id);

    @Query(
            "select u from UserEntity u where (:hairColor is null or u.hairColor = :hairColor) and"
                    + " (:gender is null or u.gender = :gender) order by u.login")
    List<UserEntity> findFiltered(
            @Param("hairColor") HairColor hairColor, @Param("gender") Gender gender);

    @Query("select f from UserEntity u join u.friends f where u.id = :id order by f.login")
    List<UserEntity> findFriends(@Param("id") UUID id);

    @Query(
            "select count(f) from UserEntity u join u.friends f where u.id = :ownerId and f.id ="
                    + " :friendId")
    long countFriendship(@Param("ownerId") UUID ownerId, @Param("friendId") UUID friendId);
}
