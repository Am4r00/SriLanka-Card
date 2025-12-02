package com.SriLankaCard.service.produtoService;

import com.SriLankaCard.dto.request.cards.CardAdjustRequest;
import com.SriLankaCard.dto.request.cards.CardRequest;
import com.SriLankaCard.dto.response.produtoResponse.CardResponse;

import java.util.List;

public interface CardService {
    CardResponse criarCard(CardRequest request);
    CardResponse atualiarCard(Long id, CardAdjustRequest adjust);
    CardResponse buscarPorId(Long id);
    void deletarCard(Long id);
    CardResponse atualizarPromocao(Boolean promo, Long id);

}
