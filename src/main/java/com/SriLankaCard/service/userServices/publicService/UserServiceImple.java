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
import com.SriLankaCard.utils.RegisterValidation;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImple implements UserService {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    public UserServiceImple(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
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
        var filterUser = RegisterValidation.checkRegister(user);

        if (userRepository.existsByEmailIgnoreCase(user.getEmail())) {
            throw new EmailAlreadyUsedException("O email: " + filterUser.getEmail() + " já esta sendo usado ");
        }

        User novo = UserMapper.toUserRegister(user);

        novo.setFuncao(new HashSet<Funcao>(List.of(Funcao.USUARIO)));
        novo.setStatus(UserStatus.ATIVO);
        novo.setPassword(passwordEncoder.encode(user.getPassword()));

        User salvo = userRepository.save(novo);
        return UserMapper.toUserResponse(salvo);
    }

    @Override
    @Transactional
    public List<UserResponse> findAll() {
        List<User> listaUsuarios = userRepository.findAll();
        if (listaUsuarios.isEmpty()) {
            throw new ListIsEmptyException("A lista está vazia ");
        }
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
