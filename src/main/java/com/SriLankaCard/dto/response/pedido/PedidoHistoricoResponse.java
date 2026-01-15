package com.SriLankaCard.dto.response.pedido;

import com.SriLankaCard.entity.pedidoEntity.ItemPedido;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PedidoHistoricoResponse {
    private Long id;
    private Long usuarioId;
    private Double valorTotal;
    private Integer quantidadeTotal;
    private LocalDateTime criadoEm;
    private List<ItemPedidoResponse> itensPedido;
}
