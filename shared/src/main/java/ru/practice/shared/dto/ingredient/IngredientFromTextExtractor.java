package ru.practice.shared.dto.ingredient;

import java.util.Deque;
import java.util.function.Function;

final class IngredientFromTextExtractor {
    private final static Deque<Function<String, IngredientDto>> EXTRACTORS = ExtractorFabric.getBasicExtractorQueue();

    static IngredientDto extract(String text) {
        for (var extractor : EXTRACTORS) {
            IngredientDto result = extractor.apply(text);
            if (result != null) {
                return result;
            }
        }
        return null;
    }
}
