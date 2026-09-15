package ru.nikita.lab2.dao.adapter;

import org.springframework.stereotype.Repository;
import ru.nikita.lab2.dao.entity.AuthAccountEntity;
import ru.nikita.lab2.dao.mapper.EntityEnumMapper;
import ru.nikita.lab2.dao.mapper.EntityMapper;
import ru.nikita.lab2.dao.repository.AuthAccountRepository;
import ru.nikita.lab2.dao.repository.UserRepository;
import ru.nikita.lab2.domain.AuthAccount;
import ru.nikita.lab2.domain.enumeration.Role;
import ru.nikita.lab2.port.AuthAccountStore;

import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaAuthAccountStore implements AuthAccountStore {
    private final AuthAccountRepository authAccounts;
    private final UserRepository users;

    public JpaAuthAccountStore(
            AuthAccountRepository authAccounts,
            UserRepository users
    ){
        this.authAccounts = authAccounts;
        this.users = users;
    }

    @Override
    public Optional<AuthAccount> findByLogin(String login){
        return authAccounts.findByLogin(login)
                .map(EntityMapper::authAccount);
    }

    @Override
    public boolean existsByLogin(String login){
        return authAccounts.existsByLogin(login);
    }

    @Override
    public boolean existsByRole(Role role){
        return authAccounts.existsByRole(
                EntityEnumMapper.toEntity(role)
        );
    }

    @Override
    public boolean existsByUserId(UUID userId){
        return authAccounts.existsByUserId(userId);
    }

    @Override
    public AuthAccount create(AuthAccount account){
        var user = account.userId() == null
                ? null
                : users.getReferenceById(account.userId());
        var entity = new AuthAccountEntity(
                account.login(),
                account.passwordHash(),
                EntityEnumMapper.toEntity(account.role()),
                user
        );
        return EntityMapper.authAccount(
                authAccounts.save(entity)
        );
    }

    @Override
    public void setEnabledByUserId(UUID userId, boolean enabled){
        authAccounts.setEnabledByUserId(userId, enabled);
    }
}
