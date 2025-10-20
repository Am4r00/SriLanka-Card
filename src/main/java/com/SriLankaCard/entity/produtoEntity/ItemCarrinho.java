package com.SriLankaCard.entity.produtoEntity;

import lombok.Data;

@Data
public class ItemCarrinho {
    private Long id;
    private String nome;
    private Double valorUnitario;
    private Integer quantidade;

    public Double precoTotal(Double valorUnitario, Integer quantidade){
        return valorUnitario * quantidade;
    }
}
