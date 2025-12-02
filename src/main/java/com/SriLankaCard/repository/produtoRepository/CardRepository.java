package com.SriLankaCard.repository.produtoRepository;

import com.SriLankaCard.entity.produtoEntity.Card;
import com.SriLankaCard.entity.produtoEntity.CardCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CardRepository extends JpaRepository<Card, Long> {
    List<Card> findByPromocao(boolean promocao);
    List<Card> findByCategory(CardCategory category);
}
