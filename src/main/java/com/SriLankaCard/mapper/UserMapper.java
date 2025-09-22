package com.SriLankaCard.mapper;

import com.SriLankaCard.dto.request.UserRequest;
import com.SriLankaCard.dto.response.UserResponse;
import com.SriLankaCard.entity.User;

public class UserMapper {

    public static UserRequest toUserRequest(User user){
        UserRequest dto = new UserRequest(
                user.getId(),
                user.getNome(),
                user.getEmail(),
                user.getFuncao());

        return dto;
    }

    public static UserResponse toUserResponse(User user){
        UserResponse dto = new UserResponse(
                user.getId(),
                user.getNome(),
                user.getEmail(),
                user.getFuncao());

        return dto;
    }
}
