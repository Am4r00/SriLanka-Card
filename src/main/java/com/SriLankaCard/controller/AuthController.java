package com.SriLankaCard.controller;

import com.SriLankaCard.dto.request.LoginRequest;
import com.SriLankaCard.dto.response.LoginResponse;
import com.SriLankaCard.service.AuthService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }
}
