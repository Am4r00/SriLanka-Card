package com.SriLankaCard.service;

import com.SriLankaCard.dto.request.AdminCreateRequest;
import com.SriLankaCard.dto.response.UserDetailResponse;
import com.SriLankaCard.dto.response.UserResponse;
import com.SriLankaCard.entity.enums.UserStatus;

public interface AdminUserService {
    UserDetailResponse adminCreateUser(AdminCreateRequest user);
    UserResponse deleteUser(Long id);
    UserDetailResponse adjustStatus(Long id, UserStatus status);
}
