package com.SriLankaCard.service.produtoService;

import com.SriLankaCard.dto.request.cards.CardAdjustRequest;
import com.SriLankaCard.dto.request.cards.CardRequest;
import com.SriLankaCard.dto.response.produtoResponse.CardResponse;
import com.SriLankaCard.entity.produtoEntity.Card;
import com.SriLankaCard.entity.produtoEntity.GiftCodeStatus;
import com.SriLankaCard.mapper.CardMapper;
import com.SriLankaCard.repository.produtoRepository.CardRepository;
import com.SriLankaCard.repository.produtoRepository.GiftCodeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CardServiceImplements implements CardService {

    private final CardRepository cardRepository;
    private final GiftCodeRepository giftCodeRepository;

    public CardServiceImplements(CardRepository cardRepository,
                                 GiftCodeRepository giftCodeRepository) {
        this.cardRepository = cardRepository;
        this.giftCodeRepository = giftCodeRepository;
    }

    @Override
    @Transactional
    public CardResponse criarCard(CardRequest request) {
        if (request == null) {
            throw new RuntimeException("ERRO PERSONALIZADO !!");
        }

        Card card = CardMapper.toCardByCardRequest(request);

        // avaliação inicial
        card.setAvaliacao(0);

        cardRepository.save(card);

        // card recém-criado ainda não tem GiftCodes => quantidade = 0
        return CardMapper.toCardResponseByCard(card, 0);
    }

    @Override
    @Transactional
    public CardResponse atualiarCard(Long id, CardAdjustRequest adjust) {
        if (id == null || id < 0 || adjust == null) {
            throw new RuntimeException("ERRO PERSONALIZADO !!");
        }

        Card card = cardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("ERRO PERSONALIZADO !!!"));

        if (adjust.getNome() != null) {
            card.setNome(adjust.getNome());
        }
        if (adjust.getObservacoes() != null) {
            card.setObservacoes(adjust.getObservacoes());
        }
        if (adjust.getValor() != null) {
            card.setValor(adjust.getValor());
        }

        cardRepository.save(card);

        // recalcula a quantidade disponível pelos GiftCodes
        Long count = giftCodeRepository.countByCardAndStatus(card, GiftCodeStatus.DISPONIVEL);
        int qtd = count == null ? 0 : count.intValue();

        return CardMapper.toCardResponseByCard(card, qtd);
    }

    @Override
    @Transactional
    public List<CardResponse> listarCards() {
        List<Card> listaCards = cardRepository.findAll();

        return listaCards.stream()
                .map(card -> {
                    Long count = giftCodeRepository
                            .countByCardAndStatus(card, GiftCodeStatus.DISPONIVEL);
                    int qtd = count == null ? 0 : count.intValue();
                    return CardMapper.toCardResponseByCard(card, qtd);
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CardResponse buscarPorId(Long id) {
        if (id == null || id < 0) {
            throw new RuntimeException("ID inválido");
        }

        Card card = cardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Card não encontrado"));

        Long count = giftCodeRepository
                .countByCardAndStatus(card, GiftCodeStatus.DISPONIVEL);
        int qtd = count == null ? 0 : count.intValue();

        return CardMapper.toCardResponseByCard(card, qtd);
    }

    @Override
    @Transactional
    public void deletarCard(Long id) {
        if (id == null || id < 0) {
            throw new RuntimeException("ERRO PESONALIZADO !!!!");
        }

        cardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("ERRO PERSONALIZADO !!"));

        cardRepository.deleteById(id);
    }

    @Override
    @Transactional
    public CardResponse atualizarPromocao(Boolean promo, Long id) {
        if (promo == null || id == null || id < 0) {
            throw new RuntimeException("ERRO PERSONALIZADO !!!");
        }

        Card card = cardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("ERRO PERSONALIZADO !!!"));

        card.setPromocao(promo);
        cardRepository.save(card);

        // recalcula a quantidade disponível pelos GiftCodes
        Long count = giftCodeRepository
                .countByCardAndStatus(card, GiftCodeStatus.DISPONIVEL);
        int qtd = count == null ? 0 : count.intValue();

        return CardMapper.toCardResponseByCard(card, qtd);
    }
}
