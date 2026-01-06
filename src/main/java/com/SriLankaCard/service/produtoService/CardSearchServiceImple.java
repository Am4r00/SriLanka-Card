package com.SriLankaCard.service.produtoService;

import com.SriLankaCard.dto.response.produtoResponse.CardResponse;
import com.SriLankaCard.entity.produtoEntity.Card;
import com.SriLankaCard.entity.produtoEntity.CardCategory;
import com.SriLankaCard.entity.produtoEntity.GiftCodeStatus;
import com.SriLankaCard.mapper.CardMapper;
import com.SriLankaCard.repository.produtoRepository.CardRepository;
import com.SriLankaCard.repository.produtoRepository.GiftCodeRepository;
import com.SriLankaCard.utils.ValidationUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CardSearchServiceImple implements CardSearchService{
    private CardRepository cardRepository;
    private GiftCodeRepository giftCodeRepository;

    public CardSearchServiceImple(CardRepository cardRepository, GiftCodeRepository giftCodeRepository) {
        this.cardRepository = cardRepository;
        this.giftCodeRepository = giftCodeRepository;
    }

    @Override
    public List<CardResponse> listarPorCategoria(CardCategory category) {
        List<Card> listaCards = cardRepository.findByCategory(category);

        return listaCards.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<CardResponse> listarTodos() {
        List<Card> listaCards = cardRepository.findAll();

        return listaCards.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<CardResponse> listarComPromocao(boolean promocao) {
        List<Card> listaCards = cardRepository.findByPromocao(promocao);

        return listaCards.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    private CardResponse mapToResponse(Card card) {
        Long count = giftCodeRepository.countByCardAndStatus(card, GiftCodeStatus.DISPONIVEL);
        int qtd = count == null ? 0 : count.intValue();
        return CardMapper.toCardResponseByCard(card, qtd);
    }

}
