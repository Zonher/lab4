package ru.nikita.lab2.application.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import ru.nikita.lab2.domain.AuthAccount;
import ru.nikita.lab2.domain.enumeration.Role;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public final class SecurityPrincipal implements UserDetails {
    private final String login;
    private final String passwordHash;
    private final Role role;
    private final UUID userId;
    private final boolean enabled;

    public SecurityPrincipal(AuthAccount account){
        this.login = account.login();
        this.passwordHash = account.passwordHash();
        this.role = account.role();
        this.userId = account.userId();
        this.enabled = account.enabled();
    }

    public Role role(){
        return role;
    }

    public UUID userId(){
        return userId;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities(){
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getPassword(){
        return passwordHash;
    }

    @Override
    public String getUsername(){
        return login;
    }

    @Override
    public boolean isAccountNonExpired(){
        return true;
    }

    @Override
    public boolean isAccountNonLocked(){
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired(){
        return true;
    }

    @Override
    public boolean isEnabled(){
        return enabled;
    }
}
