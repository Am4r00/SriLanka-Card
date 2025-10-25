package com.SriLankaCard.dto.request.user.admin;

import com.SriLankaCard.entity.userEntity.enums.Funcao;
import com.SriLankaCard.entity.userEntity.enums.UserStatus;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.Set;
@Data
public class AdminCreateRequest {
    @NotBlank
    @Size(min = 2, max = 60)
    private String name;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Size(min = 8, max = 18)
    private String password;

    @NotNull
    private UserStatus status;

    @NotEmpty
    @Size(min = 1, message = "Pelo menos uma posição deve ser atribuída")
    private Set<Funcao> funcoes;
}