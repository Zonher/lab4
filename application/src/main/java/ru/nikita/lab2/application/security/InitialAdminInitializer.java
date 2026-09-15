package ru.nikita.lab2.application.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import ru.nikita.lab2.service.AuthAccountService;

@Component
public class InitialAdminInitializer implements ApplicationRunner {
    public final AuthAccountService authAccountService;
    private final PasswordEncoder passwordEncoder;

    @Value("${INITIAL_ADMIN_LOGIN:admin}")
    private String adminLogin;

    @Value("${INITIAL_ADMIN_PASSWORD:admin123}")
    private String adminPassword;

    public InitialAdminInitializer(AuthAccountService authAccountService,
                                   PasswordEncoder passwordEncoder){
        this.authAccountService = authAccountService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(ApplicationArguments args){
        if (!authAccountService.hasAdmin()){
            authAccountService.createAdmin(adminLogin, passwordEncoder.encode(adminPassword));
        }
    }
}
