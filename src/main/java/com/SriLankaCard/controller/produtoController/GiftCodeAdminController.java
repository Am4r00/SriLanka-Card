package com.SriLankaCard.controller.produtoController;

import com.SriLankaCard.dto.request.giftCard.GerarCodesRequest;
import com.SriLankaCard.entity.produtoEntity.GiftCode;
import com.SriLankaCard.service.produtoService.GiftCodeService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin/gift-codes")
public class GiftCodeAdminController {

    private final GiftCodeService giftCodeService;

    public GiftCodeAdminController(GiftCodeService giftCodeService) {
        this.giftCodeService = giftCodeService;
    }

    @PostMapping("/gerar")
    public ResponseEntity<List<String>> gerarCodigos(@Valid @RequestBody GerarCodesRequest request){
        List<GiftCode> codes = giftCodeService.gerarCodigos(request);

        List<String> seriais = codes.stream()
                .map(GiftCode::getSerial)
                .toList();

        return ResponseEntity.ok(seriais);
    }
/**
 * BODY DA REQUISIÇÃO:
 * {
 *  "cardId": INTEGER,
 *  "quantidade": INTEGER
 * }
 *
 * **/

}
