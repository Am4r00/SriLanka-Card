package com.SriLankaCard.exception;

import com.SriLankaCard.dto.response.ResponseError;
import com.SriLankaCard.exception.dominio.EmailNotFoundException;
import com.SriLankaCard.exception.dominio.UserNotFoundException;
import com.SriLankaCard.exception.negocio.EmailAlreadyUsedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.NativeWebRequest;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {

    private final NativeWebRequest nativeWebRequest;

    public GlobalExceptionHandler(NativeWebRequest nativeWebRequest) {
        this.nativeWebRequest = nativeWebRequest;
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ResponseError> treatUserNotFound(UserNotFoundException exception) {
        return buildErrorResponse(exception, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(EmailAlreadyUsedException.class)
    public ResponseEntity<ResponseError> treatEmailAlreadyUsed(EmailAlreadyUsedException exception) {
        return buildErrorResponse(exception, HttpStatus.ALREADY_REPORTED);
    }

    @ExceptionHandler(EmailNotFoundException.class)
    public ResponseEntity<ResponseError> treatEmailNotFound(EmailNotFoundException exception) {
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