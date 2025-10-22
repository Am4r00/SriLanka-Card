package com.SriLankaCard.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class CarrinhoResponse {
    Long id;
    Double valorTotal;
    List<ItemResponse> itens;

    public CarrinhoResponse(Long id, Double valorTotal, List<ItemResponse> itens) {
    }
}
