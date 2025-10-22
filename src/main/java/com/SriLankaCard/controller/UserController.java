package com.SriLankaCard.controller;

import com.SriLankaCard.dto.request.RegisterUserRequest;
import com.SriLankaCard.dto.response.UserResponse;
import com.SriLankaCard.service.UserServiceImple;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserServiceImple userServiceImple;

    @GetMapping("/list")
    public List<UserResponse> findAllUsers(){
        return userServiceImple.findAll();
    }

    @PostMapping("/create-user")
    public UserResponse createUser(@Valid @RequestBody RegisterUserRequest dto) {
        return userServiceImple.createUser(dto);
    }

}