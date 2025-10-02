package ru.practice.recipe_aggregator.user_service.service;

public class RecipeNotContainsException extends RuntimeException {
    public RecipeNotContainsException(String message) {
        super(message);
    }
}
