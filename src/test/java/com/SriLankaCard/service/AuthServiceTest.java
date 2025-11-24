package com.SriLankaCard.service;

import com.SriLankaCard.dto.request.user.login.LoginRequest;
import com.SriLankaCard.dto.response.login.LoginResponse;
import com.SriLankaCard.entity.userEntity.User;
import com.SriLankaCard.entity.userEntity.enums.Funcao;
import com.SriLankaCard.entity.userEntity.enums.UserStatus;
import com.SriLankaCard.exception.negocio.InvalidArgumentsException;
import com.SriLankaCard.repository.userRepository.UserRepository;
import com.SriLankaCard.service.jwtServices.AuthService;
import com.SriLankaCard.service.jwtServices.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes unitários para AuthService")
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    private User user;
    private LoginRequest loginRequest;
    private String encodedPassword;
    private String jwtToken;

    @BeforeEach
    void setUp() {
        encodedPassword = "$2a$10$encodedPasswordHash";
        jwtToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.test.token";

        Set<Funcao> funcoes = new HashSet<>();
        funcoes.add(Funcao.ADMIN);

        user = new User();
        user.setId(1L);
        user.setEmail("admin@test.com");
        user.setName("Admin Test");
        user.setPassword(encodedPassword);
        user.setFuncao(funcoes);
        user.setStatus(UserStatus.ATIVO);

        loginRequest = new LoginRequest();
        loginRequest.setEmail("admin@test.com");
        loginRequest.setPassword("senha123");
    }

    @Test
    @DisplayName("Deve fazer login com sucesso quando credenciais são válidas")
    void deveFazerLoginComSucesso() {
        // Arrange
        when(userRepository.findByEmailIgnoreCase(loginRequest.getEmail()))
                .thenReturn(Optional.of(user));
        when(passwordEncoder.matches(loginRequest.getPassword(), user.getPassword()))
                .thenReturn(true);
        when(jwtService.generateToken(user)).thenReturn(jwtToken);

        // Act
        LoginResponse response = authService.login(loginRequest);

        // Assert
        assertNotNull(response);
        assertEquals(jwtToken, response.getToken());
        assertEquals(user.getEmail(), response.getEmail());
        assertEquals(user.getName(), response.getName());
        assertEquals(user.getFuncao(), response.getFuncoes());

        verify(userRepository, times(1)).findByEmailIgnoreCase(loginRequest.getEmail());
        verify(passwordEncoder, times(1)).matches(loginRequest.getPassword(), user.getPassword());
        verify(jwtService, times(1)).generateToken(user);
    }

    @Test
    @DisplayName("Deve lançar exceção quando email não existe")
    void deveLancarExcecaoQuandoEmailNaoExiste() {
        // Arrange
        when(userRepository.findByEmailIgnoreCase(loginRequest.getEmail()))
                .thenReturn(Optional.empty());

        // Act & Assert
        InvalidArgumentsException exception = assertThrows(
                InvalidArgumentsException.class,
                () -> authService.login(loginRequest)
        );

        assertEquals("Email ou senha inválidos", exception.getMessage());
        verify(userRepository, times(1)).findByEmailIgnoreCase(loginRequest.getEmail());
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(jwtService, never()).generateToken(any(User.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando senha está incorreta")
    void deveLancarExcecaoQuandoSenhaIncorreta() {
        // Arrange
        when(userRepository.findByEmailIgnoreCase(loginRequest.getEmail()))
                .thenReturn(Optional.of(user));
        when(passwordEncoder.matches(loginRequest.getPassword(), user.getPassword()))
                .thenReturn(false);

        // Act & Assert
        InvalidArgumentsException exception = assertThrows(
                InvalidArgumentsException.class,
                () -> authService.login(loginRequest)
        );

        assertEquals("Email ou senha inválidos", exception.getMessage());
        verify(userRepository, times(1)).findByEmailIgnoreCase(loginRequest.getEmail());
        verify(passwordEncoder, times(1)).matches(loginRequest.getPassword(), user.getPassword());
        verify(jwtService, never()).generateToken(any(User.class));
    }

    @Test
    @DisplayName("Deve fazer login com usuário comum (não admin)")
    void deveFazerLoginComUsuarioComum() {
        // Arrange
        Set<Funcao> funcoesUsuario = new HashSet<>();
        funcoesUsuario.add(Funcao.USUARIO);
        user.setFuncao(funcoesUsuario);

        when(userRepository.findByEmailIgnoreCase(loginRequest.getEmail()))
                .thenReturn(Optional.of(user));
        when(passwordEncoder.matches(loginRequest.getPassword(), user.getPassword()))
                .thenReturn(true);
        when(jwtService.generateToken(user)).thenReturn(jwtToken);

        // Act
        LoginResponse response = authService.login(loginRequest);

        // Assert
        assertNotNull(response);
        assertEquals(jwtToken, response.getToken());
        assertTrue(response.getFuncoes().contains(Funcao.USUARIO));
        assertFalse(response.getFuncoes().contains(Funcao.ADMIN));
    }
}

