package com.SriLankaCard.dto.response;

import com.SriLankaCard.entity.Funcao;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class UserResponse {
    private Long id;
    private String nome;
    private String email;
    private List<Funcao> funcoes;

    public UserResponse(Long id, String nome, String email, List<Funcao> funcoes) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.funcoes = funcoes;
    }
}
