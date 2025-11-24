package com.SriLankaCard.exception.negocio;

public class CardNotFoundException extends RuntimeException{

    public CardNotFoundException(String message){
        super(message);
    }

    public CardNotFoundException(){
        super("Card não encontrado!");
    }
}
