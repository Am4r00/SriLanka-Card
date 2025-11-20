package com.SriLankaCard.dto.response.itemCarrinho;

import lombok.Data;

@Data
public class ItemCarrinhoResponse {

    private Long id;
    private Long produtoId;
    private String nome;
    private Integer quantidade;
    private Double precoUnitario;
    private Double total;
}
