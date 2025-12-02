package com.SriLankaCard.controller.produtoController;

import com.SriLankaCard.dto.request.cards.CardAdjustRequest;
import com.SriLankaCard.dto.request.cards.CardRequest;
import com.SriLankaCard.dto.response.produtoResponse.CardResponse;
import com.SriLankaCard.service.produtoService.CardServiceImplements;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cards")
public class CardController {
    @Autowired
    private CardServiceImplements cardServiceImplements;

    public CardController(CardServiceImplements cardServiceImplements) {
        this.cardServiceImplements = cardServiceImplements;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("criar-Card")
    public CardResponse criarCard(@Valid @RequestBody CardRequest request){
        return cardServiceImplements.criarCard(request);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("atualizar/{id}")
    public CardResponse atualizarCard(@PathVariable Long id, @Valid @RequestBody CardAdjustRequest adjust){
        return cardServiceImplements.atualiarCard(id, adjust);
    }

    @GetMapping("/{id}")
    public CardResponse buscarPorId(@PathVariable Long id){
        return cardServiceImplements.buscarPorId(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/deletar/{id}")
    public void deletarCard(@PathVariable Long id){
        cardServiceImplements.deletarCard(id);
    }

    @PatchMapping("/cards/{id}/promocao/{promo}")
    public CardResponse atualizarPromocao(@PathVariable("id") Long id,
                                          @PathVariable("promo") boolean promo) {
        return cardServiceImplements.atualizarPromocao(promo, id);
    }
}



