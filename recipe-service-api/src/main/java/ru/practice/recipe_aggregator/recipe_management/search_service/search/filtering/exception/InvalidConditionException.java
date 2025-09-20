package ru.practice.recipe_aggregator.recipe_management.search_service.search.filtering.exception;

public class InvalidConditionException extends RuntimeException {
    public InvalidConditionException(String message) {
        super(message);
    }
}
