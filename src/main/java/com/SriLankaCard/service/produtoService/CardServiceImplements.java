package com.SriLankaCard.service.produtoService;

import com.SriLankaCard.dto.request.cards.CardAdjustRequest;
import com.SriLankaCard.dto.request.cards.CardRequest;
import com.SriLankaCard.dto.response.produtoResponse.CardResponse;
import com.SriLankaCard.entity.produtoEntity.Card;
import com.SriLankaCard.mapper.CardMapper;
import com.SriLankaCard.repository.produtoRepository.CardRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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

        String raw = UUID.randomUUID().toString().replace("-", "").toUpperCase();  // 32 hex
        String serial = raw.replaceFirst("(.{5})(.{5})(.{5})(.{5})(.{2}).*", "$1-$2-$3-$4-$5");

        card.setSerial(passwordEncoder.encode(serial));
        card.setAvaliacao(0);

        cardRepository.save(card);

        return CardMapper.toCardResponseByCard(card);
    }

    @Override
    public CardResponse atualiarCard(Long id, CardAdjustRequest adjust) {
        if(id == null || id < 0 || adjust == null){
            throw new RuntimeException("ERRO PERSONALIZADO !!");
        }

        Card card = cardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("ERRO PERSONALIZADO !!!"));

        if(adjust.getNome() != null){
            card.setNome(adjust.getNome());
        }
        if(adjust.getObservacoes() != null){
            card.setObservacoes(adjust.getObservacoes());
        }
        if(adjust.getValor() != null){
            card.setValor(adjust.getValor());
        }

        cardRepository.save(card);

        return CardMapper.toCardResponseByCard(card);
    }

    @Override
    public List<CardResponse> listarCards() {
        List<Card> listaCards = cardRepository.findAll();

        return listaCards.stream().map(CardMapper::toCardResponseByCard)
                .collect(Collectors.toList());
    }

    @Override
    public void deletarCard(Long id) {

    }

    @Override
    public CardResponse atualizarPromocao(Boolean promo, Long id) {
        return null;
    }
}
