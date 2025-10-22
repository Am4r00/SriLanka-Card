package com.SriLankaCard.dto.response;

import com.SriLankaCard.entity.userEntity.Funcao;
import lombok.Data;

import java.util.Set;

@Data
public class UserResponse {
    private Long id;
    private String nome;
    private String email;
    private Set<Funcao> funcoes;

}
