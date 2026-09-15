package ru.nikita.lab2.dao.repository;

import jakarta.persistence.LockModeType;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import ru.nikita.lab2.dao.entity.AccountEntity;

import java.util.*;

public interface AccountRepository extends JpaRepository<AccountEntity, UUID> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select a from AccountEntity a where a.id = :id")
    Optional<AccountEntity> lock(@Param("id") UUID id);

    List<AccountEntity> findAllByOrderById();

    List<AccountEntity> findByUser_IdOrderById(UUID userId);
}
