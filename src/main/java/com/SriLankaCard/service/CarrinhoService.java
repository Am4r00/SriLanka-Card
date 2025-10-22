package com.SriLankaCard.service;

import com.SriLankaCard.entity.produtoEntity.Card;
import com.SriLankaCard.entity.produtoEntity.carrinho.Carrinho;
import com.SriLankaCard.entity.produtoEntity.carrinho.ItemCarrinho;
import com.SriLankaCard.entity.userEntity.User;
import com.SriLankaCard.repository.CardRepository;
import com.SriLankaCard.repository.carrinho.CarrinhoRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class CarrinhoService {
    private final CarrinhoRepository carrinhoRepository;
    private final CardRepository cardRepository;

    public CarrinhoService(CarrinhoRepository carrinhoRepository, CardRepository cardRepository) {
        this.carrinhoRepository = carrinhoRepository;
        this.cardRepository = cardRepository;
    }

    @Transactional
    public Carrinho getOuCriaCarrinho(User user) {
        return carrinhoRepository.findByUsuarioCarrinho(user)
                .orElseGet(() -> {
                    Carrinho c = new Carrinho();
                    c.setUsuarioCarrinho(user);
                    return carrinhoRepository.save(c);
                });
    }

    @Transactional
    public Carrinho adicionarItem(User user, Long cardId, int quantidade) {
        if (quantidade <= 0) throw new IllegalArgumentException("Quantidade deve ser >= 1");
        Carrinho carrinho = getOuCriaCarrinho(user);
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new EntityNotFoundException("Card não encontrado"));

        // regra simples de estoque (opcional)
        if (card.getQuantidade() < quantidade) {
            throw new IllegalArgumentException("Estoque insuficiente");
        }

        carrinho.adicionarItem(card, quantidade);
        return carrinhoRepository.save(carrinho);
    }

    @Transactional
    public Carrinho atualizarQuantidade(User user, Long cardId, int novaQtd) {
        if (novaQtd <= 0) return removerItem(user, cardId);
        Carrinho carrinho = getOuCriaCarrinho(user);

        ItemCarrinho item = carrinho.getProdutosNoCarrinho().stream()
                .filter(i -> i.getCard().getId().equals(cardId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Item não está no carrinho"));

        item.setQuantidade(novaQtd);
        return carrinhoRepository.save(carrinho);
    }

    @Transactional
    public Carrinho removerItem(User user, Long cardId) {
        Carrinho carrinho = getOuCriaCarrinho(user);
        carrinho.removerItemPorCardId(cardId);
        return carrinhoRepository.save(carrinho);
    }

    @Transactional
    public Carrinho limpar(User user) {
        Carrinho carrinho = getOuCriaCarrinho(user);
        carrinho.limpar();
        return carrinhoRepository.save(carrinho);
    }

    @Transactional()
    public Carrinho ver(User user) {
        return getOuCriaCarrinho(user);
    }

}
