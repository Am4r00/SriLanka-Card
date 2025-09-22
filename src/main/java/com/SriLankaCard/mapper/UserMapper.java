package com.SriLankaCard.mapper;

import com.SriLankaCard.dto.request.UserRequest;
import com.SriLankaCard.entity.User;

public class UserMapper {

    public static UserRequest toUser(User user){
        UserRequest dto = new UserRequest(
                user.getId(),
                user.getNome(),
                user.getEmail(),
                user.getFuncao());

        return dto;
    }
}
