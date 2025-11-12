package ru.practice.recipe_aggregator.exception;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.practice.recipe_aggregator.recipe_management.model.dto.response.ResponseDto;

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

    @ExceptionHandler
    public ResponseDto catchUsernameNotFoundException(UsernameNotFoundException e) {
        return getAppErrorHandlerResponseDto(e, HttpStatus.NO_CONTENT);
    }

    @ExceptionHandler
    public ResponseDto catchIllegalArgumentException(IllegalArgumentException e) {
        return getAppErrorHandlerResponseDto(e, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler
    public ResponseDto catchCustomException(ParentException e) {
        return getAppErrorHandlerResponseDto(e, e.responseStatus());
    }

    private ResponseDto getAppErrorHandlerResponseDto(Exception e, HttpStatus status) {
        String error = e.getMessage();
        log.error(error, e.getCause());
        return ResponseDto.getResponseError(status, error);
    }
}
