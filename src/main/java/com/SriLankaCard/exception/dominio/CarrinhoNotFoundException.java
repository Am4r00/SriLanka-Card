package com.SriLankaCard.exception.dominio;

public class CarrinhoNotFoundException extends RuntimeException{

    public CarrinhoNotFoundException(String message){
        super(message);
    }

    public CarrinhoNotFoundException(){
        super("Carrinho não encontrado");
    }

}
