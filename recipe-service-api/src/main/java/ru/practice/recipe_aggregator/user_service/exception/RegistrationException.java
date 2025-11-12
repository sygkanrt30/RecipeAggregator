package ru.practice.recipe_aggregator.user_service.exception;

import org.springframework.http.HttpStatus;
import ru.practice.recipe_aggregator.exception.ParentException;

public class RegistrationException extends ParentException {
    public RegistrationException(String message, Throwable cause) {
        super(message, cause, HttpStatus.CONFLICT);
    }
}
