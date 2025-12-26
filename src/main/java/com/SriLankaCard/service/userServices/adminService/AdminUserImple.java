package com.SriLankaCard.service.userServices.adminService;

import com.SriLankaCard.dto.request.user.admin.AdminCreateRequest;
import com.SriLankaCard.dto.request.user.admin.AdminUpdateRequest;
import com.SriLankaCard.dto.response.user.UserDetailResponse;
import com.SriLankaCard.dto.response.user.UserResponse;
import com.SriLankaCard.entity.userEntity.User;
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
        if (userRepository.existsByEmailIgnoreCase(user.getEmail())) {
            throw new EmailAlreadyUsedException("O email: " + filterUser.getEmail() + " já esta sendo usado ");
        }

        filterUser.setStatus(user.getStatus() != null ? user.getStatus() : UserStatus.ATIVO);

        if(user.getFuncoes().isEmpty() || user.getFuncoes() == null){
            throw new InvalidArgumentsException("Função é obrigatória ");
        }
        filterUser.setFuncoes(user.getFuncoes());

        User novo = UserMapper.toUserByAdminRequest(filterUser);
        novo.setPassword(passwordEncoder.encode(filterUser.getPassword()));
        
        emailService.enviarBoasVindas(user.getEmail(), user.getName()); 
        User salvo = userRepository.save(novo);
            
        return UserMapper.toUserDetailByUser(salvo);
        
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
