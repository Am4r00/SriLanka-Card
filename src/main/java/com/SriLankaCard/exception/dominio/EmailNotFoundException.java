package com.SriLankaCard.exception.dominio;

public class EmailNotFoundException extends RuntimeException{
    public EmailNotFoundException(String message) {
        super(message);
    }

    public EmailNotFoundException(){
        super("Email Não encontrado ou inválido");
    }
}
