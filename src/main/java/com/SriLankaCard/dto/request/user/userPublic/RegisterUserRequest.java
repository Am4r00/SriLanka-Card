package com.SriLankaCard.dto.request.user.userPublic;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterUserRequest {

    @NotBlank(message = "Nome não pode estar vazio")
    @Size(min = 2, max = 60, message = "Nome deve ter no minimo 2 letras")
    private String name;

    @NotBlank(message = "A  senha não pode estar vazia")
    @Size(min = 8, max = 18, message = "The password must contain at least 8 characters")
    private String password;

    @NotBlank
    @Email
    private String email;

}
