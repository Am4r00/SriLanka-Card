package com.SriLankaCard.dto.request.user.userPublic;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActivationRequest {
    @Email
    private String  email;
}
