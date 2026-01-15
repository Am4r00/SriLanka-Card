package com.SriLankaCard.service.produtoService;

import com.SriLankaCard.dto.request.giftCard.GerarCodesRequest;
import com.SriLankaCard.entity.produtoEntity.Card;
import com.SriLankaCard.entity.produtoEntity.GiftCode;
import com.SriLankaCard.entity.produtoEntity.GiftCodeStatus;
import com.SriLankaCard.exception.dominio.CardNotFoundException;
import com.SriLankaCard.exception.negocio.InvalidArgumentsException;
import com.SriLankaCard.repository.produtoRepository.CardRepository;
import com.SriLankaCard.repository.produtoRepository.GiftCodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GiftCodeService {
    private final GiftCodeRepository giftCodeRepository;
    private final CardRepository cardRepository;

    @Transactional
    public List<GiftCode> gerarCodigos(GerarCodesRequest request){
        Card card = cardRepository.findById(request.getCardId())
                .orElseThrow(() -> new CardNotFoundException("Card não encontrado!"));

        if(request.getQuantidade() == null || request.getQuantidade() <= 0){
            throw new InvalidArgumentsException("Quantidade deve ser maior que zero");
        }

        List<GiftCode> codes = new ArrayList<>();

        for (int i = 0; i < request.getQuantidade(); i++) {
            String serial = gerarSerial();

            GiftCode code = new GiftCode();
            code.setCard(card);
            code.setSerial(serial);
            code.setStatus(GiftCodeStatus.DISPONIVEL);
            codes.add(code);
        }

        return giftCodeRepository.saveAll(codes);
    }

    private String gerarSerial() {
        String raw = UUID.randomUUID().toString().replace("-", "").toUpperCase();  // 32 hex
        String serial = raw.replaceFirst("(.{5})(.{5})(.{5})(.{5})(.{2}).*", "$1-$2-$3-$4-$5");
        return serial;
    }
}
