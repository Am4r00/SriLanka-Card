package com.SriLankaCard.repository.pedidoRepository;

import com.SriLankaCard.entity.pedidoEntity.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
}
