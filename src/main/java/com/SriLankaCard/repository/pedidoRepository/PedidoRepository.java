package com.SriLankaCard.repository.pedidoRepository;

import com.SriLankaCard.entity.pedidoEntity.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    List<Pedido> findByUsuarioIdOrderByCriadoEmDesc(Long usuarioId);
}
