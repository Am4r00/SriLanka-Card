package com.SriLankaCard.entity.pedidoEntity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name="pedido")
@NoArgsConstructor
@AllArgsConstructor
public class Pedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // segue o mesmo padrão do Carrinho (usa usuarioId, não o User direto)
    @Column(nullable = false)
    private Long usuarioId;

    @Column(nullable = false)
    private Double valorTotal;

    @Column(nullable = false)
    private Integer quantidadeTotal;

    @Column(nullable = false)
    private LocalDateTime criadoEm = LocalDateTime.now();

    @OneToMany(mappedBy = "Pedido",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<ItemPedido> itens = new ArrayList<>();
}
