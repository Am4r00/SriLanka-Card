package com.SriLankaCard.dto.response;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ItemResponse {
    private String nome;
    private Double valorUnitario;
    private Integer quantidade;
    private Double subtotal;

    public ItemResponse(Long id, String nome, Double valorUnitario, @NotNull @Min(1) Integer quantidade, Double subtotal) {
    }
}
