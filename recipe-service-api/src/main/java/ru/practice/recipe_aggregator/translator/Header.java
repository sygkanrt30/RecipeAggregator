package ru.practice.recipe_aggregator.translator;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@RequiredArgsConstructor
@Getter
@Accessors(fluent = true)
enum Header {
    CONTENT_TYPE("Content-Type"),
    AUTHORIZATION("Authorization");

    private final String value;
}
