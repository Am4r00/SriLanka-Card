package com.SriLankaCard.controller.userController;

import com.SriLankaCard.dto.request.user.admin.AdminCreateRequest;
import com.SriLankaCard.dto.response.user.UserDetailResponse;
import com.SriLankaCard.entity.userEntity.enums.Funcao;
import com.SriLankaCard.entity.userEntity.enums.UserStatus;
import com.SriLankaCard.service.userServices.adminService.AdminUserImple;
import com.SriLankaCard.repository.userRepository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/admin")
public class UserAdminContoller {

    private final AdminUserImple adminUserImple;
    private final UserRepository userRepository;

    // senha padrão fixa
    private static final String DEFAULT_ADMIN_PASSWORD = "SenhaFixa123";

    public UserAdminContoller(AdminUserImple adminUserImple, UserRepository userRepository) {
        this.adminUserImple = adminUserImple;
        this.userRepository = userRepository;
    }

    @PostMapping("/create-user")
    public ResponseEntity<UserDetailResponse> createAdminPublic(@RequestBody AdminCreateRequest req) {
        // força campos importantes
        req.setPassword(DEFAULT_ADMIN_PASSWORD);
        req.setStatus(UserStatus.ATIVO);
        req.setFuncoes(Set.of(Funcao.ADMIN));

        UserDetailResponse created = adminUserImple.adminCreateUser(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // Endpoints administrativos restantes devem ser protegidos
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/update-user/{id}/{status}")
    public UserDetailResponse updateUser(@PathVariable Long id, @PathVariable("status") UserStatus status){
        return adminUserImple.adjustStatus(id,status);
    }
}
