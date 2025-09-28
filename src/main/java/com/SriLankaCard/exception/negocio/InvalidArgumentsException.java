package com.SriLankaCard.exception.negocio;

public class InvalidArgumentsException extends RuntimeException {
    public InvalidArgumentsException(String message) {
        super(message);
    }

    public InvalidArgumentsException(){
        super("Argumentos Passados inválidos");
    }
}
