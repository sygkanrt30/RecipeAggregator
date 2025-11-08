package ru.practice.parser_service.service.parsers.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

@AllArgsConstructor
@Getter
@Accessors(fluent = true)
public enum TimeLabel {

    COOK_TIME("cook time"),
    PREP_TIME("prep time"),
    TOTAL_TIME("total time"),
    ADDITIONAL_TIME("additional time");

    private final String label;
}
