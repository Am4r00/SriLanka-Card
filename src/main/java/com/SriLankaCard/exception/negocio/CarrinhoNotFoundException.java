package com.SriLankaCard.exception.negocio;

public class CarrinhoNotFoundException extends RuntimeException{

    public CarrinhoNotFoundException(String message){
        super(message);
    }

    public CarrinhoNotFoundException(){
        super("Carrinho não encontrado");
    }

}
