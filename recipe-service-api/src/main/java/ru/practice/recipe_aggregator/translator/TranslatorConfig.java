package ru.practice.recipe_aggregator.translator;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@RequiredArgsConstructor
@Getter
@Accessors(fluent = true)
enum TranslatorConfig {
    SOURCE_LANGUAGE_CODE("sourceLanguageCode"),
    TARGET_LANGUAGE_CODE("targetLanguageCode"),
    TEXTS("texts"),
    FOLDER_ID("folderId");

    private final String key;
}
