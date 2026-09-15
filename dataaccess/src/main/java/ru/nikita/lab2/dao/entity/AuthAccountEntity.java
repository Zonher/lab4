package ru.nikita.lab2.dao.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;
import ru.nikita.lab2.dao.entity.enumeration.Role;
import ru.nikita.lab2.domain.AuthAccount;
import ru.nikita.lab2.domain.User;

import java.util.UUID;

@Entity
@Table(name = "auth_accounts")
@Access(AccessType.FIELD)
public class AuthAccountEntity {
    @Id
    @UuidGenerator(style = UuidGenerator.Style.VERSION_7)
    private UUID id;

    @Column(name = "login", nullable = false, unique = true, length = 20)
    private String login;

    @Column(name = "password_hash", nullable = false, length = 10)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 10)
    private Role role;

    @Column(name = "enabled", nullable = false)
    private boolean enabled = true;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true)
    private UserEntity user;

    protected AuthAccountEntity(){
        //for JPA only
    }

    public AuthAccountEntity(
            String login,
            String passwordHash,
            Role role,
            UserEntity user
    ){
        this.login = login;
        this.passwordHash = passwordHash;
        this.role = role;
        this.user = user;
    }

    public UUID getId(){
        return id;
    }

    public String getLogin(){
        return login;
    }

    public String getPasswordHash(){
        return passwordHash;
    }

    public Role getRole(){
        return role;
    }

    public boolean isEnabled(){
        return enabled;
    }

    public UserEntity getUser(){
        return user;
    }
}
