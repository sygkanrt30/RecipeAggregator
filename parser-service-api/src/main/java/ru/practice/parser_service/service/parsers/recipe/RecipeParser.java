package ru.practice.parser_service.service.parsers.recipe;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.practice.parser_service.service.parsers.enums.CssQueryOfRecipesParts;
import ru.practice.parser_service.service.parsers.recipe.recipes_parts.DirectionParser;
import ru.practice.parser_service.service.parsers.recipe.recipes_parts.IngredientsParser;
import ru.practice.parser_service.service.parsers.recipe.recipes_parts.TimeParser;
import ru.practice.shared.dto.IngredientDto;
import ru.practice.shared.dto.RecipeDto;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static ru.practice.parser_service.service.parsers.enums.CssQueryOfRecipesParts.*;
import static ru.practice.parser_service.service.parsers.enums.TimeLabel.*;

@UtilityClass
@Slf4j
public class RecipeParser {
    public RecipeDto parseRecipePage(Document doc) {
        log.debug("Start parsing recipe from document {}", doc.baseUri());
        String name = getSimplePartsOfRecipe(doc, NAME);
        String description = getSimplePartsOfRecipe(doc, DESCRIPTION);

        var detailsMap = fillParamsMap(doc);
        Duration mins4Cook = parseTimeParam(COOK_TIME.label(), detailsMap);
        Duration additionalMins = parseTimeParam(ADDITIONAL_TIME.label(), detailsMap);
        Duration totalMins = parseTimeParam(TOTAL_TIME.label(), detailsMap);
        Duration mins4Prep = parseTimeParam(PREP_TIME.label(), detailsMap);

        int servings = Integer.parseInt(detailsMap.get("servings"));

        List<IngredientDto> ingredients = IngredientsParser.parse(doc);
        String directions = DirectionParser.parse(doc);
        var recipe = RecipeDto.builder()
                .name(name.trim().toLowerCase())
                .description(description)
                .direction(directions)
                .ingredients(ingredients)
                .totalMins(totalMins)
                .additionalMins(additionalMins)
                .minsForCooking(mins4Cook)
                .minsForPreparing(mins4Prep)
                .servings(servings)
                .build();
        log.debug("Recipe {}", recipe.toString());
        return recipe;
    }

    private String getSimplePartsOfRecipe(Document doc, CssQueryOfRecipesParts cssQuery) {
        Element simpleElement = doc.selectFirst(cssQuery.cssQuery());
        return Objects.requireNonNull(simpleElement).text();
    }

    private Map<String, String> fillParamsMap(Document doc) {
        var detailsMap = new HashMap<String, String>();
        Elements items = doc.select(RECIPE_DETAILS_ITEM.cssQuery());
        for (var item : items) {
            String label = item.select(RECIPE_DETAILS_LABEL.cssQuery()).text();
            String value = item.select(RECIPE_DETAILS_VALUE.cssQuery()).text();
            var processedLabel = label.substring(0, label.length() - 1).toLowerCase();
            detailsMap.put(processedLabel, value.trim());
        }
        return detailsMap;
    }

    private Duration parseTimeParam(String timeLabel, Map<String, String> detailsMap) {
        if (detailsMap.containsKey(timeLabel)) {
            return TimeParser.parseMinsFromString(detailsMap.get(timeLabel));
        }
        return Duration.ofMinutes(0);
    }
}
