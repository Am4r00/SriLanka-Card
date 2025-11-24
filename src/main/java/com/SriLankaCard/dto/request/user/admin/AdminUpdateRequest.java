package com.SriLankaCard.dto.request.user.admin;

import com.SriLankaCard.entity.userEntity.enums.UserStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AdminUpdateRequest {
    @Size(min = 2, max = 60)
    private String name;

    @Email
    private String email;

    private UserStatus status;
}

