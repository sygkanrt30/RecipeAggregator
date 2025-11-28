package ru.practice.recipe_aggregator.translator;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@RequiredArgsConstructor
@Getter
@Accessors(fluent = true)
public enum LanguageCode {
    RUSSIAN("ru"),
    ENGLISH("en");

    private final String code;
}