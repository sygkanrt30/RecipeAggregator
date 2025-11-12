package ru.practice.parser_service.service.parsers;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practice.parser_service.service.mapper.RecipeMapper;
import ru.practice.parser_service.service.parsers.recipe.RecipeParser;
import ru.practice.parser_service.service.parsers.recipe.recipes_parts.TimeParser;
import ru.practice.shared.dto.IngredientDto;
import ru.practice.shared.dto.RecipeDto;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecipeParserTest {

    @Mock
    private RecipeMapper mapper;

    @InjectMocks
    private RecipeParser recipeParser;

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
        var expectedName = "Test Recipe";
        var expectedPrepTime = Duration.ofMinutes(30);
        var expectedCookTime = Duration.ofMinutes(60);
        var expectedAdditionalMins = Duration.ofMinutes(0);
        var expectedTotalMins = Duration.ofMinutes(0);
        int expectedServings = 4;
        var expectedIngredients = List.of(IngredientDto.of(
                "vanilla extract", "1/2", "teaspoon"
        ));
        var expectedDirection = "Test Directions";
        var expectedDescription = "Test Description";
        var expectedId = UUID.randomUUID();

        var expectedRecipeDto = new RecipeDto(
                expectedId,
                "test recipe",
                expectedPrepTime,
                expectedCookTime,
                expectedAdditionalMins,
                expectedTotalMins,
                expectedServings,
                expectedIngredients,
                expectedDirection,
                expectedDescription
        );

        when(mapper.toRecipeDto(
                expectedName,
                expectedPrepTime,
                expectedCookTime,
                expectedAdditionalMins,
                expectedTotalMins,
                expectedServings,
                expectedIngredients,
                expectedDirection,
                expectedDescription
        )).thenReturn(expectedRecipeDto);

        Document doc = Jsoup.parse(html);

        // Act
        RecipeDto result = recipeParser.parseRecipePage(doc);

        // Assert
        assertNotNull(result);
        assertEquals("test recipe", result.name());
        assertEquals("Test Description", result.description());
        assertEquals("Test Directions", result.direction());
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
        assertThrows(NullPointerException.class, () -> recipeParser.parseRecipePage(doc));
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

        var expectedName = "Test Recipe";
        var zeroDuration = Duration.ofMinutes(0);
        int expectedServings = 4;
        var expectedIngredients = List.of(IngredientDto.of(
                "flour", "1", "cup"
        ));
        var expectedDirection = "Mix ingredients";
        var expectedDescription = "Test Description";
        var expectedId = UUID.randomUUID();

        var expectedRecipeDto = new RecipeDto(
                expectedId,
                "test recipe",
                zeroDuration,
                zeroDuration,
                zeroDuration,
                zeroDuration,
                expectedServings,
                expectedIngredients,
                expectedDirection,
                expectedDescription
        );

        when(mapper.toRecipeDto(
                eq(expectedName),
                eq(zeroDuration),
                eq(zeroDuration),
                eq(zeroDuration),
                eq(zeroDuration),
                eq(expectedServings),
                eq(expectedIngredients),
                eq(expectedDirection),
                eq(expectedDescription)
        )).thenReturn(expectedRecipeDto);

        Document doc = Jsoup.parse(html);
        // Act
        RecipeDto result = recipeParser.parseRecipePage(doc);

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
        assertThrows(NullPointerException.class, () -> recipeParser.parseRecipePage(doc));
    }
}