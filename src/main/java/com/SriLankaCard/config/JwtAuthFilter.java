package com.SriLankaCard.config;

import com.SriLankaCard.service.jwtServices.CustomUserDetailsService;
import com.SriLankaCard.service.jwtServices.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Set;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;


    private static final Set<String> PUBLIC_URLS = Set.of(
            "/", "/home", "/login", "/signup",

            // 🌟 ROTAS DO USERCONTROLLER
            "/users/signup",
            "/users/create-user",

            // 🌟 ROTAS DO AUTHCONTROLLER
            "/auth/login",
            "/auth/registrar",
            
            // 🌟 ROTAS DE ADMIN (API)
            "/admin/create-user",
            "/admin/create-user-common",
            "/admin/test-create-admin",
            "/admin/update-user-to-admin",
            
            // 🌟 ROTAS DE CARDS (API)
            "/cards/listar",

            // OUTRAS PÁGINAS HTML
            "/contato", "/faq", "/sobre", "/giftcard",
            "/jogos", "/produto", "/funcionarios", "/cart",
            "/forgot", "/payment", "/verify", "/addEmploye",
            "/test", "/static-test",
            "/produtoDetalhe"
            // /home_admin foi removido - precisa autenticação ADMIN
            "/home_admin", "/test", "/static-test","/confirmacaoPagamento"
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
        System.out.println("JWT FILTER PATH: " + path);

        // 🔥 LIBERAR TODAS AS ROTAS PÚBLICAS
        if (PUBLIC_URLS.contains(path) ||
                path.endsWith(".html") ||
                path.startsWith("/css/") ||
                path.startsWith("/js/") ||
                path.startsWith("/img/") ||
                path.startsWith("/static/") ||
                path.startsWith("/fonts/")) {

            filterChain.doFilter(request, response);
            return;
        }

        // 🔥 VERIFICAÇÃO DE TOKEN
        // Tentar pegar do header Authorization primeiro
        String authHeader = request.getHeader("Authorization");
        String token = null;
        
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
        } else {
            // Se não tiver no header, tentar pegar do cookie
            jakarta.servlet.http.Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (jakarta.servlet.http.Cookie cookie : cookies) {
                    if ("jwt_token".equals(cookie.getName())) {
                        token = cookie.getValue();
                        break;
                    }
                }
            }
        }
        
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
                }
            }

        } catch (Exception ex) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        filterChain.doFilter(request, response);
    }
}
