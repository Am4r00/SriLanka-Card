package com.SriLankaCard.repository.carrinhoRepository;

import com.SriLankaCard.entity.carrinhoEntity.Carrinho;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CarrinhoRepository extends JpaRepository<Carrinho, Long> {
    Optional<Carrinho> findByUsuarioId(Long usuarioId);
}
