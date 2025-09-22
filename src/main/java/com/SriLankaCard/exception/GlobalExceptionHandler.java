package com.SriLankaCard.exception;

import com.helpdesk.supportapi.dto.users.response.ResponseError;
import com.helpdesk.supportapi.exception.business.EmailAlreadyUsedException;
import com.helpdesk.supportapi.exception.business.InvalidStatusException;
import com.helpdesk.supportapi.exception.domain.EmailNotFoundException;
import com.helpdesk.supportapi.exception.domain.UserNotFoundException;
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

    @ExceptionHandler(InvalidStatusException.class)
    public ResponseEntity<ResponseError> treatInvalidStatus(InvalidStatusException exception) {
        return buildErrorResponse(exception, HttpStatus.MULTI_STATUS);
    }

    private ResponseEntity<ResponseError> buildErrorResponse(RuntimeException exception, HttpStatus httpStatus) {
        ResponseError error = new ResponseError(
                httpStatus.value(),
                exception.getMessage(),
                LocalDateTime.now());

        return new ResponseEntity<>(error, httpStatus);
    }
}