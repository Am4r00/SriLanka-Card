package com.SriLankaCard.dto.response.login;

import com.SriLankaCard.entity.userEntity.enums.Funcao;
import lombok.Data;

import java.util.Set;

@Data
public class LoginResponse {
    private String token;
    private String name;
    private String email;
    private Set<Funcao> funcoes;

    public LoginResponse(String token, String name, String email, Set<Funcao> funcoes) {
        this.token = token;
        this.name = name;
        this.email = email;
        this.funcoes = funcoes;
    }
}
