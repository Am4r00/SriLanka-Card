package com.SriLankaCard.dto.request.user.userPublic;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterUserRequest {

    @NotBlank
    @Size(min = 2, max = 60)
    private String name;

    @NotBlank
    @Size(min = 8, max = 18)
    private String password;

    @NotBlank
    @Email
    private String email;

}
