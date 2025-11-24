package com.SriLankaCard.mapper;

import com.SriLankaCard.dto.request.cards.CardRequest;
import com.SriLankaCard.dto.response.produtoResponse.CardResponse;
import com.SriLankaCard.entity.produtoEntity.Card;

import java.util.UUID;

public class CardMapper {

    public static Card toCardByCardRequest(CardRequest request){
        Card card = new Card();
        card.setNome(request.getNome());
        card.setObservacoes(request.getObservacoes());
        card.setValor(request.getValor());
        card.setPromocao(request.isPromocao());
        // Quantidade inicial será 0, será atualizada após gerar os gift codes
        card.setQuantidade(request.getQuantidade() != null ? request.getQuantidade() : 0);
        // Avaliação inicial padrão é 0
        card.setAvaliacao(0);
        // Gerar serial único para o card
        card.setSerial(gerarSerial());

        return card;
    }

    private static String gerarSerial() {
        String raw = UUID.randomUUID().toString().replace("-", "").toUpperCase();
        String serial = raw.replaceFirst("(.{5})(.{5})(.{5})(.{5})(.{2}).*", "$1-$2-$3-$4-$5");
        return serial;
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
