package com.SriLankaCard.entity.produtoEntity.carrinho;

import com.SriLankaCard.entity.produtoEntity.Card;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
@Entity
@Table(name = "itens_carrinho")
@Data
public class ItemCarrinho {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private Double valorUnitario;

    @Column(nullable = false)
    @NotNull @Min(1)
    private Integer quantidade;

    @ManyToOne(optional = false,fetch = FetchType.LAZY)
    private Carrinho carrinho;

    @ManyToOne(optional = false,fetch = FetchType.LAZY)
    private Card card;

    @Transient
    public Double getSubtotal(){
        return valorUnitario * quantidade;
    }
}
