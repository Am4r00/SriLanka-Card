package com.SriLankaCard.dto.response.itemCarrinho;


import lombok.Data;

import java.util.List;

@Data
public class CarrinhoResponse {

    private Long id;
    private Long usuarioId;
    private Double valorTotal;
    private Integer quantidade;
    private boolean possuiItens;

    private List<ItemCarrinhoResponse> itens;
}
