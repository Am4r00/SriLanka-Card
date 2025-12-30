package com.SriLankaCard.service.userServices.publicService;

import com.SriLankaCard.dto.request.user.userPublic.RegisterUserRequest;
import com.SriLankaCard.dto.response.user.UserResponse;
import com.SriLankaCard.entity.userEntity.User;
import com.SriLankaCard.entity.userEntity.enums.Funcao;
import com.SriLankaCard.entity.userEntity.enums.UserStatus;
import com.SriLankaCard.exception.dominio.ListIsEmptyException;
import com.SriLankaCard.exception.dominio.UserNotFoundException;
import com.SriLankaCard.exception.negocio.EmailAlreadyUsedException;
import com.SriLankaCard.mapper.UserMapper;
import com.SriLankaCard.repository.userRepository.UserRepository;
import com.SriLankaCard.service.emailService.EmailService;
import com.SriLankaCard.utils.RegisterUtils;
import com.SriLankaCard.utils.ValidationUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImple implements UserService {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private EmailService emailService;

    public UserServiceImple(UserRepository userRepository, PasswordEncoder passwordEncoder, EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    @Transactional
    public UserResponse findByEmail(String email) {
        var user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado"));
        return UserMapper.toUserResponse(user);
    }
    @Override
    @Transactional
    public UserResponse createUser(RegisterUserRequest user) {
        var filterUser = RegisterUtils.checkRegister(user);

        if (userRepository.existsByEmailIgnoreCase(filterUser.getEmail())) {
            throw new EmailAlreadyUsedException("O email: " + filterUser.getEmail() + " já esta sendo usado ");
        }

        User novo = UserMapper.toUserRegister(filterUser);

        novo.setFuncao(new HashSet<Funcao>(List.of(Funcao.USUARIO)));
        novo.setStatus(UserStatus.ATIVO);
        novo.setPassword(passwordEncoder.encode(user.getPassword()));

        try {
            emailService.enviarBoasVindas(user.getEmail(), user.getName());
        } catch (Exception e) {
            System.out.println("⚠️ Falha ao enviar boas-vindas: " + e.getMessage());
        }

        User salvo = userRepository.save(novo);
        return UserMapper.toUserResponse(salvo);
    }

    @Override
    @Transactional
    public List<UserResponse> findAll() {
        List<User> listaUsuarios = userRepository.findAll();
        ValidationUtils.validateListNotEmpty(listaUsuarios);

        return mapToUserDetailDTOList(listaUsuarios);
    }

    private List<UserResponse> mapToUserDetailDTOList(List<User> userList) {
        return userList.stream()
                .map(UserMapper::toUserResponse)
                .collect(Collectors.toList());
    }

    private String encodePassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }
}
