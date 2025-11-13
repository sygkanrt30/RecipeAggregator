package ru.practice.recipe_aggregator.exception;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<String> catchEntityExistsException(EntityExistsException e) {
        return getAppErrorHandlerResponseDto(e, HttpStatus.CONFLICT);
    }

    @ExceptionHandler
    public ResponseEntity<String> catchEntityNotFoundException(EntityNotFoundException e) {
        return getAppErrorHandlerResponseDto(e, HttpStatus.NO_CONTENT);
    }

    @ExceptionHandler
    public ResponseEntity<String> catchUsernameNotFoundException(UsernameNotFoundException e) {
        return getAppErrorHandlerResponseDto(e, HttpStatus.NO_CONTENT);
    }

    @ExceptionHandler
    public ResponseEntity<String> catchIllegalArgumentException(IllegalArgumentException e) {
        return getAppErrorHandlerResponseDto(e, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler
    public ResponseEntity<String> catchCustomException(ParentException e) {
        return getAppErrorHandlerResponseDto(e, e.responseStatus());
    }

    private ResponseEntity<String> getAppErrorHandlerResponseDto(Exception e, HttpStatus status) {
        String error = e.getMessage();
        log.error(error, e.getCause());
        return ResponseEntity.status(status).body(error);
    }
}
