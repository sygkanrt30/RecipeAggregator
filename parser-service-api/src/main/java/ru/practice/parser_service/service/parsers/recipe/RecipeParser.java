package ru.practice.parser_service.service.parsers.recipe;

import lombok.experimental.UtilityClass;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.practice.parser_service.model.Recipe;
import ru.practice.parser_service.service.parsers.enums.CssQueryOfPartsOfRecipes;
import ru.practice.parser_service.service.parsers.recipe.part_of_recipe.DirectionParser;
import ru.practice.parser_service.service.parsers.recipe.part_of_recipe.IngredientsParser;
import ru.practice.parser_service.service.parsers.recipe.part_of_recipe.TimeParser;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@UtilityClass
public class RecipeParser {
    public Recipe parseRecipePage(Document doc) {
        Element nameElement = doc.selectFirst(CssQueryOfPartsOfRecipes.NAME.cssQuery());
        String name = Objects.requireNonNull(nameElement).text();

        Element descriptionElement = doc.selectFirst(CssQueryOfPartsOfRecipes.DESCRIPTION.cssQuery());
        String description = Objects.requireNonNull(descriptionElement).text();

        var detailsMap = fillParamsMap(doc);

        int servings = Integer.parseInt(detailsMap.get("servings"));

        Duration timeForCooking = parseTimeParam("cook time", detailsMap);
        Duration additionalTime = parseTimeParam("additional time", detailsMap);
        Duration totalTime = parseTimeParam("total time", detailsMap);
        Duration timeForPrep = parseTimeParam("prep time", detailsMap);

        Map<String, String> ingredients = IngredientsParser.parseIngredients(doc);

        String directions = DirectionParser.parseDirections(doc);
        return Recipe.builder()
                .name(name)
                .description(description)
                .direction(directions)
                .ingredients(ingredients)
                .timeForPreparing(timeForPrep)
                .additionalTime(additionalTime)
                .timeForCooking(timeForCooking)
                .servings(servings)
                .totalTime(totalTime)
                .build();
    }

    private Map<String, String> fillParamsMap(Document doc) {
        var detailsMap = new HashMap<String, String>();
        Elements items = doc.select(CssQueryOfPartsOfRecipes.RECIPE_DETAILS_ITEM.cssQuery());
        for (Element item : items) {
            String label = item.select(CssQueryOfPartsOfRecipes.RECIPE_DETAILS_LABEL.cssQuery()).text();
            String value = item.select(CssQueryOfPartsOfRecipes.RECIPE_DETAILS_VALUE.cssQuery()).text().trim();
            detailsMap.put(label.substring(0, label.length() - 1).toLowerCase(), value);
        }
        return detailsMap;
    }

    private Duration parseTimeParam(String timeLabel, Map<String, String> detailsMap) {
        if (detailsMap.containsKey(timeLabel)) {
            return TimeParser.parseDurationFromString(detailsMap.get(timeLabel));
        }
        return Duration.ZERO;
    }
}
