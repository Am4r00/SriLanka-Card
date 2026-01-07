package com.SriLankaCard.dto.request.cards;

import com.SriLankaCard.entity.produtoEntity.CardCategory;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CardRequest {
    @NotBlank
    private String nome;

    @NotBlank
    private String observacoes;

    @NotNull
    @DecimalMin("0.0")
    private Double valor;

    @NotNull
    private CardCategory category;

    @NotNull
    private boolean promocao;

}
