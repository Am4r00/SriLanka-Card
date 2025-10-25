package com.SriLankaCard.controller.userController;

import com.SriLankaCard.dto.request.user.admin.AdminCreateRequest;
import com.SriLankaCard.dto.response.user.UserDetailResponse;
import com.SriLankaCard.entity.userEntity.enums.UserStatus;
import com.SriLankaCard.service.userServices.adminService.AdminUserImple;
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