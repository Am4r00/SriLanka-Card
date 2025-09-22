package com.SriLankaCard.exception.negocio;

public class EmailAlreadyUsedException extends RuntimeException {
    public EmailAlreadyUsedException(String message) {
        super(message);
    }

    public EmailAlreadyUsedException(){
        super("Email Já usado !");
    }
}
