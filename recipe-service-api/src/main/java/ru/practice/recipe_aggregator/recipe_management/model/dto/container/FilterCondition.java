package ru.practice.recipe_aggregator.recipe_management.model.dto.container;

public record FilterCondition(
        String fieldName,
        FilterOperator operator,
        int value) {
}
