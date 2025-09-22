package com.SriLankaCard.service;

import com.SriLankaCard.dto.request.UserRequest;
import com.SriLankaCard.dto.response.UserResponse;
import com.SriLankaCard.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.ExecutionException;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    private PasswordEncoder passwordEncoder;

    @Transactional
    public UserResponse createUser(UserRequest dto){
        if(dto == null){
            throw new ExecutionException();
        }
    }
}
