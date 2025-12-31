package com.SriLankaCard.dto.request.carrinho;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddItemCarrinhoRequest {

    @NotNull(message = "O id é um campo obrigatório !")
    private Long id;

    @NotNull(message = "Quantidade deve ser maior ou igual a 1")
    @Min(1)
    private Integer quantidade;
}
