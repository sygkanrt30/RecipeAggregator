package ru.practice.parser_service.service.parsers.recipe.recipes_parts;

import lombok.experimental.UtilityClass;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.Map;

import static ru.practice.parser_service.service.parsers.enums.CssQueryOfRecipesParts.*;

@UtilityClass
public class IngredientsParser {
    private final String DEFAULT_VALUE = "1";

    public Map<String, String> parse(Document doc) {
        Map<String, String> ingredientsMap = new HashMap<>();
        Elements ingredientItems = doc.select(INGREDIENTS_NODE.cssQuery());
        for (var item : ingredientItems) {
            String quantity = item.select(INGREDIENT_QUANTITY.cssQuery()).text();
            String unit = item.select(INGREDIENTS_UNIT.cssQuery()).text();
            String name = item.select(INGREDIENTS_NAME.cssQuery()).text();
            String amount = quantity + " " + unit;
            put(ingredientsMap, name, amount, quantity);
        }
        return ingredientsMap;
    }

    private void put(Map<String, String> ingredientsMap, String name, String amount, String quantity) {
        if (quantity.isEmpty()) {
            ingredientsMap.put(name, DEFAULT_VALUE);
            return;
        }
        ingredientsMap.put(name, amount);
    }
}