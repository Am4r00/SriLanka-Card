package com.SriLankaCard.entity.carrinhoEntity;

import com.SriLankaCard.entity.produtoEntity.Card;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "carrinho")
public class Carrinho {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long usuarioId;

    @Column(nullable = false)
    private Double valorTotal = 0.0;

    @Column(nullable = false)
    private Integer quantidade = 0;

    @Column(nullable = false)
    private boolean possuiItens = false;

    @Column(nullable = false)
    private LocalDateTime criadoEm = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime atualizadoEm = LocalDateTime.now();

    @OneToMany(
            mappedBy = "carrinho",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<ItemCarrinho> itens = new ArrayList<>();

    public Carrinho() {
    }

    public Carrinho(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public void adicionarItem(Card card, int quantidade){
        if (quantidade <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser maior que zero");
        }
        ItemCarrinho existente = itens.stream()
                .filter(i -> i.getCard().getId().equals(card.getId()))
                .findFirst()
                .orElse(null);
        if (existente == null) {
            ItemCarrinho novo = new ItemCarrinho(this, card , quantidade, card.getValor());
            itens.add(novo);
        } else {
            existente.setQuantidade(existente.getQuantidade() + quantidade);
        }
        trocarAtualizacao();
        atualizarValor();
    }

    public void removerItem(Long id){
        itens.removeIf(i -> i.getCard().getId().equals(id));
        trocarAtualizacao();
        atualizarValor();
    }

    public void limpar(){
        itens.clear();
        trocarAtualizacao();
        atualizarValor();
    }

    private void atualizarValor(){
        double soma = 0;
        int qnt = 0;
        for(ItemCarrinho c: itens){
           soma += c.total();
           qnt += c.getQuantidade();
        }
        this.valorTotal = soma;
        this.quantidade = qnt;
        this.possuiItens = !itens.isEmpty();
    }
    private void trocarAtualizacao() {
        this.atualizadoEm = LocalDateTime.now();
    }
}
