package com.SriLankaCard.repository.carrinho;

import com.SriLankaCard.entity.produtoEntity.carrinho.Carrinho;
import com.SriLankaCard.entity.userEntity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CarrinhoRepository extends JpaRepository<Carrinho, Long> {
    Optional<Carrinho> findByUsuarioCarrinho(User user);
}
