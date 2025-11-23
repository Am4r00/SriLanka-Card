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
        card.setValor(request.getValor());
        card.setPromocao(request.isPromocao());

        return card;
    }

    public static CardResponse toCardResponseByCard(Card card,int quantidadeDisponivel){
        CardResponse response = new CardResponse();
        response.setId(card.getId());
        response.setNome(card.getNome());
        response.setObservacoes(card.getObservacoes());
        response.setQuantidade(quantidadeDisponivel);
        response.setValor(card.getValor());
        response.setPromocao(card.isPromocao());
        return response;
    }
}
