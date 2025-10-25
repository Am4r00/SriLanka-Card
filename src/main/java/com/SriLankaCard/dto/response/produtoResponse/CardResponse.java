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
    @NotBlank
    private String nome;

    @NotBlank
    private String observacoes;

    @NotBlank
    @Min(0)
    private Integer quantidade;

    @NotNull
    @DecimalMin("0.0")
    private Double valor;

    @NotBlank
    private boolean promocao;
}
