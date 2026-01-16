package com.SriLankaCard.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
@org.springframework.context.annotation.Profile("!test")
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
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .headers(h -> h.frameOptions(f -> f.sameOrigin()))
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth

                        //Arquvivos estáticos que sempre são permitidos
                        .requestMatchers("/css/**","/js/**","/img/**","/static/**","/fonts/**").permitAll()

                        //Rotas públicas 
                        .requestMatchers(
                                "/", "/home", "/login", "/signup", "/contato", "/faq", "/sobre", "/giftcard",
                                "/forgot", "/verify", "/reset-password","/test", "/static-test",
                                "/users/create-user","/users/send-activation-code","/users/activate","/erro",
                                "/auth/**","/cards","/cards/{id}","/error").permitAll()

                        //Rotas que precisam de login
                        .requestMatchers(
                                "/payment","/cart","/api/**","/confirmacaoPagamento", "/users/me",
                                "/api/carrinho/**").authenticated()
                        
                        // Rotas protegidas por ROLE - ADMIN
                        .requestMatchers(
                                "/admin/**","/home_admin","/painel-admin","/produto","/addEmploye",
                                "/cards/criar-Card", "/cards/atualizar/**", "/cards/deletar/**","/users/list",
                                "/cards/{id}/promocao/{promo}"
                        ).hasRole("ADMIN")

                        // RESTO PRECISA AUTENTICAÇÃO
                        .anyRequest().authenticated()
                );

        http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
