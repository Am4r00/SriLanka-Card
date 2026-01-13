package com.SriLankaCard.dto.request.user.userPublic;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateUserRequest {
    @NotBlank
    private String nome;
    @NotBlank @Email
    private String email;
    private String senhaAtual;
    private String novaSenha;
}

