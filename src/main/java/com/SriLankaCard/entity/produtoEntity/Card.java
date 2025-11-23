package com.SriLankaCard.entity.produtoEntity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;

@Entity
@Data
@Table(name="cards")
public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "card_nome", nullable = false)
    @NotBlank
    private String nome;

    @Column(name = "card_observacoes", nullable = false, length = 1000)
    @NotBlank
    private String observacoes;

    @Column(name = "card_valor",nullable = false)
    @NotNull
    @DecimalMin("0.0")
    private Double valor;

    @Column(name = "card_promocao",nullable = false)
    private boolean promocao;

    @Column(name = "card_avaliacao",nullable = false)
    @NotNull
    @Min(0) @Max(5)
    private Integer avaliacao;

}
