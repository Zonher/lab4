package ru.nikita.lab2.application.controller;


import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import ru.nikita.lab2.application.dto.AdminCreateRequest;
import ru.nikita.lab2.domain.AuthAccount;
import ru.nikita.lab2.service.AuthAccountService;

import java.util.UUID;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private final AuthAccountService authAccountService;
    private final PasswordEncoder passwordEncoder;

    public AdminController(
            AuthAccountService authAccountService,
            PasswordEncoder passwordEncoder
    ){
        this.authAccountService = authAccountService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/admins")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public void createAdmin(
            @Valid @RequestBody AdminCreateRequest request
            ){
        authAccountService.createAdmin(request.login(), passwordEncoder.encode(request.password()));
    }

    @PatchMapping("/users/{userId}/deactivate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void deactivateClient(@PathVariable UUID userId){
        authAccountService.deactivateClient(userId);
    }

    @PatchMapping("/users/{userId}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void activateClient(@PathVariable UUID userId){
        authAccountService.activateClient(userId);
    }
}
