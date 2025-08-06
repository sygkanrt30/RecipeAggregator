package ru.practice.parser_service.service.parsers;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import ru.practice.parser_service.model.Recipe;
import ru.practice.parser_service.service.parsers.recipe.part_of_recipe.TimeParser;
import ru.practice.parser_service.service.parsers.recipe.RecipeParser;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mockStatic;

class RecipeParserTest {
    @Test
    void parseRecipePage_shouldReturnCorrectRecipe() {
        // Arrange
        String html = """
            <div>
                <h1 class="article-heading text-headline-400">Test Recipe</h1>
                <p class="article-subheading text-utility-300">Test Description</p>
                <div class="mm-recipes-details">
                    <div class="mm-recipes-details__item">
                        <div class="mm-recipes-details__label">servings:</div>
                        <div class="mm-recipes-details__value">4</div>
                    </div>
                    <div class="mm-recipes-details__item">
                        <div class="mm-recipes-details__label">prep time:</div>
                        <div class="mm-recipes-details__value">30 mins</div>
                    </div>
                    <div class="mm-recipes-details__item">
                        <div class="mm-recipes-details__label">cook time:</div>
                        <div class="mm-recipes-details__value">1 hour</div>
                    </div>
                </div>
                <ul class="mm-recipes-structured-ingredients">
                    <li class="mm-recipes-structured-ingredients__list-item">
                        <span data-ingredient-quantity="true">1/2</span>
                        <span data-ingredient-unit="true">teaspoon</span>
                        <span data-ingredient-name="true">vanilla extract</span>
                    </li>
                </ul>
                <ol class="mntl-sc-block-group">
                    <li><p class="mntl-sc-block-html">Test Directions</p></li>
                </ol>
            </div>
            """;

        Document doc = Jsoup.parse(html);
        try (MockedStatic<TimeParser> mockedTime = mockStatic(TimeParser.class)) {
            mockedTime.when(() -> TimeParser.parseMinutesFromString("30 mins"))
                    .thenReturn(30);
            mockedTime.when(() -> TimeParser.parseMinutesFromString("1 hour"))
                    .thenReturn(60);

            // Act
            Recipe result = RecipeParser.parseRecipePage(doc);

            // Assert
            assertNotNull(result);
            assertEquals("test recipe", result.name());
            assertEquals("Test Description", result.description());
            assertEquals("Test Directions", result.direction());
            Map<String, String> expectedIngredients = Map.of(
                    "vanilla extract", "1/2 teaspoon"
            );
            assertEquals(expectedIngredients, result.ingredients());
            assertEquals(4, result.servings());
            assertEquals(30, result.mins4Prep());
            assertEquals(60, result.mins4Cook());
            assertEquals(0, result.additionalMins());
            assertEquals(0, result.totalMins());
        }
    }

    @Test
    void parseRecipePage_shouldThrowExceptionWhenNameIsMissing() {
        // Arrange
        String html = """
            <div>
                <p class="article-subheading text-utility-300">Test Description</p>
            </div>
            """;
        Document doc = Jsoup.parse(html);

        // Act & Assert
        assertThrows(NullPointerException.class, () -> RecipeParser.parseRecipePage(doc));
    }

    @Test
    void parseRecipePage_shouldHandleMissingTimeParameters() {
        // Arrange
        String html = """
            <div>
                <h1 class="article-heading text-headline-400">Test Recipe</h1>
                <p class="article-subheading text-utility-300">Test Description</p>
                <div class="mm-recipes-details">
                    <div class="mm-recipes-details__item">
                        <div class="mm-recipes-details__label">servings:</div>
                        <div class="mm-recipes-details__value">4</div>
                    </div>
                </div>
                <ul class="mm-recipes-structured-ingredients">
                    <li class="mm-recipes-structured-ingredients__list-item">
                        <span data-ingredient-quantity="true">1</span>
                        <span data-ingredient-unit="true">cup</span>
                        <span data-ingredient-name="true">flour</span>
                    </li>
                </ul>
                <ol class="mntl-sc-block-group">
                    <li><p class="mntl-sc-block-html">Mix ingredients</p></li>
                </ol>
            </div>
            """;

        Document doc = Jsoup.parse(html);
        try (MockedStatic<TimeParser> mockedTime = mockStatic(TimeParser.class)) {
            // Act
            Recipe result = RecipeParser.parseRecipePage(doc);

            // Assert
            assertNotNull(result);
            assertEquals(0, result.mins4Prep());
            assertEquals(0, result.mins4Cook());
            assertEquals(0, result.additionalMins());
            assertEquals(0, result.totalMins());
            mockedTime.verifyNoInteractions();
        }
    }

    @Test
    void parseRecipePage_shouldHandleEmptyDescription() {
        // Arrange
        String html = """
            <div>
                <h1 class="article-heading text-headline-400">Test Recipe</h1>
                <div class="mm-recipes-details">
                    <div class="mm-recipes-details__item">
                        <div class="mm-recipes-details__label">servings:</div>
                        <div class="mm-recipes-details__value">2</div>
                    </div>
                </div>
            </div>
            """;

        Document doc = Jsoup.parse(html);

        // Act & Assert
        assertThrows(NullPointerException.class, () -> RecipeParser.parseRecipePage(doc));
    }
}