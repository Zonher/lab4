package ru.nikita.lab2.application.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.nikita.lab2.service.AuthAccountService;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final AuthAccountService authAccountService;

    public CustomUserDetailsService(AuthAccountService authAccountService){
        this.authAccountService = authAccountService;
    }

    @Override
    public UserDetails loadUserByUsername(String username)
        throws UsernameNotFoundException{
        var account = authAccountService.findByLogin(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        return new SecurityPrincipal(account);
    }
}
