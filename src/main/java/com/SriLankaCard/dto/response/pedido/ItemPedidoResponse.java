package com.SriLankaCard.dto.response.pedido;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemPedidoResponse {
    private String nomeProduto;
    private Integer quantidade;
    private Double precoUnitario;
    private Double total;
}
