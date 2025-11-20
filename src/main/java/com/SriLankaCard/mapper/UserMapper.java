package com.SriLankaCard.mapper;

import com.SriLankaCard.dto.request.user.admin.AdminCreateRequest;
import com.SriLankaCard.dto.request.user.userPublic.RegisterUserRequest;
import com.SriLankaCard.dto.request.user.login.LoginRequest;
import com.SriLankaCard.dto.response.user.UserDetailResponse;
import com.SriLankaCard.dto.response.user.UserResponse;
import com.SriLankaCard.entity.userEntity.User;
import com.SriLankaCard.entity.userEntity.enums.Funcao;

import java.util.HashSet;
import java.util.Set;


public class UserMapper {

    public static LoginRequest toUserRequest(User user) {
        LoginRequest dto = new LoginRequest();


        return dto;
    }

    public static UserResponse toUserResponse(User user) {
        UserResponse dto = new UserResponse();
            dto.setId(user.getId());
            dto.setNome(user.getName());
            dto.setEmail(user.getEmail());
            dto.setFuncoes(user.getFuncao());

        return dto;
    }

    public static User toUserRegister(RegisterUserRequest dto) {
        User users = new User();
        users.setName(dto.getName());
        users.setPassword(dto.getPassword());
        users.setEmail(dto.getEmail());

        return users;
    }

    public static AdminCreateRequest toUserAdminRequest(RegisterUserRequest dto){
        AdminCreateRequest user = new AdminCreateRequest();
        user.setName(dto.getName());
        user.setPassword(dto.getPassword());
        user.setEmail(dto.getEmail());

        return user;
    }

    public static RegisterUserRequest toUserRegisterRequest(AdminCreateRequest dto){
        RegisterUserRequest user = new RegisterUserRequest();
        user.setName(dto.getName());
        user.setPassword(dto.getPassword());
        user.setEmail(dto.getEmail());

        return user;

    }

    public static User toUserByAdminRequest(AdminCreateRequest dto){
        User user = new User();
        user.setName(dto.getName());
        user.setPassword(dto.getPassword());
        user.setEmail(dto.getEmail());
        user.setStatus(dto.getStatus());
        
        // SEMPRE definir as funções - garantir que não seja null
        if (dto.getFuncoes() != null && !dto.getFuncoes().isEmpty()) {
            user.setFuncao(new HashSet<>(dto.getFuncoes())); // Criar nova instância para garantir
            System.out.println("=== MAPPER: Funções definidas ===");
            System.out.println("Funções recebidas do DTO: " + dto.getFuncoes());
            System.out.println("Funções no User após set: " + user.getFuncao());
        } else {
            System.out.println("=== MAPPER: ERRO - Funções vazias ou null no DTO ===");
            System.out.println("DTO funcoes: " + dto.getFuncoes());
            // Mesmo assim, tentar definir como ADMIN se não foi fornecido
            user.setFuncao(new HashSet<>(Set.of(Funcao.ADMIN)));
            System.out.println("Funções definidas como ADMIN por fallback: " + user.getFuncao());
        }

        return user;
    }

    public static UserDetailResponse toUserDetailByUser(User dto){
        UserDetailResponse user = new UserDetailResponse();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setStatus(dto.getStatus());
        user.setFuncoes(dto.getFuncao());

        return user;
    }
}
