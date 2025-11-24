package com.SriLankaCard.dto.request.user.userPublic;


import lombok.Data;

@Data
public class ResetPasswordRequest {
    private String email;
    private String code;
    private String newPassword;
}
