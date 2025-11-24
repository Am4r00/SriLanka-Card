package com.SriLankaCard.controller;

import com.SriLankaCard.config.TestSecurityConfig;
import com.SriLankaCard.dto.request.user.userPublic.RegisterUserRequest;
import com.SriLankaCard.dto.response.user.UserResponse;
import com.SriLankaCard.service.userServices.publicService.UserServiceImple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestSecurityConfig.class)
@DisplayName("Testes unitários para UserController")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserServiceImple userServiceImple;

    private RegisterUserRequest registerRequest;
    private UserResponse userResponse;
    private List<UserResponse> userList;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterUserRequest();
        registerRequest.setName("Test User");
        registerRequest.setEmail("test@example.com");
        registerRequest.setPassword("senha123");

        userResponse = new UserResponse();
        userResponse.setId(1L);
        userResponse.setNome("Test User");
        userResponse.setEmail("test@example.com");

        userList = new ArrayList<>();
        userList.add(userResponse);
    }

    @Test
    @DisplayName("Deve retornar página de signup")
    void deveRetornarPaginaSignup() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/users/signup"))
                .andExpect(status().isOk())
                .andExpect(view().name("signup"))
                .andExpect(model().attributeExists("usuario"));
    }

    @Test
    @DisplayName("Deve criar usuário e redirecionar para login")
    void deveCriarUsuarioERedirecionar() throws Exception {
        // Arrange
        when(userServiceImple.createUser(any(RegisterUserRequest.class)))
                .thenReturn(userResponse);

        // Act & Assert
        mockMvc.perform(post("/users/create-user")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("name", registerRequest.getName())
                        .param("email", registerRequest.getEmail())
                        .param("password", registerRequest.getPassword()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    @DisplayName("Deve listar todos os usuários")
    @WithMockUser(username = "admin@test.com", roles = {"ADMIN"})
    void deveListarTodosUsuarios() throws Exception {
        // Arrange
        when(userServiceImple.findAll()).thenReturn(userList);

        // Act & Assert
        mockMvc.perform(get("/users/list"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].email").value("test@example.com"))
                .andExpect(jsonPath("$[0].nome").value("Test User"));
    }

    @Test
    @DisplayName("Deve retornar usuário atual autenticado")
    @WithMockUser(username = "test@example.com", roles = {"USER"})
    void deveRetornarUsuarioAtual() throws Exception {
        // Arrange
        when(userServiceImple.findByEmail("test@example.com"))
                .thenReturn(userResponse);

        // Act & Assert
        mockMvc.perform(get("/users/me"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.nome").value("Test User"));
    }

    @Test
    @DisplayName("Deve retornar erro quando usuário não encontrado")
    @WithMockUser(username = "naoexiste@example.com", roles = {"USER"})
    void deveRetornarErroQuandoUsuarioNaoEncontrado() throws Exception {
        // Arrange
        when(userServiceImple.findByEmail(anyString()))
                .thenThrow(new com.SriLankaCard.exception.dominio.UserNotFoundException("Usuário não encontrado"));

        // Act & Assert
        mockMvc.perform(get("/users/me"))
                .andExpect(status().isNotFound());
    }
}

