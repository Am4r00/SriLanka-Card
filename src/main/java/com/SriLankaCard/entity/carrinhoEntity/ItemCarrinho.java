package com.SriLankaCard.entity.carrinhoEntity;

import com.SriLankaCard.entity.produtoEntity.Card;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "item_carrinho")
public class ItemCarrinho {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "carrinho_id", nullable = false)
    @JsonIgnore
    private Carrinho carrinho;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "produto_id", nullable = false)
    private Card card;

    @Column(nullable = false)
    private Integer quantidade;

    @Column(nullable = false)
    private Double precoUnitario;


    public ItemCarrinho() {
    }

    public ItemCarrinho(Carrinho carrinho, Card card, Integer quantidade, Double precoUnitario) {
        this.carrinho = carrinho;
        this.card = card;
        this.quantidade = quantidade;
        this.precoUnitario = precoUnitario;
    }

    public Double total() {
        return precoUnitario * quantidade;
    }
}
