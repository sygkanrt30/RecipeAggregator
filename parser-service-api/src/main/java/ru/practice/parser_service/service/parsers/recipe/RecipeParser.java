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

        int mins4Cook = parseTimeParam("cook time", detailsMap);
        int additionalMins = parseTimeParam("additional time", detailsMap);
        int totalMins = parseTimeParam("total time", detailsMap);
        int mins4Prep = parseTimeParam("prep time", detailsMap);

        Map<String, String> ingredients = IngredientsParser.parseIngredients(doc);

        String directions = DirectionParser.parseDirections(doc);
        return Recipe.builder()
                .name(name.trim().toLowerCase())
                .description(description)
                .direction(directions)
                .ingredients(ingredients)
                .totalMins(totalMins)
                .additionalMins(additionalMins)
                .mins4Cook(mins4Cook)
                .mins4Prep(mins4Prep)
                .servings(servings)
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

    private int parseTimeParam(String timeLabel, Map<String, String> detailsMap) {
        if (detailsMap.containsKey(timeLabel)) {
            return TimeParser.parseMinutesFromString(detailsMap.get(timeLabel));
        }
        return 0;
    }
}
