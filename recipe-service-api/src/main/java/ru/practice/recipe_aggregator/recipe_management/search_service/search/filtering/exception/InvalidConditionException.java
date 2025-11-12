package ru.practice.recipe_aggregator.recipe_management.search_service.search.filtering.exception;

import org.springframework.http.HttpStatus;
import ru.practice.recipe_aggregator.exception.ParentException;

public class InvalidConditionException extends ParentException {

    public InvalidConditionException(String message) {
        super(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
