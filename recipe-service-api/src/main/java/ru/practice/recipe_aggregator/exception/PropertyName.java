package ru.practice.recipe_aggregator.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@RequiredArgsConstructor
@Getter
@Accessors(fluent = true)
enum PropertyName {
    ERROR_CODE("errorCode"),
    TIMESTAMP("timestamp");

    private final String value;
}
