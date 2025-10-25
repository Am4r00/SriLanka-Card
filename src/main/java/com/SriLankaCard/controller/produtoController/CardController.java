package com.SriLankaCard.controller.produtoController;

import com.SriLankaCard.dto.request.cards.CardRequest;
import com.SriLankaCard.dto.response.produtoResponse.CardResponse;
import com.SriLankaCard.service.produtoService.CardService;
import com.SriLankaCard.service.produtoService.CardServiceImplements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cards")
public class CardController {
    @Autowired
    private CardServiceImplements cardServiceImplements;

    public CardController(CardServiceImplements cardServiceImplements) {
        this.cardServiceImplements = cardServiceImplements;
    }

    @PostMapping("criar-Card")
    public CardResponse criarCard(@RequestBody CardRequest request){
        return cardServiceImplements.criarCard(request);
    }


}
