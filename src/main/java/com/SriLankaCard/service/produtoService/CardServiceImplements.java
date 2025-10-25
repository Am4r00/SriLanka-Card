package com.SriLankaCard.service.produtoService;

import com.SriLankaCard.dto.request.cards.CardAdjustRequest;
import com.SriLankaCard.dto.request.cards.CardRequest;
import com.SriLankaCard.dto.response.produtoResponse.CardResponse;
import com.SriLankaCard.entity.produtoEntity.Card;
import com.SriLankaCard.mapper.CardMapper;
import com.SriLankaCard.repository.produtoRepository.CardRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

public class CardServiceImplements implements CardService{
    private CardRepository cardRepository;
    private PasswordEncoder passwordEncoder;

    public CardServiceImplements(CardRepository cardRepository, PasswordEncoder passwordEncoder) {
        this.cardRepository = cardRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public CardResponse criarCard(CardRequest request) {
        if(request == null){
            throw new RuntimeException("ERRO PERSONALIZADO !!");
        }
        Card card = CardMapper.toCardByCardRequest(request);
        cardRepository.save(card);

        return CardMapper.toCardResponseByCard(card);
    }

    @Override
    public CardResponse atualiarCard(Long id, CardAdjustRequest adjust) {
        return null;
    }

    @Override
    public List<CardResponse> listarCards() {
        return List.of();
    }

    @Override
    public void deletarCard(Long id) {

    }

    @Override
    public CardResponse atualizarPromocao(Boolean promo, Long id) {
        return null;
    }
}
