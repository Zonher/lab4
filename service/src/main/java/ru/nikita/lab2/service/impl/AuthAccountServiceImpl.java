package ru.nikita.lab2.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.nikita.lab2.domain.AuthAccount;
import ru.nikita.lab2.domain.User;
import ru.nikita.lab2.domain.enumeration.Role;
import ru.nikita.lab2.port.AuthAccountStore;
import ru.nikita.lab2.port.UserStore;
import ru.nikita.lab2.service.AuthAccountService;
import ru.nikita.lab2.service.UserCRUDService;
import ru.nikita.lab2.service.exception.InvalidUserDataException;
import ru.nikita.lab2.service.exception.UserAlreadyExistsException;

import java.util.Optional;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class AuthAccountServiceImpl implements AuthAccountService {
    private final AuthAccountStore authAccounts;
    private final UserStore userStore;
    private final UserCRUDService users;

    public AuthAccountServiceImpl(
            AuthAccountStore authAccounts,
            UserStore userStore,
            UserCRUDService users
    ){
        this.authAccounts = authAccounts;
        this.users = users;
        this.userStore = userStore;
    }

    @Override
    public Optional<AuthAccount> findByLogin(String login){
        return authAccounts.findByLogin(login);
    }

    @Override
    @Transactional
    public User createClient(User user, String passwordHash){
        String login = normalizeLogin(user.login());
        requirePasswordHash(passwordHash);

        if (authAccounts.existsByLogin(login)){
            throw new UserAlreadyExistsException(login);
        }
        User createUser = users.createUser(user);

        authAccounts.create(
                new AuthAccount(
                        null,
                        login,
                        passwordHash,
                        Role.CLIENT,
                        createUser.id(),
                        true
                )
        );
        return createUser;
    }

    @Override
    @Transactional
    public AuthAccount createAdmin(String login, String passwordHash){
        login = normalizeLogin(login);
        requirePasswordHash(passwordHash);

        if (authAccounts.existsByLogin(login) || userStore.existsByLogin(login)){
            throw new UserAlreadyExistsException(login);
        }
        return authAccounts.create(
                new AuthAccount(null,
                        login,
                        passwordHash,
                        Role.ADMIN,
                        null,
                        true)
        );

    }

    @Override
    public boolean hasAdmin(){
        return authAccounts.existsByRole(Role.ADMIN);
    }

    private static String normalizeLogin(String login){
        if (login == null || login.isBlank() || login.strip().length() > 20){
            throw new InvalidUserDataException("login must contain 1..20 characters");
        }
        return login.strip();

    }


    private static void requirePasswordHash(String passwordHash){
        if (passwordHash == null || passwordHash.isBlank()){
            throw new InvalidUserDataException("password hash is required");
        }
    }

    @Override
    @Transactional
    public void activateClient(UUID userId){
        authAccounts.setEnabledByUserId(userId, true);
    }

    @Override
    @Transactional
    public void deactivateClient(UUID userId){
        authAccounts.setEnabledByUserId(userId, false);
    }
}
