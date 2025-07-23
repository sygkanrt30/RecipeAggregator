package ru.practice.parser_service.service.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

@AllArgsConstructor
@Getter
@Accessors(fluent = true)
public enum InvalidRequestPrefix {
    JAVASCRIPT("javascript:"),
    MAILTO("mailto:"),
    TEL("tel:");

    private final String value;
}
