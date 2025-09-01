package ru.practice.search_service.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.practice.search_service.model.dto.factory.ResponseDtoFactory;
import ru.practice.search_service.model.dto.response.ResponseDto;
import ru.practice.search_service.service.filtering.exception.InvalidConditionException;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler
    public ResponseDto catchInvalidConditionException(InvalidConditionException e) {
        return getAppErrorHandlerResponseDto(e, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler
    public ResponseDto catchIllegalArgumentException(IllegalArgumentException e) {
        return getAppErrorHandlerResponseDto(e, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseDto getAppErrorHandlerResponseDto(Exception e, HttpStatus status) {
        String error = e.getMessage();
        log.error(error, e);
        return ResponseDtoFactory.getResponseError(status, error);
    }
}