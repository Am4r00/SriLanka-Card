package com.SriLankaCard.dto.request.user.login;

import com.SriLankaCard.entity.userEntity.enums.UserStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;


@Data
public class LoginRequest {
    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String password;

    @Override
    public String toString() {
        return "Login: " + email;
    }
}
