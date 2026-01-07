package com.SriLankaCard.service.produtoService;

import com.SriLankaCard.dto.request.cards.CardAdjustRequest;
import com.SriLankaCard.dto.request.cards.CardRequest;
import com.SriLankaCard.dto.response.produtoResponse.CardResponse;
import com.SriLankaCard.entity.produtoEntity.Card;
import com.SriLankaCard.entity.produtoEntity.GiftCodeStatus;
import com.SriLankaCard.exception.negocio.InvalidCardException;
import com.SriLankaCard.exception.negocio.CardNotFoundException;
import com.SriLankaCard.mapper.CardMapper;
import com.SriLankaCard.repository.produtoRepository.CardRepository;
import com.SriLankaCard.repository.produtoRepository.GiftCodeRepository;
import com.SriLankaCard.utils.ValidationUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        ValidationUtils.validateNotNull(request,"Card está vazio");

        Card card = CardMapper.toCardByCardRequest(request);
        card.setAvaliacao(0);

        Card cardSalvo = cardRepository.save(card);
        Long count = giftCodeRepository.countByCardAndStatus(cardSalvo, GiftCodeStatus.DISPONIVEL);
        int qtdDisponivel = count == null ? 0 : count.intValue();
        return CardMapper.toCardResponseByCard(cardSalvo, qtdDisponivel);
    }

    @Override
    @Transactional
    public CardResponse atualiarCard(Long id, CardAdjustRequest adjust) {
        ValidationUtils.validateNotNullAndPositive(id,adjust,"Parâmetro inválido para card. Verique se os parâmetros estão corretos !");

        Card card = cardRepository.findById(id)
                .orElseThrow(() -> new CardNotFoundException("Card não encontrado!"));

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

        Long count = giftCodeRepository.countByCardAndStatus(card, GiftCodeStatus.DISPONIVEL);
        int qtd = count == null ? 0 : count.intValue();

        return CardMapper.toCardResponseByCard(card, qtd);
    }

    @Override
    @Transactional
    public CardResponse buscarPorId(Long id) {
        ValidationUtils.validateNumbers(id);

        Card card = cardRepository.findById(id)
                .orElseThrow(() -> new CardNotFoundException("Card não encontrado!"));

        Long count = giftCodeRepository
                .countByCardAndStatus(card, GiftCodeStatus.DISPONIVEL);
        int qtd = count == null ? 0 : count.intValue();

        return CardMapper.toCardResponseByCard(card, qtd);
    }

    @Override
    @Transactional
    public void deletarCard(Long id) {
        ValidationUtils.validateNumbers(id);

        Card card = cardRepository.findById(id)
                .orElseThrow(() -> new CardNotFoundException("Card não encontrado!"));
        if(giftCodeRepository.existsGiftCodeByCard(card)){
            throw new InvalidCardException("Não foi possivel excluir pois existem códigos ativos deste card ");
        }
        cardRepository.deleteById(id);
    }

    @Override
    @Transactional
    public CardResponse atualizarPromocao(Boolean promo, Long id) {
        ValidationUtils.validateNumbers(id);
        if (promo == null)
            throw new InvalidCardException("Promoção está em branco! ");

        Card card = cardRepository.findById(id)
                .orElseThrow(() -> new CardNotFoundException("Card não encontrado!"));

        card.setPromocao(promo);
        cardRepository.save(card);

        Long count = giftCodeRepository
                .countByCardAndStatus(card, GiftCodeStatus.DISPONIVEL);
        int qtd = count == null ? 0 : count.intValue();

        return CardMapper.toCardResponseByCard(card, qtd);
    }
}
