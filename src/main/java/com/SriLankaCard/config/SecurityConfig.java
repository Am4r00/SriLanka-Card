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
                .headers(h -> h.frameOptions(f -> f.sameOrigin()))
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth

                        // 🌟 ROTAS WEB PÚBLICAS
                        .requestMatchers(
                                "/", "/home", "/login", "/signup",
                                "/contato", "/faq", "/sobre", "/giftcard",
                                "/jogos", "/produto", "/funcionarios", "/cart",
                                "/forgot", "/payment", "/verify", "/addEmploye",

                                "/home_admin", "/test", "/static-test","/confirmacaoPagamento","/verify.html", "/usuariodetalhe"

                        ).permitAll()


                        // 🌟 ROTAS PRIVADAS — NECESSITAM LOGIN
                        .requestMatchers("/produto", "/funcionarios", "/cart","/api/**","/confirmacaoPagamento")
                        .authenticated()

                        // 🌟 LIBERANDO AS ROTAS DE API NECESSÁRIAS
                        .requestMatchers("/users/create-user").permitAll()
                        .requestMatchers("/users/list", "/users/me").authenticated()
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/admin/create-user", "/admin/create-user-common", "/admin/test-create-admin", "/admin/update-user-to-admin").permitAll()
                        
                        // 🌟 API DE CARDS - Listar é público, criar/atualizar/deletar precisa de ADMIN
                        .requestMatchers("/cards/listar", "/cards/{id}").permitAll()
                        
                        // 🌟 ROTAS ADMIN - precisam estar autenticadas com role ADMIN
                        // As rotas específicas com @PreAuthorize vão verificar a role
                        .requestMatchers("/admin/**").authenticated()

                        // 🌟 LIBERANDO ARQUIVOS ESTÁTICOS
                        .requestMatchers("/css/**", "/js/**", "/img/**", "/static/**", "/fonts/**").permitAll()

                        // 🌟 ROTAS DO CARRINHO – precisam estar autenticadas
                        .requestMatchers("/api/carrinho/**").authenticated()

                        // 🌟 ROTAS PROTEGIDAS POR ROLE - Cards
                        .requestMatchers("/cards/criar-Card", "/cards/atualizar/**", "/cards/deletar/**")
                        .hasRole("ADMIN")
                        
                        // 🌟 Endpoint de erro do Spring (para não bloquear mensagens de erro)
                        .requestMatchers("/error").permitAll()

                        // 🌟 ARQUIVOS ESTÁTICOS
                        .requestMatchers("/css/**", "/js/**", "/img/**", "/static/**", "/fonts/**").permitAll()

                        // RESTO PRECISA AUTENTICAÇÃO
                        .anyRequest().authenticated()
                );

        http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
