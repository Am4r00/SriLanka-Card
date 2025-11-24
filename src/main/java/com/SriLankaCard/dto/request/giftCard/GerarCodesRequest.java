package com.SriLankaCard.dto.request.giftCard;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class GerarCodesRequest {
    @NotNull
    private Long cardId;
    @NotNull
    @Min(1)
    private Integer quantidade;
}
