package com.SriLankaCard.config;

import com.SriLankaCard.exception.dominio.UserNotFoundException;
import com.SriLankaCard.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity(prePostEnabled = true) // habilita @PreAuthorize
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 1) Admin em memória (fallback)
    @Bean
    public InMemoryUserDetailsManager inMemoryUserDetails(PasswordEncoder encoder) {
        var admin = User.withUsername("admin")      // login = "admin"
                .password(encoder.encode("12345678"))
                .roles("ADMIN")                     // vira "ROLE_ADMIN"
                .build();
        return new InMemoryUserDetailsManager(admin);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**", "/public/**", "/users/create-user", "/users/create-user/**").permitAll()
                        .anyRequest().authenticated()
                )
                .httpBasic(c -> {}) // habilita Basic Auth
                .build();
    }
}
