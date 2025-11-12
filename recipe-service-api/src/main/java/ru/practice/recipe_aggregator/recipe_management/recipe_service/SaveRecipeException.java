package ru.practice.recipe_aggregator.recipe_management.recipe_service;

import org.springframework.http.HttpStatus;
import ru.practice.recipe_aggregator.exception.ParentException;

public class SaveRecipeException extends ParentException {
    public SaveRecipeException(String message) {
        super(message, HttpStatus.CONFLICT);
    }

    public SaveRecipeException(String message, Throwable cause) {
        super(message, cause, HttpStatus.CONFLICT);
    }
}
