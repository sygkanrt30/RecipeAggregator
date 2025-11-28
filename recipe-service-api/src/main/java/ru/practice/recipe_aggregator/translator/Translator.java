package ru.practice.recipe_aggregator.translator;

import ru.practice.shared.dto.RecipeDto;

import java.util.List;

public interface Translator {
    String translate(String text, String sourceLang, String targetLang);

    List<RecipeDto> translateListOfRecipeDtos(List<RecipeDto> recipes);
}
