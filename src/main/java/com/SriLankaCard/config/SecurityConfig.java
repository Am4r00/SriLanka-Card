package com.SriLankaCard.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
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

//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        return http
//                .csrf(csrf -> csrf.disable())
//                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers("/auth/**", "/public/**", "/users/create-user","/admin/**", "/cards/**").permitAll()
//                        .anyRequest().authenticated()
//                )
//                .httpBasic(c -> {}) // habilita Basic Auth
//                .build();
//    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // ADICIONE ESTE BLOCO PARA PERMITIR IFRAMES
                .headers(headers -> headers
                        .frameOptions(frameOptions -> frameOptions.disable()) // Desabilita X-Frame-Options
                )
                // OU
                // .headers(headers -> headers.frameOptions(FrameOptionsConfig::disable))

                .authorizeHttpRequests(auth -> auth
                        // Regra de acesso (você já usou .anyRequest().permitAll() para dev)
                        .requestMatchers("/auth/**", "/public/**", "/users/create-user","/admin/**", "/cards/**").permitAll()
                        .anyRequest().permitAll() // Mantém a segurança desabilitada para dev
                )
                .httpBasic(c -> {})
                .build();
    }
}