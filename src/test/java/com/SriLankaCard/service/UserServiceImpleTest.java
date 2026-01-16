package com.SriLankaCard.service;

import com.SriLankaCard.dto.request.user.userPublic.RegisterUserRequest;
import com.SriLankaCard.dto.response.user.UserResponse;
import com.SriLankaCard.entity.userEntity.User;
import com.SriLankaCard.entity.userEntity.enums.Funcao;
import com.SriLankaCard.entity.userEntity.enums.UserStatus;
import com.SriLankaCard.exception.dominio.ListIsEmptyException;
import com.SriLankaCard.exception.dominio.UserNotFoundException;
import com.SriLankaCard.exception.negocio.EmailAlreadyUsedException;
import com.SriLankaCard.repository.userRepository.UserRepository;
import com.SriLankaCard.service.userServices.publicService.UserServiceImple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes unitários para UserServiceImple")
class UserServiceImpleTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImple userService;

    private RegisterUserRequest registerRequest;
    private User user;
    private String encodedPassword;

    @BeforeEach
    void setUp() {
        encodedPassword = "$2a$10$encodedPasswordHash";

        registerRequest = new RegisterUserRequest();
        registerRequest.setName("Test User");
        registerRequest.setEmail("test@example.com");
        registerRequest.setPassword("senha123");

        Set<Funcao> funcoes = new HashSet<>();
        funcoes.add(Funcao.USUARIO);

        user = new User();
        user.setId(1L);
        user.setName("Test User");
        user.setEmail("test@example.com");
        user.setPassword(encodedPassword);
        user.setFuncao(funcoes);
        user.setStatus(UserStatus.ATIVO);
    }

    @Test
    @DisplayName("Deve criar usuário com sucesso")
    void deveCriarUsuarioComSucesso() {

        when(userRepository.existsByEmailIgnoreCase(registerRequest.getEmail()))
                .thenReturn(false);
        when(passwordEncoder.encode(registerRequest.getPassword()))
                .thenReturn(encodedPassword);
        when(userRepository.save(any(User.class))).thenReturn(user);


        UserResponse response = userService.createUser(registerRequest);


        assertNotNull(response);
        assertEquals(user.getEmail(), response.getEmail());
        assertEquals(user.getName(), response.getNome());

        verify(userRepository, times(1)).existsByEmailIgnoreCase(registerRequest.getEmail());
        verify(passwordEncoder, times(1)).encode(registerRequest.getPassword());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando email já está em uso")
    void deveLancarExcecaoQuandoEmailJaExiste() {

        when(userRepository.existsByEmailIgnoreCase(registerRequest.getEmail()))
                .thenReturn(true);


        EmailAlreadyUsedException exception = assertThrows(
                EmailAlreadyUsedException.class,
                () -> userService.createUser(registerRequest)
        );

        assertTrue(exception.getMessage().contains("já esta sendo usado"));
        verify(userRepository, times(1)).existsByEmailIgnoreCase(registerRequest.getEmail());
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Deve retornar lista de usuários quando existem usuários")
    void deveRetornarListaDeUsuarios() {

        List<User> users = new ArrayList<>();
        users.add(user);

        User user2 = new User();
        user2.setId(2L);
        user2.setName("User 2");
        user2.setEmail("user2@example.com");
        user2.setPassword(encodedPassword);
        user2.setFuncao(new HashSet<>(Set.of(Funcao.USUARIO)));
        user2.setStatus(UserStatus.ATIVO);
        users.add(user2);

        when(userRepository.findAll()).thenReturn(users);

        List<UserResponse> response = userService.findAll();


        assertNotNull(response);
        assertEquals(2, response.size());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve lançar exceção quando lista está vazia")
    void deveLancarExcecaoQuandoListaVazia() {

        when(userRepository.findAll()).thenReturn(new ArrayList<>());


        ListIsEmptyException exception = assertThrows(
                ListIsEmptyException.class,
                () -> userService.findAll()
        );

        assertTrue(exception.getMessage().contains("A lista está vazia"));
        verify(userRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve encontrar usuário por email com sucesso")
    void deveEncontrarUsuarioPorEmail() {

        String email = "test@example.com";
        when(userRepository.findByEmailIgnoreCase(email))
                .thenReturn(Optional.of(user));


        UserResponse response = userService.findByEmail(email);


        assertNotNull(response);
        assertEquals(user.getEmail(), response.getEmail());
        assertEquals(user.getName(), response.getNome());
        verify(userRepository, times(1)).findByEmailIgnoreCase(email);
    }

    @Test
    @DisplayName("Deve lançar exceção quando usuário não encontrado por email")
    void deveLancarExcecaoQuandoUsuarioNaoEncontrado() {

        String email = "naoexiste@example.com";
        when(userRepository.findByEmailIgnoreCase(email))
                .thenReturn(Optional.empty());


        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> userService.findByEmail(email)
        );

        assertEquals("Usuário não encontrado", exception.getMessage());
        verify(userRepository, times(1)).findByEmailIgnoreCase(email);
    }

    @Test
    @DisplayName("Deve criar usuário com role USUARIO por padrão")
    void deveCriarUsuarioComRoleUsuario() {

        when(userRepository.existsByEmailIgnoreCase(registerRequest.getEmail()))
                .thenReturn(false);
        when(passwordEncoder.encode(registerRequest.getPassword()))
                .thenReturn(encodedPassword);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            assertTrue(savedUser.getFuncao().contains(Funcao.USUARIO));
            assertEquals(UserStatus.ATIVO, savedUser.getStatus());
            return savedUser;
        });


        userService.createUser(registerRequest);


        verify(userRepository, times(1)).save(any(User.class));
    }
}

