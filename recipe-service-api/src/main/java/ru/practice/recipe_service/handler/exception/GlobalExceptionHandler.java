package ru.practice.recipe_service.handler.exception;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.practice.recipe_service.model.dto.factory.ResponseDtoFactory;
import ru.practice.recipe_service.model.dto.response.ResponseDto;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler
    public ResponseDto catchEntityExistsException(EntityExistsException e) {
        return getAppErrorHandlerResponseEntity(e, HttpStatus.CONFLICT);
    }

    @ExceptionHandler
    public ResponseDto catchEntityNotFoundException(EntityNotFoundException e) {
        return getAppErrorHandlerResponseEntity(e, HttpStatus.NO_CONTENT);
    }

    private ResponseDto getAppErrorHandlerResponseEntity(Exception e, HttpStatus status) {
        String error = e.getMessage();
        log.error(error, e);
        return ResponseDtoFactory.getResponseError(status, error);
    }
}
