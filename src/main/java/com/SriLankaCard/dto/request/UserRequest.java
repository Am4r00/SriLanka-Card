package com.SriLankaCard.dto.request;

import com.SriLankaCard.entity.Funcao;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;


@Data
public class UserRequest {

    @Id
    private Long id;

    @NotBlank
    @Size(min = 2, max = 60)
    private String nome;

    @Email
    @Size
    private String email;

    @NotBlank
    private List<Funcao> funcoes;

    public UserRequest(Long id,String nome, String email, List<Funcao> funcoes) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.funcoes = funcoes;
    }
}
