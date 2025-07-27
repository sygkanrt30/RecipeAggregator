package ru.practice.parser_service.service.parsers.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

@AllArgsConstructor
@Getter
@Accessors(fluent = true)
public enum ValidHtmlTag {
    HREF("href"),
    HTTP("http"),
    HREF_TAG("a[href]");

    private final String value;
}
