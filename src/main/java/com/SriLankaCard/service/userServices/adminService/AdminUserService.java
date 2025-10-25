package com.SriLankaCard.service.userServices.adminService;

import com.SriLankaCard.dto.request.user.admin.AdminCreateRequest;
import com.SriLankaCard.dto.response.user.UserDetailResponse;
import com.SriLankaCard.dto.response.user.UserResponse;
import com.SriLankaCard.entity.userEntity.enums.UserStatus;

public interface AdminUserService {
    UserDetailResponse adminCreateUser(AdminCreateRequest user);
    UserResponse deleteUser(Long id);
    UserDetailResponse adjustStatus(Long id, UserStatus status);
}
