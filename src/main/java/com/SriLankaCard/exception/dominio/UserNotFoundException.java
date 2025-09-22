package com.SriLankaCard.exception.dominio;

public class UserNotFoundException  extends RuntimeException{
    public UserNotFoundException(String message) {
        super(message);
    }

    public UserNotFoundException(){
        super("Usuário Não foi encontrado ! ");
    }};
