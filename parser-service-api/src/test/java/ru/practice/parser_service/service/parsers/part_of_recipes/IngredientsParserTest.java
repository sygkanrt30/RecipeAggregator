package ru.practice.parser_service.service.parsers.part_of_recipes;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Test;
import ru.practice.parser_service.service.parsers.recipe.part_of_recipe.IngredientsParser;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class IngredientsParserTest {

    @Test
    void parseIngredients_shouldReturnEmptyMap_whenNoIngredientsFound() {
        // Arrange
        String html = "<div>No ingredients here</div>";
        Document doc = Jsoup.parse(html);

        // Act
        Map<String, String> result = IngredientsParser.parseIngredients(doc);

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void parseIngredients_shouldParseSingleIngredient() {
        // Arrange
        String html = "<ul>" +
                "<li class='mm-recipes-structured-ingredients__list-item'>" +
                "<span data-ingredient-quantity='true'>2</span>" +
                "<span data-ingredient-unit='true'>cups</span>" +
                "<span data-ingredient-name='true'>flour</span>" +
                "</li>" +
                "</ul>";
        Document doc = Jsoup.parse(html);

        // Act
        Map<String, String> result = IngredientsParser.parseIngredients(doc);

        // Assert
        assertEquals(1, result.size());
        assertEquals("2 cups", result.get("flour"));
    }

    @Test
    void parseIngredients_shouldHandleEmptyQuantity() {
        // Arrange
        String html = "<ul>" +
                "<li class='mm-recipes-structured-ingredients__list-item'>" +
                "<span data-ingredient-quantity='true'></span>" +
                "<span data-ingredient-unit='true'>to taste</span>" +
                "<span data-ingredient-name='true'>salt</span>" +
                "</li>" +
                "</ul>";
        Document doc = Jsoup.parse(html);

        // Act
        Map<String, String> result = IngredientsParser.parseIngredients(doc);

        // Assert
        assertEquals(1, result.size());
        assertEquals("1", result.get("salt"));
    }

    @Test
    void parseIngredients_shouldHandleFractionalQuantities() {
        // Arrange
        String html = "<ul>" +
                "<li class='mm-recipes-structured-ingredients__list-item'>" +
                "<span data-ingredient-quantity='true'>1/2</span>" +
                "<span data-ingredient-unit='true'>teaspoon</span>" +
                "<span data-ingredient-name='true'>vanilla extract</span>" +
                "</li>" +
                "</ul>";
        Document doc = Jsoup.parse(html);

        // Act
        Map<String, String> result = IngredientsParser.parseIngredients(doc);

        // Assert
        assertEquals(1, result.size());
        assertEquals("1/2 teaspoon", result.get("vanilla extract"));
    }

    @Test
    void parseIngredients_shouldHandleMultipleIngredients() {
        // Arrange
        String html = "<ul>" +
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
        Map<String, String> result = IngredientsParser.parseIngredients(doc);

        // Assert
        assertEquals(2, result.size());
        assertEquals("2 cups", result.get("flour"));
        assertEquals("1 cup", result.get("sugar"));
    }

    @Test
    void parseIngredients_shouldHandleComplexIngredientNames() {
        // Arrange
        String html = "<ul>" +
                "<li class='mm-recipes-structured-ingredients__list-item'>" +
                "<span data-ingredient-quantity='true'>3</span>" +
                "<span data-ingredient-unit='true'>tablespoons</span>" +
                "<span data-ingredient-name='true'>extra-virgin olive oil, divided</span>" +
                "</li>" +
                "</ul>";
        Document doc = Jsoup.parse(html);

        // Act
        Map<String, String> result = IngredientsParser.parseIngredients(doc);

        // Assert
        assertEquals(1, result.size());
        assertEquals("3 tablespoons", result.get("extra-virgin olive oil, divided"));
    }
}