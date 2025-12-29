package com.SriLankaCard.dto.request.user.userPublic;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ResetPasswordRequest {
    @Email
    private String email;

    @NotBlank
    private String code;

    @NotBlank
    private String newPassword;
}
