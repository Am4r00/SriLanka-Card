package com.SriLankaCard.service.produtoService;

import com.SriLankaCard.dto.request.cards.CardAdjustRequest;
import com.SriLankaCard.dto.request.cards.CardRequest;
import com.SriLankaCard.dto.response.produtoResponse.CardResponse;
import com.SriLankaCard.entity.produtoEntity.Card;
import com.SriLankaCard.mapper.CardMapper;
import com.SriLankaCard.repository.produtoRepository.CardRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CardServiceImplements implements CardService{
    private CardRepository cardRepository;
    private PasswordEncoder passwordEncoder;

    public CardServiceImplements(CardRepository cardRepository, PasswordEncoder passwordEncoder) {
        this.cardRepository = cardRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
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
    @Transactional
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
    @Transactional
    public List<CardResponse> listarCards() {
        List<Card> listaCards = cardRepository.findAll();

        return listaCards.stream().map(CardMapper::toCardResponseByCard)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CardResponse buscarPorId(Long id) {
        if(id == null || id < 0){
            throw new RuntimeException("ID inválido");
        }
        Card card = cardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Card não encontrado"));
        return CardMapper.toCardResponseByCard(card);
    }

    @Override
    @Transactional
    public void deletarCard(Long id) {
        if(id == null || id < 0){
            throw  new RuntimeException("ERRO PESONALIZADO !!!!");
        }
        cardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("ERRO PERSONALIZADO !!"));
        cardRepository.deleteById(id);
    }

    @Override
    @Transactional
    public CardResponse atualizarPromocao(Boolean promo, Long id) {
        if(promo == null || id == null || id < 0){
            throw new RuntimeException("ERRO PERSONALIZADO !!!");
        }

        Card card = cardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("ERRO PERSONALIZADO !!!"));

        card.setPromocao(promo);
        cardRepository.save(card);

        return CardMapper.toCardResponseByCard(card);
    }
}
