package com.SriLankaCard.controller.userController;

import com.SriLankaCard.dto.request.user.userPublic.RegisterUserRequest;
import com.SriLankaCard.dto.response.user.UserResponse;
import com.SriLankaCard.service.userServices.publicService.UserServiceImple;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserServiceImple userServiceImple;


    @PostMapping("/create-user")
    public UserResponse createUser(@Valid @RequestBody RegisterUserRequest dto) {
        return userServiceImple.createUser(dto);
    }


    @GetMapping("/list")
    public List<UserResponse> findAllUsers(Authentication authentication) {
        return userServiceImple.findAll();
    }


    @GetMapping("/me")
    public UserResponse getCurrentUser(Authentication authentication) {
        String email = authentication.getName();
        return userServiceImple.findByEmail(email);
    }
}
