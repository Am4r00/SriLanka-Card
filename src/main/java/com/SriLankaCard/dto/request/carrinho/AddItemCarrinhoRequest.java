package com.SriLankaCard.dto.request.carrinho;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddItemCarrinhoRequest {

    @NotNull
    private Long id;

    @NotNull
    @Min(1)
    private Integer quantidade;
}
