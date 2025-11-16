package com.SriLankaCard.mapper;

import com.SriLankaCard.dto.request.cards.CardRequest;
import com.SriLankaCard.dto.response.produtoResponse.CardResponse;
import com.SriLankaCard.entity.produtoEntity.Card;
import com.SriLankaCard.entity.carrinhoEntity.ItemCarrinho;

public class CardMapper {

    public static Card toCardByCardRequest(CardRequest request){
        Card card = new Card();
        card.setNome(request.getNome());
        card.setObservacoes(request.getObservacoes());
        card.setQuantidade(request.getQuantidade());
        card.setValor(request.getValor());
        card.setPromocao(request.isPromocao());

        return card;
    }

    public static CardResponse toCardResponseByCard(Card card){
        CardResponse response = new CardResponse();
        response.setNome(card.getNome());
        response.setObservacoes(card.getObservacoes());
        response.setQuantidade(card.getQuantidade());
        response.setValor(card.getValor());
        response.setPromocao(card.isPromocao());

        return response;
    }

    public static ItemCarrinho toItemCarrinhoByCard(Card card){
        ItemCarrinho itemCarrinho = new ItemCarrinho();

        itemCarrinho.setId(card.getId());
        itemCarrinho.setPrecoUnitario(card.getValor());

        return itemCarrinho;
    }
}
