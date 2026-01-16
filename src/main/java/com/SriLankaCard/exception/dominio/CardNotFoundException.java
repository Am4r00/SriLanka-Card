package com.SriLankaCard.exception.dominio;

public class CardNotFoundException extends RuntimeException{

    public CardNotFoundException(String message){
        super(message);
    }

    public CardNotFoundException(){
        super("Card não encontrado!");
    }
}
