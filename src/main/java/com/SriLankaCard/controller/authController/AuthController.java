package com.SriLankaCard.controller.authController;

import com.SriLankaCard.dto.request.user.login.LoginRequest;
import com.SriLankaCard.dto.response.login.LoginResponse;
import com.SriLankaCard.service.jwtServices.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody @Valid LoginRequest request) {
        return authService.login(request);
    }
}