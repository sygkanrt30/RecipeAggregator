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

    public static LanguageCode fromCode(String code) {
        for (var language : values()) {
            if (language.code.equalsIgnoreCase(code)) {
                return language;
            }
        }
        throw new IllegalArgumentException("Unknown language code: " + code);
    }
}