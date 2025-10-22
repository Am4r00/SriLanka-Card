package com.SriLankaCard.controller;

import com.SriLankaCard.dto.request.AdminCreateRequest;
import com.SriLankaCard.dto.response.UserDetailResponse;
import com.SriLankaCard.entity.userEntity.enums.UserStatus;
import com.SriLankaCard.service.AdminUserImple;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
public class UserAdminContoller {

    private AdminUserImple adminUserImple;

    public UserAdminContoller(AdminUserImple adminUserImple) {
        this.adminUserImple = adminUserImple;
    }

    @PostMapping("/create-user")
    public UserDetailResponse createUserByAdmin(@Valid @RequestBody AdminCreateRequest user){
        return adminUserImple.adminCreateUser(user);
    }

    @PatchMapping("/update-user/{id}/{status}")
    public UserDetailResponse updateUser(@Valid @PathVariable Long id, @PathVariable("status") UserStatus status){
        return adminUserImple.adjustStatus(id,status);
    }
}