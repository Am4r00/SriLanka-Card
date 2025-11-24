package com.SriLankaCard.exception.negocio;

public class InvalidCardException extends RuntimeException{

    public InvalidCardException(String message){
        super(message);
    }

    public InvalidCardException(){
        super("Card inválido");
    }

}
