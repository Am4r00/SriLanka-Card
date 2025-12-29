package com.SriLankaCard.dto.request.user.userPublic;


import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class ForgotPasswordRequest {
    @Email
    private String email;
}
