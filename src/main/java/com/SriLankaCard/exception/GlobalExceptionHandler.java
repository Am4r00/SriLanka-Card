package com.SriLankaCard.exception;

import com.SriLankaCard.dto.response.exceptionHandler.ResponseError;
import com.SriLankaCard.exception.dominio.EmailNotFoundException;
import com.SriLankaCard.exception.dominio.UserNotFoundException;
import com.SriLankaCard.exception.negocio.InvalidCardException;
import com.SriLankaCard.exception.dominio.CardNotFoundException;
import com.SriLankaCard.exception.dominio.CarrinhoNotFoundException;
import com.SriLankaCard.exception.negocio.EmailAlreadyUsedException;
import com.SriLankaCard.exception.negocio.InvalidArgumentsException;
import com.SriLankaCard.exception.negocio.UserInativoException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.time.Instant;


@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ResponseError> treatUserNotFound(UserNotFoundException exception,HttpServletRequest request) {
        return buildErrorResponse(exception, HttpStatus.NOT_FOUND,request);
    }

    @ExceptionHandler(EmailAlreadyUsedException.class)
    public ResponseEntity<ResponseError> treatEmailAlreadyUsed(EmailAlreadyUsedException exception,HttpServletRequest request) {
        return buildErrorResponse(exception, HttpStatus.CONFLICT,request);
    }

    @ExceptionHandler(EmailNotFoundException.class)
    public ResponseEntity<ResponseError> treatEmailNotFound(EmailNotFoundException exception,HttpServletRequest request) {
        return buildErrorResponse(exception, HttpStatus.NOT_FOUND,request);
    }


    @ExceptionHandler(InvalidArgumentsException.class)
    public ResponseEntity<ResponseError> treatInvalidArguments(InvalidArgumentsException exception,HttpServletRequest request) {
        return buildErrorResponse(exception, HttpStatus.BAD_REQUEST,request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseError> treatMethodArgumentNotValid(MethodArgumentNotValidException exception,HttpServletRequest request) {
            String message = exception.getBindingResult().getFieldErrors().stream()
                    .map(error -> error.getField() + ": " + error.getDefaultMessage())
                    .reduce((msg1, msg2) -> msg1 + ", " + msg2)
                    .orElse("Dados inválidos");

            return  buildErrorResponse(message,HttpStatus.BAD_REQUEST,request);
    }

    @ExceptionHandler(CarrinhoNotFoundException.class)
    public ResponseEntity<ResponseError> treatCartNotFound(CarrinhoNotFoundException exception,HttpServletRequest request) {
        return buildErrorResponse(exception, HttpStatus.NOT_FOUND,request);
    }

    @ExceptionHandler(InvalidCardException.class)
    public ResponseEntity<ResponseError> treatCardIsNull(InvalidCardException exception,HttpServletRequest request) {
        return buildErrorResponse(exception, HttpStatus.BAD_REQUEST,request);
    }

    @ExceptionHandler(CardNotFoundException.class)
    public ResponseEntity<ResponseError> treatCardNotFound(CardNotFoundException exception,HttpServletRequest request) {
        return buildErrorResponse(exception, HttpStatus.NOT_FOUND,request);
    }

    @ExceptionHandler(UserInativoException.class)
    public ResponseEntity<ResponseError> treatUserInactive(UserInativoException ex, HttpServletRequest req) {
        ResponseError err = new ResponseError(
                HttpStatus.FORBIDDEN.value(),
                "USER_INACTIVE",
                Instant.now(),
                req.getRequestURI()
        );
        return new ResponseEntity<>(err, HttpStatus.FORBIDDEN);
    }


    private ResponseEntity<ResponseError> buildErrorResponse(Exception exception, HttpStatus httpStatus,HttpServletRequest request) {
        ResponseError error = new ResponseError(
                httpStatus.value(),
                exception.getMessage(),
                Instant.now(),
                request.getRequestURI());

        return new ResponseEntity<>(error, httpStatus);
    }

    private ResponseEntity<ResponseError> buildErrorResponse(String message,HttpStatus httpStatus,HttpServletRequest request) {
        ResponseError error = new ResponseError(
                httpStatus.value(),
                message,
                Instant.now(),
                request.getRequestURI());

        return new ResponseEntity<>(error, httpStatus);
    }

}