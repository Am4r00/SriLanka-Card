package com.SriLankaCard.dto.response;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ItemResponse {
    Long cardId;
    String nome;
    Double valorUnitario;
    Integer quantidade;
    Double subtotal;

    public ItemResponse(Long id, String nome, Double valorUnitario, @NotNull @Min(1) Integer quantidade, Double subtotal) {
    }
}
