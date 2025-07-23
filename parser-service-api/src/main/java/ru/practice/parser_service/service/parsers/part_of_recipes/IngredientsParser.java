package ru.practice.parser_service.service.parsers.part_of_recipes;

import lombok.experimental.UtilityClass;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.practice.parser_service.service.enums.CssQueryOfPartsOfRecipes;

import java.util.HashMap;
import java.util.Map;

@UtilityClass
public class IngredientsParser {
    public Map<String, String> parseIngredients(Document doc) {
        Elements ingredientItems = doc.select(CssQueryOfPartsOfRecipes.INGREDIENTS_NODE.cssQuery());
        Map<String, String> ingredientsMap = new HashMap<>();

        for (Element item : ingredientItems) {
            String quantity = item.select(CssQueryOfPartsOfRecipes.INGREDIENT_QUANTITY.cssQuery()).text();
            String unit = item.select(CssQueryOfPartsOfRecipes.INGREDIENTS_UNIT.cssQuery()).text();
            String name = item.select(CssQueryOfPartsOfRecipes.INGREDIENTS_NAME.cssQuery()).text();
            String amount = quantity + " " + unit;

            put(ingredientsMap, name, amount, quantity);
        }
        return ingredientsMap;
    }

    private void put(Map<String, String> ingredientsMap, String name, String amount, String quantity) {
        if (quantity.isEmpty()) {
            ingredientsMap.put(name, "1");
            return;
        }
        ingredientsMap.put(name, amount);
    }
}