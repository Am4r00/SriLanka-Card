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
                .headers(h -> h.frameOptions(f -> f.sameOrigin()))
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth

                        // 🌟 ROTAS WEB PÚBLICAS
                        .requestMatchers(
                                "/", "/home", "/login", "/signup",
                                "/contato", "/faq", "/sobre", "/giftcard",
                                "/jogos", "/produto", "/funcionarios", "/cart",
                                "/forgot", "/payment", "/verify", "/addEmploye",
                                "/home_admin", "/test", "/static-test","/confirmacaoPagamento","/verify.html"
                        ).permitAll()


                        // 🌟 ROTAS PRIVADAS — NECESSITAM LOGIN
                        .requestMatchers("/produto", "/funcionarios", "/cart","/api/**","/confirmacaoPagamento")
                        .authenticated()
                        
                        // 🌟 ROTAS ADMIN — NECESSITAM ROLE ADMIN
                        .requestMatchers("/home_admin", "/usuariodetalhe")
                        .hasRole("ADMIN")

                        // 🌟 LIBERANDO AS ROTAS DE API NECESSÁRIAS
                        .requestMatchers("/users/create-user").permitAll()
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/admin/create-user", "/admin/create-user-common", "/admin/test-create-admin", "/admin/update-user-to-admin").permitAll()
                        .requestMatchers("/admin/**").authenticated()
                        
                        // 🌟 API DE CARDS - Listar é público, criar/atualizar/deletar precisa de ADMIN
                        .requestMatchers("/cards/listar", "/cards/{id}").permitAll()
                        .requestMatchers("/admin/**").permitAll()

                        // 🌟 LIBERANDO ARQUIVOS ESTÁTICOS
                        .requestMatchers("/css/**", "/js/**", "/img/**", "/static/**", "/fonts/**").permitAll()

                        // 🌟 ROTAS DO CARRINHO – precisam estar autenticadas
                        .requestMatchers("/api/carrinho/**").authenticated()

                        // 🌟 ROTAS PROTEGIDAS POR ROLE
                        .requestMatchers("/cards/criar-Card", "/cards/atualizar/**", "/cards/deletar/**")
                        .hasRole("ADMIN")

                        // 🌟 ARQUIVOS ESTÁTICOS
                        .requestMatchers("/css/**", "/js/**", "/img/**", "/static/**", "/fonts/**").permitAll()

                        // RESTO PRECISA AUTENTICAÇÃO
                        .anyRequest().authenticated()
                );

        http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
