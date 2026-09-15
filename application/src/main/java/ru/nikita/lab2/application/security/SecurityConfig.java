package ru.nikita.lab2.application.security;


import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(
            CustomUserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder
    ){
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);

        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            DaoAuthenticationProvider authenticationProvider
    )throws Exception{
            http.csrf(csrf -> csrf.disable())
                    .authenticationProvider(authenticationProvider)
                    .authorizeHttpRequests(auth -> auth
                            .requestMatchers("/login").permitAll()
                            .anyRequest().authenticated()
                    )
                    .formLogin(form -> form
                            .loginProcessingUrl("/login")
                            .usernameParameter("login")
                            .passwordParameter("password")
                            .successHandler((request, response, authentication) -> {
                                response.setStatus(HttpServletResponse.SC_OK);
                                response.setContentType("application/json");
                                response.getWriter()
                                        .write("{\"status\":\"authenticated\"}");
                            }).failureHandler((request, response, exception)-> response.sendError(
                                    HttpServletResponse.SC_UNAUTHORIZED
                                    )).permitAll()

                    )
                    .logout(logout -> logout
                            .logoutUrl("/logout")
                            .logoutSuccessHandler((request, response, authentication) -> {
                                response.setStatus(HttpServletResponse.SC_OK);
                                response.setContentType("application/json");
                                response.getWriter().write("{\"status\":\"logged out\"}");
                            })
                                    .invalidateHttpSession(true)
                                    .deleteCookies("JSESSIONID")
                            )
                            .exceptionHandling(exceptions -> exceptions
                                    .authenticationEntryPoint(
                                            (request, response, exception) ->
                                                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED)
                                    ).accessDeniedHandler((request, response, exception)-> response.sendError(
                                            HttpServletResponse.SC_FORBIDDEN
                                    )
                            )
                    );
            return http.build();

    }
}
