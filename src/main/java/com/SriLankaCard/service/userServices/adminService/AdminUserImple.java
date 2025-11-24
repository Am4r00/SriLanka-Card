package com.SriLankaCard.service.userServices.adminService;

import com.SriLankaCard.dto.request.user.admin.AdminCreateRequest;
import com.SriLankaCard.dto.request.user.admin.AdminUpdateRequest;
import com.SriLankaCard.dto.response.user.UserDetailResponse;
import com.SriLankaCard.dto.response.user.UserResponse;
import com.SriLankaCard.entity.userEntity.User;
import com.SriLankaCard.entity.userEntity.enums.Funcao;
import com.SriLankaCard.entity.userEntity.enums.UserStatus;
import com.SriLankaCard.exception.dominio.UserNotFoundException;
import com.SriLankaCard.exception.negocio.EmailAlreadyUsedException;
import com.SriLankaCard.exception.negocio.InvalidArgumentsException;
import com.SriLankaCard.mapper.UserMapper;
import com.SriLankaCard.repository.userRepository.UserRepository;
import com.SriLankaCard.service.emailService.EmailService;
import com.SriLankaCard.utils.RegisterValidation;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminUserImple implements AdminUserService{

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private EmailService emailService;

    public AdminUserImple(UserRepository userRepository, PasswordEncoder passwordEncoder, EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    @Override
    @Transactional
    public UserDetailResponse adminCreateUser(AdminCreateRequest user) {

       var filterUser = RegisterValidation.checkRegisterByAdmin(user);
       
       System.out.println("=== SERVICE: AdminCreateUser ===");
       System.out.println("Funções antes do mapper: " + filterUser.getFuncoes());

        if (userRepository.existsByEmailIgnoreCase(user.getEmail())) {
            throw new EmailAlreadyUsedException("O email: " + filterUser.getEmail() + " já esta sendo usado ");
        }

        User novo = UserMapper.toUserByAdminRequest(filterUser);
        novo.setPassword(passwordEncoder.encode(filterUser.getPassword()));
        
        System.out.println("=== ANTES DE SALVAR ===");
        System.out.println("Funções no User: " + novo.getFuncao());
        System.out.println("Status: " + novo.getStatus());

        try {
            try {
                emailService.enviarBoasVindas(user.getEmail(), user.getName());
            } catch (Exception e) {
                System.out.println("⚠️ Falha ao enviar boas-vindas: " + e.getMessage());
            }
            User salvo = userRepository.save(novo);
            System.out.println("=== DEPOIS DE SALVAR ===");
            System.out.println("Funções salvas: " + salvo.getFuncao());
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
    
    @Transactional
    public UserDetailResponse updateUserToAdmin(String email) {
        if (email == null || email.isEmpty()) {
            throw new InvalidArgumentsException("Email é obrigatório");
        }
        
        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado com email: " + email));
        
        System.out.println("=== SERVICE: updateUserToAdmin ===");
        System.out.println("Usuário encontrado: " + user.getEmail());
        System.out.println("Funções atuais: " + user.getFuncao());
        
        // Atualizar funções para ADMIN
        user.setFuncao(new java.util.HashSet<>(java.util.Set.of(Funcao.ADMIN)));
        
        System.out.println("Funções atualizadas para: " + user.getFuncao());
        
        User atualizado = userRepository.save(user);
        
        System.out.println("=== DEPOIS DE SALVAR ===");
        System.out.println("Funções salvas: " + atualizado.getFuncao());
        
        return UserMapper.toUserDetailByUser(atualizado);
    }

    @Override
    @Transactional
    public UserDetailResponse updateUser(Long id, AdminUpdateRequest request) {
        if (id == null || id <= 0) {
            throw new InvalidArgumentsException("O id deve ser um número positivo.");
        }
        
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado"));
        
        // Atualizar apenas os campos que foram fornecidos
        if (request.getName() != null && !request.getName().trim().isEmpty()) {
            user.setName(request.getName().trim());
        }
        
        if (request.getEmail() != null && !request.getEmail().trim().isEmpty()) {
            // Verificar se o email já está em uso por outro usuário
            if (userRepository.existsByEmailIgnoreCase(request.getEmail()) && 
                !user.getEmail().equalsIgnoreCase(request.getEmail())) {
                throw new EmailAlreadyUsedException("O email " + request.getEmail() + " já está sendo usado.");
            }
            user.setEmail(request.getEmail().trim());
        }
        
        if (request.getStatus() != null) {
            user.setStatus(request.getStatus());
        }
        
        User atualizado = userRepository.save(user);
        return UserMapper.toUserDetailByUser(atualizado);
    }
}
