package ru.practice.parser_service.service.parsers.recipe.html.recipe_parts;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import ru.practice.shared.dto.ingredient.IngredientDto;

import java.util.ArrayList;
import java.util.List;

import static ru.practice.parser_service.service.parsers.recipe.html.CssQueryOfHtmlRecipeParts.*;

@Slf4j
@UtilityClass
public class IngredientsParser {

    public List<IngredientDto> parse(Document doc) {
        log.trace("Start parsing ingredients from document {}", doc.baseUri());
        var ingredients = new ArrayList<IngredientDto>();
        Elements ingredientItems = doc.select(INGREDIENTS_NODE.cssQuery());
        ingredientItems.forEach(item -> {
            String quantity = item.select(INGREDIENT_QUANTITY.cssQuery()).text();
            String unit = item.select(INGREDIENTS_UNIT.cssQuery()).text();
            String name = item.select(INGREDIENTS_NAME.cssQuery()).text();
            ingredients.add(IngredientDto.of(name, quantity, unit));
        });
        logMapIfLevelDebug(ingredients);
        return ingredients;
    }

    private static void logMapIfLevelDebug(List<IngredientDto> ingredients) {
        if (log.isTraceEnabled()) {
            ingredients.forEach(ingredient ->
                    log.trace("Ingredient {}: {} {}", ingredient.name(), ingredient.quantity(), ingredient.unit()));
        }
    }
}