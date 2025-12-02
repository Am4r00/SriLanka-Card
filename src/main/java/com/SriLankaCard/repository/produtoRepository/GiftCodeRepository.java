package com.SriLankaCard.repository.produtoRepository;

import com.SriLankaCard.entity.produtoEntity.Card;
import com.SriLankaCard.entity.produtoEntity.GiftCode;
import com.SriLankaCard.entity.produtoEntity.GiftCodeStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GiftCodeRepository extends JpaRepository<GiftCode, Long> {
    List<GiftCode> findByCardAndStatus(Card card, GiftCodeStatus status);
    Long countByCardAndStatus(Card card, GiftCodeStatus status);
    Boolean existsGiftCodeByCard(Card card);
}
