package com.SriLankaCard.dto.request.user.userPublic;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ActivationConfirmRequest {

    @Email
    private String email;

    @NotBlank
    private String code;
}
