package com.SriLankaCard.mapper;

import com.SriLankaCard.dto.request.AdminCreateRequest;
import com.SriLankaCard.dto.request.RegisterUserRequest;
import com.SriLankaCard.dto.request.LoginRequest;
import com.SriLankaCard.dto.response.UserDetailResponse;
import com.SriLankaCard.dto.response.UserResponse;
import com.SriLankaCard.entity.userEntity.User;


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
        user.setFuncao(dto.getFuncoes());

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
