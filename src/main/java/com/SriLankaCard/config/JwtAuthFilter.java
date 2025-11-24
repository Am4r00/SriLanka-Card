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
            
            // 🌟 ROTAS DE CARDS (API)
            "/cards/listar",

            // OUTRAS PÁGINAS HTML
            "/contato", "/faq", "/sobre", "/giftcard",
            "/jogos", "/produto", "/funcionarios", "/cart",
            "/forgot", "/payment", "/verify", "/addEmploye",
            "/produtoDetalhe", "/test", "/static-test"
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
        String authHeader = request.getHeader("Authorization");

        // Se não houver token, permitir passar para que o Spring Security decida
        // Isso é necessário para que páginas HTML possam ser carregadas
        // O Spring Security vai verificar se a rota precisa de autenticação/role
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        try {
            String username = jwtService.extractUsername(token);

            // Verificar se já existe uma autenticação (útil para testes com @WithMockUser)
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                if (jwtService.isTokenValid(token, userDetails)) {
                    System.out.println("=== JWT AUTH FILTER: Autenticando usuário ===");
                    System.out.println("Username: " + username);
                    System.out.println("Authorities: " + userDetails.getAuthorities());

                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails, null, userDetails.getAuthorities()
                            );

                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    System.out.println("=== JWT AUTH FILTER: Autenticação configurada com sucesso ===");
                    System.out.println("SecurityContext Authorities: " + SecurityContextHolder.getContext().getAuthentication().getAuthorities());
                } else {
                    System.out.println("=== JWT AUTH FILTER: Token inválido ===");
                }
            } else if (SecurityContextHolder.getContext().getAuthentication() != null) {
                // Se já existe uma autenticação (ex: @WithMockUser em testes), não sobrescrever
                System.out.println("=== JWT AUTH FILTER: Autenticação já existe, mantendo ===");
            }
        } catch (Exception ex) {
            System.out.println("=== JWT AUTH FILTER: Erro ao processar token ===");
            System.out.println("Erro: " + ex.getMessage());
            ex.printStackTrace();
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        filterChain.doFilter(request, response);
    }
}
