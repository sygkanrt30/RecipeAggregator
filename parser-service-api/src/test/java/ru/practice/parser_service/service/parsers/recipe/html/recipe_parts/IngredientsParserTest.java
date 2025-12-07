package ru.practice.parser_service.service.parsers.recipe.html.recipe_parts;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Test;
import ru.practice.shared.dto.ingredient.IngredientDto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class IngredientsParserTest {

    @Test
    void parseIngredients_shouldReturnEmptyMap_whenNoIngredientsFound() {
        // Arrange
        var html = "<div>No ingredients here</div>";
        Document doc = Jsoup.parse(html);

        // Act
        List<IngredientDto> result = IngredientsParser.parse(doc);

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void parseIngredients_shouldParseSingleIngredient() {
        // Arrange
        var html = "<ul>" +
                "<li class='mm-recipes-structured-ingredients__list-item'>" +
                "<span data-ingredient-quantity='true'>2</span>" +
                "<span data-ingredient-unit='true'>cups</span>" +
                "<span data-ingredient-name='true'>flour</span>" +
                "</li>" +
                "</ul>";
        Document doc = Jsoup.parse(html);

        // Act
        List<IngredientDto> result = IngredientsParser.parse(doc);

        // Assert
        assertEquals(1, result.size());
        IngredientDto ingredient = result.getFirst();
        assertEquals("flour", ingredient.name());
        assertEquals("2", ingredient.quantity());
        assertEquals("cups", ingredient.unit());
    }

    @Test
    void parseIngredients_shouldHandleEmptyQuantity() {
        // Arrange
        var html = "<ul>" +
                "<li class='mm-recipes-structured-ingredients__list-item'>" +
                "<span data-ingredient-quantity='true'></span>" +
                "<span data-ingredient-unit='true'>to taste</span>" +
                "<span data-ingredient-name='true'>salt</span>" +
                "</li>" +
                "</ul>";
        Document doc = Jsoup.parse(html);

        // Act
        List<IngredientDto> result = IngredientsParser.parse(doc);

        // Assert
        assertEquals(1, result.size());
        IngredientDto ingredient = result.getFirst();
        assertEquals("salt", ingredient.name());
        assertEquals("1", ingredient.quantity());
        assertEquals("to taste", ingredient.unit());
    }

    @Test
    void parseIngredients_shouldHandleFractionalQuantities() {
        // Arrange
        var html = "<ul>" +
                "<li class='mm-recipes-structured-ingredients__list-item'>" +
                "<span data-ingredient-quantity='true'>1/2</span>" +
                "<span data-ingredient-unit='true'>teaspoon</span>" +
                "<span data-ingredient-name='true'>vanilla extract</span>" +
                "</li>" +
                "</ul>";
        Document doc = Jsoup.parse(html);

        // Act
        List<IngredientDto> result = IngredientsParser.parse(doc);

        // Assert
        assertEquals(1, result.size());
        IngredientDto ingredient = result.getFirst();
        assertEquals("vanilla extract", ingredient.name());
        assertEquals("1/2", ingredient.quantity());
        assertEquals("teaspoon", ingredient.unit());
    }

    @Test
    void parseIngredients_shouldHandleMultipleIngredients() {
        // Arrange
        var html = "<ul>" +
                "<li class='mm-recipes-structured-ingredients__list-item'>" +
                "<span data-ingredient-quantity='true'>2</span>" +
                "<span data-ingredient-unit='true'>cups</span>" +
                "<span data-ingredient-name='true'>flour</span>" +
                "</li>" +
                "<li class='mm-recipes-structured-ingredients__list-item'>" +
                "<span data-ingredient-quantity='true'>1</span>" +
                "<span data-ingredient-unit='true'>cup</span>" +
                "<span data-ingredient-name='true'>sugar</span>" +
                "</li>" +
                "</ul>";
        Document doc = Jsoup.parse(html);

        // Act
        List<IngredientDto> result = IngredientsParser.parse(doc);

        // Assert
        assertEquals(2, result.size());

        IngredientDto flour = result.getFirst();
        assertEquals("flour", flour.name());
        assertEquals("2", flour.quantity());
        assertEquals("cups", flour.unit());

        IngredientDto sugar = result.get(1);
        assertEquals("sugar", sugar.name());
        assertEquals("1", sugar.quantity());
        assertEquals("cup", sugar.unit());
    }

    @Test
    void parseIngredients_shouldHandleComplexIngredientNames() {
        // Arrange
        var html = "<ul>" +
                "<li class='mm-recipes-structured-ingredients__list-item'>" +
                "<span data-ingredient-quantity='true'>3</span>" +
                "<span data-ingredient-unit='true'>tablespoons</span>" +
                "<span data-ingredient-name='true'>extra-virgin olive oil, divided</span>" +
                "</li>" +
                "</ul>";
        Document doc = Jsoup.parse(html);

        // Act
        List<IngredientDto> result = IngredientsParser.parse(doc);

        // Assert
        assertEquals(1, result.size());
        IngredientDto ingredient = result.getFirst();
        assertEquals("extra-virgin olive oil, divided", ingredient.name());
        assertEquals("3", ingredient.quantity());
        assertEquals("tablespoons", ingredient.unit());
    }
}