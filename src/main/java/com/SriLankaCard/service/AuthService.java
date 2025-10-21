package com.SriLankaCard.service;

import com.SriLankaCard.dto.request.LoginRequest;
import com.SriLankaCard.dto.response.LoginResponse;
import com.SriLankaCard.entity.User;
import com.SriLankaCard.exception.negocio.InvalidArgumentsException;
import com.SriLankaCard.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService; // classe que gera JWT

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmailIgnoreCase(request.getEmail())
                .orElseThrow(() -> new InvalidArgumentsException("Email ou senha inválidos"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidArgumentsException("Email ou senha inválidos");
        }

        String token = jwtService.generateToken(user);

        return new LoginResponse(token, user.getName(), user.getEmail());
    }
}
