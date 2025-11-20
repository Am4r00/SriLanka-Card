package com.SriLankaCard.dto.response.produtoResponse;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class CardResponse {
    private String nome;
    private String observacoes;
    private Integer quantidade;
    private Double valor;
    private boolean promocao;
}
