package com.SriLankaCard.controller.produtoController;

import com.SriLankaCard.dto.response.produtoResponse.CardResponse;
import com.SriLankaCard.entity.produtoEntity.CardCategory;
import com.SriLankaCard.service.produtoService.CardSearchServiceImple;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cards")
public class CardSearchController {
    private final CardSearchServiceImple cardSearchService;

    public CardSearchController(CardSearchServiceImple cardSearchService) {
        this.cardSearchService = cardSearchService;
    }

    @GetMapping
    public ResponseEntity<List<CardResponse>> listarTodos() {
        List<CardResponse> cards = cardSearchService.listarTodos();
        if (cards.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(cards);
    }

    @GetMapping("/categoria/{categoria}")
    public ResponseEntity<List<CardResponse>> listarPorCategoria(
            @PathVariable("categoria") CardCategory categoria
    ) {
        List<CardResponse> cards = cardSearchService.listarPorCategoria(categoria);
        if (cards.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(cards);
    }

    @GetMapping("/promocao")
    public ResponseEntity<List<CardResponse>> listarPorPromocao(
            @RequestParam("ativa") Boolean promocao
    ) {
        List<CardResponse> cards = cardSearchService.listarComPromocao(promocao);
        if (cards.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(cards);
    }
}
