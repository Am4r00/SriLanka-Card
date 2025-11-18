package com.SriLankaCard.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth

                        // 🌟 ROTAS WEB PÚBLICAS
                        .requestMatchers(
                                "/", "/home", "/login", "/signup",
                                "/contato", "/faq", "/sobre", "/giftcard",
                                "/jogos", "/produto", "/funcionarios", "/cart",
                                "/forgot", "/payment", "/verify", "/addEmploye",
                                "/home_admin", "/test", "/static-test","/confirmacaoPagamento"
                        ).permitAll()

                        .requestMatchers("/error", "/error/**").permitAll()

                        // 🌟 LIBERANDO AS ROTAS QUE VOCÊ REALMENTE USA
                        .requestMatchers("/users/create-user").permitAll()
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/admin/**").permitAll()

                        // 🌟 LIBERANDO ARQUIVOS ESTÁTICOS
                        .requestMatchers("/css/**", "/js/**", "/img/**", "/static/**", "/fonts/**").permitAll()

                        // 🌟 ROTAS PROTEGIDAS POR ROLE
                        .requestMatchers("/cards/criar-Card", "/cards/atualizar/**", "/cards/deletar/**")
                        .hasRole("ADMIN")
                        .requestMatchers("/cards/listar")
                        .hasAnyRole("ADMIN", "USUARIO")

                        // RESTO PRECISA JWT
                        .anyRequest().authenticated()
                );

        // JWT Filter
        http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
