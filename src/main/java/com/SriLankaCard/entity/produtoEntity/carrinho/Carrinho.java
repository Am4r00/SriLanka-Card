package com.SriLankaCard.entity.produtoEntity.carrinho;

import com.SriLankaCard.entity.produtoEntity.Card;
import com.SriLankaCard.entity.userEntity.User;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
@Entity
@Table(name = "carrinhos")
@Data
public class Carrinho {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private User usuarioCarrinho;

    @OneToMany(
            mappedBy = "carrinho",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<ItemCarrinho> produtosNoCarrinho = new ArrayList<>();

    @Transient
    public Double getValorTotal() {
        return produtosNoCarrinho.stream()
                .mapToDouble(ItemCarrinho::getSubtotal)
                .sum();
    }

    public void adicionarItem(Card card, int quantidade) {
        ItemCarrinho existente = produtosNoCarrinho.stream()
                .filter(i -> i.getCard().getId().equals(card.getId()))
                .findFirst()
                .orElse(null);

        if (existente == null) {
            ItemCarrinho novo = new ItemCarrinho();
            novo.setCard(card);
            novo.setNome(card.getNome());
            novo.setValorUnitario(card.getValor());
            novo.setQuantidade(quantidade);
            novo.setCarrinho(this);
            produtosNoCarrinho.add(novo);
        } else {
            existente.setQuantidade(existente.getQuantidade() + quantidade);
        }
    }

    public void removerItemPorCardId(Long cardId) {
        produtosNoCarrinho.removeIf(i -> i.getCard().getId().equals(cardId));
    }

    public void limpar() {
        produtosNoCarrinho.clear();
    }

}
