package com.SriLankaCard.dto.response.user;


import com.SriLankaCard.entity.userEntity.enums.Funcao;
import com.SriLankaCard.entity.userEntity.enums.UserStatus;
import lombok.Data;

import java.util.Set;
@Data
public class UserDetailResponse {
    private String name;
    private String email;
    private UserStatus status;
    private Set<Funcao> funcoes;
}
