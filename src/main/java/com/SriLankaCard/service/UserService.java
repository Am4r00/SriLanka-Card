package com.SriLankaCard.service;

import com.SriLankaCard.dto.request.RegisterUserRequest;
import com.SriLankaCard.dto.response.UserResponse;

import java.util.List;

public interface UserService {
    UserResponse createUser(RegisterUserRequest user);
    List<UserResponse> findAll();
}
