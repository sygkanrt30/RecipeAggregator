package ru.practice.shared.dto.ingredient;

import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

interface ExtractorFabric {

    static Deque<Function<String, IngredientDto>> getBasicExtractorQueue() {
        return new LinkedList<>(List.of(
                new RangeQuantityExtractor(),
                new FractionExtractor(),
                new QuantityUnitExtractor(),
                new QuantityOnlyExtractor(),
                new ParenthesesExtractor(),
                new FallbackExtractor()
        ));
    }
}
