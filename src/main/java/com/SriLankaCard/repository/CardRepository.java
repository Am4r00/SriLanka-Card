package com.SriLankaCard.repository;

import com.SriLankaCard.entity.produtoEntity.Card;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CardRepository extends JpaRepository<Card, Long> { }
