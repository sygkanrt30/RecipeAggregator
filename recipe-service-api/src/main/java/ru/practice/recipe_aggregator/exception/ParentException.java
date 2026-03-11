package ru.practice.recipe_aggregator.exception;

import lombok.Getter;
import lombok.experimental.Accessors;
import org.springframework.http.HttpStatus;

@Getter
@Accessors(fluent = true)
public class ParentException extends RuntimeException {

    private final HttpStatus responseStatus;

    protected ParentException(String message, Throwable cause, HttpStatus responseStatus) {
        super(message, cause);
        this.responseStatus = responseStatus;
    }

    protected ParentException(String message, HttpStatus responseStatus) {
        super(message);
        this.responseStatus = responseStatus;
    }
}
