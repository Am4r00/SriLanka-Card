package com.SriLankaCard.config;

import com.SriLankaCard.service.jwtServices.CustomUserDetailsService;
import com.SriLankaCard.service.jwtServices.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Set;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthFilter.class);
    private final CustomUserDetailsService userDetailsService;
    private final JwtService jwtService;

    private static final Set<String> PUBLIC_URLS = Set.of(
            "/", "/home", "/login", "/signup",

            "/users/signup",
            "/users/create-user",

            "/auth/login",
            "/auth/registrar",

            "/cards",

            "/contato", "/faq", "/sobre", "/giftcard",
            "/produto", "/funcionarios",
            "/forgot", "/verify", "/reset-password",
            "/produtoDetalhe", "/test", "/static-test"
    );

    private static final List<String> STATIC_PATH_PREFIXES = List.of(
            "/css/", "/js/", "/img/", "/static/", "/fonts/"
    );

    public JwtAuthFilter(JwtService jwtService, CustomUserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getServletPath();

        if (isPublicPath(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = extractToken(request);
        if (token == null) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String username = jwtService.extractUsername(token);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                if (jwtService.isTokenValid(token, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails, null, userDetails.getAuthorities()
                            );

                    SecurityContextHolder.getContext().setAuthentication(authToken);
                } else {
                    log.debug("Token JWT inválido para usuário {}", username);
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    return;
                }
            }
        } catch (Exception ex) {
            log.warn("Erro ao validar JWT para {}: {}", path, ex.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        filterChain.doFilter(request, response);
    }

    private boolean isPublicPath(String path) {
        if (PUBLIC_URLS.contains(path) || path.endsWith(".html")) {
            return true;
        }

        return STATIC_PATH_PREFIXES.stream().anyMatch(path::startsWith);
    }

    private String extractToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("jwt_token".equals(cookie.getName()) && cookie.getValue() != null && !cookie.getValue().isBlank()) {
                    log.debug("Token JWT obtido do cookie");
                    return cookie.getValue();
                }
            }
        }

        return null;
    }
}
