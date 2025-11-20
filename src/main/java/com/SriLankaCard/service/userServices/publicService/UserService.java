package com.SriLankaCard.service.userServices.publicService;

import com.SriLankaCard.dto.request.user.userPublic.RegisterUserRequest;
import com.SriLankaCard.dto.response.user.UserResponse;
import com.SriLankaCard.entity.userEntity.User;

import java.util.List;

public interface UserService {
    UserResponse createUser(RegisterUserRequest user);
    List<UserResponse> findAll();

}
