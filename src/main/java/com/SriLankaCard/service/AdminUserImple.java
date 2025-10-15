package com.SriLankaCard.service;

import com.SriLankaCard.dto.request.AdminCreateRequest;
import com.SriLankaCard.dto.response.UserDetailResponse;
import com.SriLankaCard.dto.response.UserResponse;
import com.SriLankaCard.entity.User;
import com.SriLankaCard.entity.userEntity.enums.UserStatus;
import com.SriLankaCard.exception.dominio.UserNotFoundException;
import com.SriLankaCard.exception.negocio.EmailAlreadyUsedException;
import com.SriLankaCard.exception.negocio.InvalidArgumentsException;
import com.SriLankaCard.mapper.UserMapper;
import com.SriLankaCard.repository.UserRepository;
import com.SriLankaCard.utils.RegisterValidation;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminUserImple implements AdminUserService{

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    public AdminUserImple (UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    @Transactional
    public UserDetailResponse adminCreateUser(AdminCreateRequest user) {

       var filterUser = RegisterValidation.checkRegisterByAdmin(user);

        if (userRepository.existsByEmailIgnoreCase(user.getEmail())) {
            throw new EmailAlreadyUsedException("O email: " + filterUser.getEmail() + " já esta sendo usado ");
        }

        User novo = UserMapper.toUserByAdminRequest(filterUser);
        novo.setPassword(passwordEncoder.encode(filterUser.getPassword()));

        try {
            User salvo = userRepository.save(novo);
            return UserMapper.toUserDetailByUser(salvo);
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            throw new EmailAlreadyUsedException("O email " + filterUser.getEmail() + " já está sendo usado.");
        }
    }

    @Override
    @Transactional
    public UserDetailResponse adjustStatus(Long id, UserStatus status) {
        if(id == null || id <= 0 || status == null){
            throw new InvalidArgumentsException("Os argumentos precisam ser válidos ");
        }
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("usário não encontrado "));

        user.setStatus(status);
        User atualizado = userRepository.save(user);

        return UserMapper.toUserDetailByUser(atualizado);
    }

    @Override
    @Transactional
    public UserResponse deleteUser(Long id) {
        if (id == null || id <= 0) {
            throw new InvalidArgumentsException("O id deve ser um número positivo.");
        }
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("usário não encontrado "));

        userRepository.delete(user);
        return UserMapper.toUserResponse(user);
    }
}
