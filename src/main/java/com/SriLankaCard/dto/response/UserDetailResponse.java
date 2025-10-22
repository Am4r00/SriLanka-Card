package com.SriLankaCard.dto.response;

import com.SriLankaCard.entity.userEntity.Funcao;
import com.SriLankaCard.entity.userEntity.UserStatus;
import lombok.Data;

import java.util.Set;
@Data
public class UserDetailResponse {
    private String name;
    private String email;
    private UserStatus status;
    private Set<Funcao> funcoes;
}
