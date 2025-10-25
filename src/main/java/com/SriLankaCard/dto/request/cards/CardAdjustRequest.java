package com.SriLankaCard.dto.request.cards;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CardAdjustRequest {
    @NotBlank
    private String nome;

    @NotBlank
    private  String observacoes;

    @NotNull
    @DecimalMin("0.0")
    private Double valor;

    @NotBlank
    private Boolean promocao;
}
