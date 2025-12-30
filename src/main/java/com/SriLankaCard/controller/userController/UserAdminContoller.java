package com.SriLankaCard.controller.userController;

import com.SriLankaCard.dto.request.user.admin.AdminCreateRequest;
import com.SriLankaCard.dto.request.user.admin.AdminUpdateRequest;
import com.SriLankaCard.dto.response.user.UserDetailResponse;
import com.SriLankaCard.entity.userEntity.enums.Funcao;
import com.SriLankaCard.entity.userEntity.enums.UserStatus;
import com.SriLankaCard.exception.negocio.InvalidArgumentsException;
import com.SriLankaCard.service.userServices.adminService.AdminUserImple;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@PreAuthorize("hasRole('ADMIN')")
@RestController
@RequestMapping("/admin")
public class UserAdminContoller {

    private final AdminUserImple adminUserImple;

    public UserAdminContoller(AdminUserImple adminUserImple) {
        this.adminUserImple = adminUserImple;
    }

    @PostMapping("/create-user")
    public ResponseEntity<UserDetailResponse> createAdmin(@Valid @RequestBody AdminCreateRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminUserImple.adminCreateUser(req));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/update-user/{id}/{status}")
    public UserDetailResponse updateUserStatus(@PathVariable Long id, @PathVariable("status") UserStatus status){
        return adminUserImple.adjustStatus(id,status);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/update-user/{id}")
    public ResponseEntity<UserDetailResponse> updateUser(@PathVariable Long id, @Valid @RequestBody AdminUpdateRequest request){
        UserDetailResponse updated = adminUserImple.updateUser(id, request);
        return ResponseEntity.ok(updated);
    }
    
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete-user/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
            adminUserImple.deleteUser(id);
            return ResponseEntity.ok().build();
    }

}
