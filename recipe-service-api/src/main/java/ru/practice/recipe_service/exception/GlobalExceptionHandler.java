package ru.practice.recipe_service.exception;

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
        return getAppErrorHandlerResponseDto(e, HttpStatus.CONFLICT);
    }

    @ExceptionHandler
    public ResponseDto catchEntityNotFoundException(EntityNotFoundException e) {
        return getAppErrorHandlerResponseDto(e, HttpStatus.NO_CONTENT);
    }

    private ResponseDto getAppErrorHandlerResponseDto(Exception e, HttpStatus status) {
        String error = e.getMessage();
        log.error(error, e);
        return ResponseDtoFactory.getResponseError(status, error);
    }
}
