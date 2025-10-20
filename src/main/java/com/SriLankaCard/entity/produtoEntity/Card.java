package com.SriLankaCard.entity.produtoEntity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Entity
@Data
@Table(name="cards")
public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "card_name")
    @NotBlank
    private String name;

    @Column(name = "card_observacoes")
    @NotBlank
    private String observacoes;

    @Column(name = "card_quantidade")
    @NotBlank
    private Integer quantidade;

    @Column(name = "card_serial")
    @NotBlank
    private String serial;

    @Column(name = "card_valor")
    @NotBlank
    private Double valor;

    @Column(name = "card_promocao")
    @NotBlank
    private boolean promocao;

    @Column(name = "card_avaliacao")
    @NotBlank
    private Integer avaliacao;

}
