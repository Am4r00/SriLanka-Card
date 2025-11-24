package com.SriLankaCard.controller.pedidoController;

import com.SriLankaCard.entity.userEntity.User;
import com.SriLankaCard.repository.userRepository.UserRepository;
import com.SriLankaCard.service.pedidoService.PedidoService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final PedidoService pedidoService;
    private final UserRepository userRepository;

    public PedidoController(PedidoService pedidoService, UserRepository userRepository) {
        this.pedidoService = pedidoService;
        this.userRepository = userRepository;
    }

    private Long getUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            throw new RuntimeException("Usuário não autenticado");
        }

        Object principal = auth.getPrincipal();

        if (principal instanceof User appUser) {
            return appUser.getId();
        }

        String username;
        if (principal instanceof UserDetails springUser) {
            username = springUser.getUsername(); // no seu caso é o email
        } else if (principal instanceof String s) {
            username = s;
        } else {
            throw new IllegalStateException("Tipo de principal inesperado: " + principal.getClass());
        }

        return userRepository.findByEmail(username)
                .orElseThrow(() -> new IllegalStateException("Usuário não encontrado: " + username))
                .getId();
    }

    @PostMapping("/finalizar")
    public ResponseEntity<Void> finalizarPedido() {
        Long usuarioId = getUserId();
        pedidoService.finalizarPedido(usuarioId);

        return ResponseEntity.ok().build();
    }
}

