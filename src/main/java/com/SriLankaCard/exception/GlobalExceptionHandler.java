package com.SriLankaCard.exception;

import com.SriLankaCard.dto.response.exceptionHandler.ResponseError;
import com.SriLankaCard.exception.dominio.EmailNotFoundException;
import com.SriLankaCard.exception.dominio.UserNotFoundException;
import com.SriLankaCard.exception.negocio.InvalidCardException;
import com.SriLankaCard.exception.negocio.CardNotFoundException;
import com.SriLankaCard.exception.negocio.CarrinhoNotFoundException;
import com.SriLankaCard.exception.negocio.EmailAlreadyUsedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ResponseError> treatUserNotFound(UserNotFoundException exception) {
        return buildErrorResponse(exception, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(EmailAlreadyUsedException.class)
    public ResponseEntity<ResponseError> treatEmailAlreadyUsed(EmailAlreadyUsedException exception) {
        return buildErrorResponse(exception, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(EmailNotFoundException.class)
    public ResponseEntity<ResponseError> treatEmailNotFound(EmailNotFoundException exception) {
        return buildErrorResponse(exception, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(CarrinhoNotFoundException.class)
    public ResponseEntity<ResponseError> treatCartNotFound(CarrinhoNotFoundException exception){
        return buildErrorResponse(exception, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidCardException.class)
    public ResponseEntity<ResponseError> treatCardIsNull(InvalidCardException exception){
        return buildErrorResponse(exception, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CardNotFoundException.class)
    public ResponseEntity<ResponseError> treatCardNotFound(CardNotFoundException exception){
        return buildErrorResponse(exception, HttpStatus.NOT_FOUND);
    }

    private ResponseEntity<ResponseError> buildErrorResponse(RuntimeException exception, HttpStatus httpStatus) {
        ResponseError error = new ResponseError(
                httpStatus.value(),
                exception.getMessage(),
                LocalDateTime.now());

        return new ResponseEntity<>(error, httpStatus);
    }
}