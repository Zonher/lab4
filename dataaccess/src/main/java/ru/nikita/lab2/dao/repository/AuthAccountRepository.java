package ru.nikita.lab2.dao.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.nikita.lab2.dao.entity.AuthAccountEntity;
import ru.nikita.lab2.dao.entity.enumeration.Role;

import java.util.Optional;
import java.util.UUID;

public interface AuthAccountRepository extends JpaRepository<AuthAccountEntity, UUID> {
    Optional<AuthAccountEntity> findByLogin(String login);

    boolean existsByLogin(String login);

    boolean existsByRole(Role role);

    boolean existsByUserId(UUID userId);

    @Modifying
    @Query("""
        update AuthAccountEntity a
        set a.enabled = :enabled
        where a.user.id = :userId
                """)
    void setEnabledByUserId(
            @Param("userId") UUID userId,
            @Param("enabled") boolean enabled
    );
}
