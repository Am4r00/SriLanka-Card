package com.SriLankaCard.controller.carrinhoController;

import com.SriLankaCard.dto.request.carrinho.AddItemCarrinhoRequest;
import com.SriLankaCard.dto.response.itemCarrinho.CarrinhoResponse;
import com.SriLankaCard.entity.carrinhoEntity.Carrinho;
import com.SriLankaCard.entity.userEntity.User;
import com.SriLankaCard.repository.userRepository.UserRepository;
import com.SriLankaCard.service.carrinho.CarrinhoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/carrinho")
public class CarrinhoController {

    private final CarrinhoService carrinhoService;
    private final UserRepository userRepository;

    public CarrinhoController(CarrinhoService carrinhoService,
                              UserRepository userRepository) {
        this.carrinhoService = carrinhoService;
        this.userRepository = userRepository;
    }

    private Long getUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            throw new RuntimeException("Usuário não autenticado");
        }

        Object principal = auth.getPrincipal();

        // Caso 1: se um dia você passar a usar sua entidade User como principal
        if (principal instanceof User appUser) {
            return appUser.getId();
        }

        // Caso 2: é o User do Spring ou qualquer outra implementação de UserDetails
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

    //OK!
    @PostMapping
    public ResponseEntity<Void> adicionarItem(@RequestBody @Valid AddItemCarrinhoRequest request){
        Long userID = getUserId();
        carrinhoService.adicionarItem(userID, request);
        return ResponseEntity.ok().build();
    }

    //OK!
    @GetMapping
    public ResponseEntity<CarrinhoResponse> buscarCarrinhoDoUsuario() {
        Long usuarioId = getUserId();
        CarrinhoResponse carrinho = carrinhoService.buscarCarrinhoDoUsuarioDto(usuarioId);
        return ResponseEntity.ok(carrinho);
    }

    //OK!
    @DeleteMapping("/itens/{produtoId}")
    public ResponseEntity<Void> removerItem(@PathVariable Long produtoId) {
        Long usuarioId = getUserId();
        carrinhoService.removerItem(usuarioId, produtoId);
        return ResponseEntity.noContent().build();
    }

    //OK!
    @DeleteMapping
    public ResponseEntity<Void> limparCarrinho() {
        Long usuarioId = getUserId();
        carrinhoService.limparCarrinho(usuarioId);
        return ResponseEntity.noContent().build();
    }

    //OK!
    @GetMapping("/total")
    public ResponseEntity<Map<String, Object>> buscarTotais() {
        Long usuarioId = getUserId();
        Double valorTotal = carrinhoService.totalValor(usuarioId);
        Integer quantidadeTotal = carrinhoService.totalQuantidade(usuarioId);

        Map<String, Object> body = new HashMap<>();
        body.put("valorTotal", valorTotal);
        body.put("quantidadeTotal", quantidadeTotal);

        return ResponseEntity.ok(body);
    }
}
