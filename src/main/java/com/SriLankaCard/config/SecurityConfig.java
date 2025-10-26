package com.SriLankaCard.config;

import com.SriLankaCard.config.JwtAuthFilter;
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

import static org.springframework.security.authorization.SingleResultAuthorizationManager.permitAll;

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
                        // endpoints públicos
                        .requestMatchers("/auth/**", "/users/create-user").permitAll()
                        .requestMatchers("/admin/**").permitAll()
                        .requestMatchers("/cards/criar-Card", "/cards/atualizar/**", "/cards/deletar/**").hasRole("ADMIN")
                        .requestMatchers("/cards/listar").hasAnyRole("ADMIN", "USUARIO")
                        // páginas HTML públicas
                        .requestMatchers("/", "/home", "/login", "/singup", "/giftcard", "/jogos", "/employe", "/cart", "/contato", "/faq", "/forgot", "/payment", "/produto", "/produtoDetalhe", "/sobre", "/verify", "/addEmploye", "/home_admin", "/test", "/static-test").permitAll()
                        .requestMatchers("/static/**", "/css/**", "/js/**", "/img/**").permitAll()
                        .anyRequest().authenticated()
                );

        // adicionar o filtro JWT antes do filtro de autenticação do Spring
        http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
