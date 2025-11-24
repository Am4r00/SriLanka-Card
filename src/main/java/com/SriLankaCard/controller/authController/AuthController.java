package com.SriLankaCard.controller.authController;

import com.SriLankaCard.dto.request.user.login.LoginRequest;
import com.SriLankaCard.dto.request.user.userPublic.ForgotPasswordRequest;
import com.SriLankaCard.dto.request.user.userPublic.ResetPasswordRequest;
import com.SriLankaCard.dto.response.login.LoginResponse;
import com.SriLankaCard.service.jwtServices.AuthService;

import jakarta.validation.Valid;

import com.SriLankaCard.service.jwtServices.PasswordResetService;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final PasswordResetService passwordResetService;

    public AuthController(AuthService authService, PasswordResetService passwordResetService) {
        this.authService = authService;
        this.passwordResetService = passwordResetService;
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody @Valid LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest req) {
        passwordResetService.enviarCodigoReset(req.getEmail());
        return ResponseEntity.ok("Código enviado para o e-mail.");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest req) {
        passwordResetService.resetarSenha(req.getEmail(), req.getCode(), req.getNewPassword());
        return ResponseEntity.ok("Senha alterada com sucesso.");
    }
}