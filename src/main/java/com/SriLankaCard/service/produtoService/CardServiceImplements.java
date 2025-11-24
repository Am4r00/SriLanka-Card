package com.SriLankaCard.service.produtoService;

import com.SriLankaCard.dto.request.cards.CardAdjustRequest;
import com.SriLankaCard.dto.request.cards.CardRequest;
import com.SriLankaCard.dto.request.giftCard.GerarCodesRequest;
import com.SriLankaCard.dto.response.produtoResponse.CardResponse;
import com.SriLankaCard.entity.produtoEntity.Card;
import com.SriLankaCard.entity.produtoEntity.GiftCodeStatus;
import com.SriLankaCard.exception.negocio.InvalidCardException;
import com.SriLankaCard.exception.negocio.CardNotFoundException;
import com.SriLankaCard.mapper.CardMapper;
import com.SriLankaCard.repository.produtoRepository.CardRepository;
import com.SriLankaCard.repository.produtoRepository.GiftCodeRepository;
import com.SriLankaCard.service.produtoService.GiftCodeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CardServiceImplements implements CardService {

    private final CardRepository cardRepository;
    private final GiftCodeRepository giftCodeRepository;
    private final GiftCodeService giftCodeService;

    public CardServiceImplements(CardRepository cardRepository,
                                 GiftCodeRepository giftCodeRepository,
                                 GiftCodeService giftCodeService) {
        this.cardRepository = cardRepository;
        this.giftCodeRepository = giftCodeRepository;
        this.giftCodeService = giftCodeService;
    }

    @Override
    @Transactional
    public CardResponse criarCard(CardRequest request) {
        if (request == null) {
            throw new InvalidCardException("Card vazio!");
        }

        Card card = CardMapper.toCardByCardRequest(request);

        // avaliação inicial
        card.setAvaliacao(0);

        Card cardSalvo = cardRepository.save(card);

        // Se quantidade foi informada e é maior que zero, gerar os gift codes automaticamente
        int quantidadeGerada = 0;
        if (request.getQuantidade() != null && request.getQuantidade() > 0) {
            try {
                GerarCodesRequest gerarCodesRequest = new GerarCodesRequest();
                gerarCodesRequest.setCardId(cardSalvo.getId());
                gerarCodesRequest.setQuantidade(request.getQuantidade());
                
                giftCodeService.gerarCodigos(gerarCodesRequest);
                quantidadeGerada = request.getQuantidade();
                
                // Atualizar a quantidade do card após gerar os códigos
                cardSalvo.setQuantidade(quantidadeGerada);
                cardSalvo = cardRepository.save(cardSalvo);
            } catch (Exception e) {
                // Se houver erro ao gerar códigos, logar mas não falhar a criação do card
                System.err.println("Erro ao gerar gift codes: " + e.getMessage());
                e.printStackTrace();
                // Continua sem os gift codes, quantidade permanece 0
            }
        }

        return CardMapper.toCardResponseByCard(cardSalvo, quantidadeGerada);
    }

    @Override
    @Transactional
    public CardResponse atualiarCard(Long id, CardAdjustRequest adjust) {
        if (id == null || id < 0 || adjust == null) {
            throw new InvalidCardException("Parâmetro inválido para card. Verique se os parâmetros estão corretos");
        }

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
            throw new InvalidCardException("ID inválido");
        }

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
        if (id == null || id < 0) {
            throw new InvalidCardException("ID do card é inválido!");
        }

        cardRepository.findById(id)
                .orElseThrow(() -> new CardNotFoundException("Card não encontrado!"));

        cardRepository.deleteById(id);
    }

    @Override
    @Transactional
    public CardResponse atualizarPromocao(Boolean promo, Long id) {
        if (promo == null || id == null || id < 0) {
            throw new InvalidCardException("Parâmetro inválido!");
        }

        Card card = cardRepository.findById(id)
                .orElseThrow(() -> new CardNotFoundException("Card não encontrado!"));

        card.setPromocao(promo);
        cardRepository.save(card);

        // recalcula a quantidade disponível pelos GiftCodes
        Long count = giftCodeRepository
                .countByCardAndStatus(card, GiftCodeStatus.DISPONIVEL);
        int qtd = count == null ? 0 : count.intValue();

        return CardMapper.toCardResponseByCard(card, qtd);
    }
}
