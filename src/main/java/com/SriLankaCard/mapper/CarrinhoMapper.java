package com.SriLankaCard.mapper;

import com.SriLankaCard.dto.response.CarrinhoResponse;
import com.SriLankaCard.entity.produtoEntity.carrinho.Carrinho;
import com.SriLankaCard.entity.produtoEntity.carrinho.ItemCarrinho;
import  com.SriLankaCard.dto.response.ItemResponse;


public class CarrinhoMapper {
    public static CarrinhoResponse toResponse(Carrinho c) {
        var itens = (c.getProdutosNoCarrinho() == null ? java.util.List.<ItemCarrinho>of()
                : c.getProdutosNoCarrinho())
                .stream()
                .map(CarrinhoMapper::toItem)
                .toList();

        return new CarrinhoResponse(
                c.getId(),
                c.getValorTotal(),
                itens
        );
    }

    private static ItemResponse toItem(ItemCarrinho i) {
        // se possível trocar getCard().getId() por getCardId() snapshot
        Long cardId = i.getCard() != null ? i.getCard().getId() : null;

        return new ItemResponse(
                cardId,
                i.getNome(),
                i.getValorUnitario(),
                i.getQuantidade(),
                i.getSubtotal()
        );
    }
}
