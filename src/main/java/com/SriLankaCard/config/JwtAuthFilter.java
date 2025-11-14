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

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    public JwtAuthFilter(JwtService jwtService, CustomUserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String path = request.getServletPath();

        // LIBERA endpoints públicos
        if (path.equals("/users/create-user") || 
            path.startsWith("/auth") || 
            path.startsWith("/admin") ||
            path.startsWith("/static") ||
            path.startsWith("/css") ||
            path.startsWith("/js") ||
            path.startsWith("/img") ||
            path.equals("/") ||
            path.equals("/home") ||
            path.equals("/login") ||
            path.equals("/singup") ||
            path.equals("/giftcard") ||
            path.equals("/jogos") ||
            path.equals("/employe") ||
            path.equals("/cart") ||
            path.equals("/contato") ||
            path.equals("/faq") ||
            path.equals("/forgot") ||
            path.equals("/payment") ||
            path.equals("/produto") ||
            path.equals("/produtoDetalhe") ||
            path.equals("/sobre") ||
            path.equals("/verify") ||
            path.equals("/addEmploye") ||
            path.equals("/home_admin") ||
            path.equals("/test") ||
            path.equals("/static-test")) {
            filterChain.doFilter(request, response);
            return;
        }

        // captura token
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                String username = jwtService.extractUsername(token);

                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                    if (jwtService.isTokenValid(token, userDetails)) {
                        UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                    }
                }
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}
