package com.SriLankaCard.service.produtoService;

import com.SriLankaCard.dto.response.produtoResponse.CardResponse;
import com.SriLankaCard.entity.produtoEntity.CardCategory;
import org.springframework.stereotype.Service;

import java.util.List;


public interface CardSearchService {
    List<CardResponse> listarPorCategoria(CardCategory category);
    List<CardResponse> listarTodos();
    List<CardResponse> listarComPromocao(boolean promocao);

}
