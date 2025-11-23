package com.SriLankaCard.entity.produtoEntity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "gift_code")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GiftCode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String serial;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GiftCodeStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "card_id", nullable = false)
    private Card card;
}
