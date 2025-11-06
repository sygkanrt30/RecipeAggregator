package ru.practice.parser_service.service.parsers;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import ru.practice.parser_service.service.parsers.recipe.RecipeParser;
import ru.practice.parser_service.service.parsers.recipe.recipes_parts.TimeParser;
import ru.practice.shared.dto.IngredientDto;
import ru.practice.shared.dto.RecipeDto;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mockStatic;

class RecipeParserTest {

    @BeforeAll
    static void setUp() {
        MockedStatic<TimeParser> mockedTime = mockStatic(TimeParser.class);
        mockedTime.when(() -> TimeParser.parseMinsFromString("30 mins"))
                .thenReturn(Duration.ofMinutes(30));
        mockedTime.when(() -> TimeParser.parseMinsFromString("1 hour"))
                .thenReturn(Duration.ofMinutes(60));
    }

    @Test
    void parseRecipePage_shouldReturnCorrectRecipe() {
        // Arrange
        var html = """
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


        // Act
        RecipeDto result = RecipeParser.parseRecipePage(doc);

        // Assert
        assertNotNull(result);
        assertEquals("test recipe", result.name());
        assertEquals("Test Description", result.description());
        assertEquals("Test Directions", result.direction());
        List<IngredientDto> expectedIngredients = List.of(IngredientDto.of(
                "vanilla extract", "1/2", "teaspoon"
        ));
        assertEquals(expectedIngredients, result.ingredients());
        assertEquals(4, result.servings());
        assertEquals(Duration.ofMinutes(30), result.minsForPreparing());
        assertEquals(Duration.ofMinutes(60), result.minsForCooking());
        assertEquals(Duration.ofMinutes(0), result.additionalMins());
        assertEquals(Duration.ofMinutes(0), result.totalMins());
    }

    @Test
    void parseRecipePage_shouldThrowExceptionWhenNameIsMissing() {
        // Arrange
        var html = """
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
        var html = """
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
        // Act
        RecipeDto result = RecipeParser.parseRecipePage(doc);

        // Assert
        assertNotNull(result);
        assertEquals(Duration.ofMinutes(0), result.minsForPreparing());
        assertEquals(Duration.ofMinutes(0), result.minsForCooking());
        assertEquals(Duration.ofMinutes(0), result.additionalMins());
        assertEquals(Duration.ofMinutes(0), result.totalMins());
    }

    @Test
    void parseRecipePage_shouldHandleEmptyDescription() {
        // Arrange
        var html = """
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