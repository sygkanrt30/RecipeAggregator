package ru.practice.shared.dto.ingredient;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@RequiredArgsConstructor
@Getter
@Accessors(fluent = true)
enum DefaultValue {
    QUANTITY("1");

    private final String defaultValue;
}
