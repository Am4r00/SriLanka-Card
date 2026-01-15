package com.SriLankaCard.service.pedidoService;

import com.SriLankaCard.dto.response.pedido.PedidoHistoricoResponse;

import java.util.List;

public interface PedidoService {
    void finalizarPedido(Long usuarioId);
    List<PedidoHistoricoResponse> listarPedidosUsuario(Long id);
    List<PedidoHistoricoResponse> listarTodosPedidos();
}
