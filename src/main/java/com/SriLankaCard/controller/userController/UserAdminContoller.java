package com.SriLankaCard.controller.userController;

import com.SriLankaCard.dto.request.user.admin.AdminCreateRequest;
import com.SriLankaCard.dto.response.user.UserDetailResponse;
import com.SriLankaCard.entity.userEntity.enums.Funcao;
import com.SriLankaCard.entity.userEntity.enums.UserStatus;
import com.SriLankaCard.service.userServices.adminService.AdminUserImple;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/admin")
public class UserAdminContoller {

    private final AdminUserImple adminUserImple;

    private static final String DEFAULT_ADMIN_PASSWORD = "admin12345678";

    public UserAdminContoller(AdminUserImple adminUserImple) {
        this.adminUserImple = adminUserImple;
    }

    @PostMapping("/create-user")
    public ResponseEntity<UserDetailResponse> createAdminPublic(@RequestBody AdminCreateRequest req) {
        System.out.println("=== ENDPOINT /admin/create-user CHAMADO ===");
        System.out.println("Request recebido - Nome: " + req.getName());
        System.out.println("Request recebido - Email: " + req.getEmail());
        System.out.println("Request recebido - Password: " + (req.getPassword() != null ? "***" : "null"));
        System.out.println("Request recebido - Status: " + req.getStatus());
        System.out.println("Request recebido - Funções ANTES: " + req.getFuncoes());
        
        // Definir valores padrão para ADMIN
        if (req.getPassword() == null || req.getPassword().isEmpty()) {
            req.setPassword(DEFAULT_ADMIN_PASSWORD);
            System.out.println("Password definido como padrão: " + DEFAULT_ADMIN_PASSWORD);
        }
        if (req.getStatus() == null) {
            req.setStatus(UserStatus.ATIVO);
            System.out.println("Status definido como ATIVO");
        }
        
        // FORÇAR como ADMIN - não importa o que veio no request
        req.setFuncoes(new java.util.HashSet<>(java.util.Set.of(Funcao.ADMIN)));
        
        System.out.println("=== VALORES DEFINIDOS PARA ADMIN ===");
        System.out.println("Nome: " + req.getName());
        System.out.println("Email: " + req.getEmail());
        System.out.println("Password: " + req.getPassword());
        System.out.println("Funções FORÇADAS: " + req.getFuncoes());
        System.out.println("Status: " + req.getStatus());

        UserDetailResponse created = adminUserImple.adminCreateUser(req);
        
        System.out.println("=== RESULTADO DA CRIAÇÃO ===");
        System.out.println("Email criado: " + created.getEmail());
        System.out.println("Funções retornadas: " + created.getFuncoes());
        System.out.println("Status: " + created.getStatus());
        
        // Verificar se realmente foi salvo como ADMIN
        if (created.getFuncoes() != null && created.getFuncoes().contains(Funcao.ADMIN)) {
            System.out.println("✅ CONFIRMADO: Usuário foi criado como ADMIN");
        } else {
            System.out.println("❌ ERRO: Usuário NÃO foi criado como ADMIN! Funções: " + created.getFuncoes());
        }
        
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
    
    @PostMapping("/create-user-common")
    public ResponseEntity<UserDetailResponse> createCommonUser(@RequestBody AdminCreateRequest req) {
        // Criar usuário comum (USUARIO)
        if (req.getPassword() == null || req.getPassword().isEmpty()) {
            throw new RuntimeException("Senha é obrigatória para usuário comum");
        }
        if (req.getStatus() == null) {
            req.setStatus(UserStatus.ATIVO);
        }
        req.setFuncoes(Set.of(Funcao.USUARIO));

        UserDetailResponse created = adminUserImple.adminCreateUser(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/update-user/{id}/{status}")
    public UserDetailResponse updateUser(@PathVariable Long id, @PathVariable("status") UserStatus status){
        return adminUserImple.adjustStatus(id,status);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create-employee")
    public ResponseEntity<UserDetailResponse> createEmployee(@Valid @RequestBody AdminCreateRequest req) {

        req.setPassword(DEFAULT_ADMIN_PASSWORD);
        req.setStatus(UserStatus.ATIVO);
        req.setFuncoes(Set.of(Funcao.ESTOQUISTA));
        UserDetailResponse created = adminUserImple.adminCreateUser(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
    
    // Endpoint de teste para criar admin rapidamente
    @PostMapping("/test-create-admin")
    public ResponseEntity<UserDetailResponse> testCreateAdmin(@RequestParam(required = false, defaultValue = "admin@test.com") String email,
                                                              @RequestParam(required = false, defaultValue = "Admin Teste") String name) {
        AdminCreateRequest req = new AdminCreateRequest();
        req.setName(name);
        req.setEmail(email);
        req.setPassword(DEFAULT_ADMIN_PASSWORD);
        req.setStatus(UserStatus.ATIVO);
        req.setFuncoes(Set.of(Funcao.ADMIN));
        
        System.out.println("=== TESTE: Criando ADMIN ===");
        System.out.println("Nome: " + req.getName());
        System.out.println("Email: " + req.getEmail());
        System.out.println("Funções: " + req.getFuncoes());
        
        try {
            UserDetailResponse created = adminUserImple.adminCreateUser(req);
            System.out.println("=== TESTE: ADMIN criado com sucesso ===");
            System.out.println("Funções salvas: " + created.getFuncoes());
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (Exception e) {
            System.out.println("=== TESTE: Erro ao criar ADMIN ===");
            System.out.println("Erro: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // Endpoint para atualizar função de usuário existente para ADMIN (temporário para correção)
    @PostMapping("/update-user-to-admin")
    public ResponseEntity<UserDetailResponse> updateUserToAdmin(@RequestParam String email) {
        System.out.println("=== ATUALIZANDO USUÁRIO PARA ADMIN ===");
        System.out.println("Email: " + email);
        
        try {
            UserDetailResponse updated = adminUserImple.updateUserToAdmin(email);
            System.out.println("=== USUÁRIO ATUALIZADO PARA ADMIN ===");
            System.out.println("Funções atualizadas: " + updated.getFuncoes());
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            System.out.println("=== ERRO AO ATUALIZAR ===");
            System.out.println("Erro: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete-user/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            adminUserImple.deleteUser(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            System.out.println("=== ERRO AO DELETAR USUÁRIO ===");
            System.out.println("Erro: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao deletar usuário: " + e.getMessage());
        }
    }

}
