package ru.practice.parser_service.service.parsers;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.yaml.snakeyaml.Yaml;
import ru.practice.parser_service.service.mapper.RecipeMapper;
import ru.practice.parser_service.service.parsers.recipe.RecipeParser;
import ru.practice.parser_service.service.parsers.recipe.recipes_parts.TimeParser;
import ru.practice.shared.dto.IngredientDto;
import ru.practice.shared.dto.RecipeDto;

import java.io.IOException;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecipeParserTest {

    private static final String TEST_APPLICATION_YAML = "test-application.yaml";
    @Mock
    private RecipeMapper mapper;

    @InjectMocks
    private RecipeParser recipeParser;

    private static MockedStatic<TimeParser> mockedTime;
    private static Document validDoc;
    private static Document missingNameDoc;
    private static Document missingTimeDoc;
    private static Document emptyDescriptionDoc;

    @BeforeAll
    static void setUpAll() throws IOException {
        mockedTime = mockStatic(TimeParser.class);
        mockedTime.when(() -> TimeParser.parseMinsFromString("30 mins"))
                .thenReturn(Duration.ofMinutes(30));
        mockedTime.when(() -> TimeParser.parseMinsFromString("1 hour"))
                .thenReturn(Duration.ofMinutes(60));

        Map<String, String> htmlContents = loadHtmlFromYaml();
        validDoc = Jsoup.parse(htmlContents.get("valid"));
        missingNameDoc = Jsoup.parse(htmlContents.get("missingName"));
        missingTimeDoc = Jsoup.parse(htmlContents.get("missingTime"));
        emptyDescriptionDoc = Jsoup.parse(htmlContents.get("emptyDescription"));
    }

    @SuppressWarnings("unchecked")
    private static Map<String, String> loadHtmlFromYaml() throws IOException {
        var yaml = new Yaml();
        try (var inputStream = RecipeParserTest.class.getClassLoader()
                .getResourceAsStream(TEST_APPLICATION_YAML)) {

            Map<String, Object> data = yaml.load(inputStream);
            var test = (Map<String, Object>) data.get("test");
            var recipes = (Map<String, Object>) test.get("recipes");

            var result = new HashMap<String, String>();
            result.put("valid", getHtml(recipes, "valid"));
            result.put("missingName", getHtml(recipes, "missing-name"));
            result.put("missingTime", getHtml(recipes, "missing-time"));
            result.put("emptyDescription", getHtml(recipes, "empty-description"));

            return result;
        }
    }

    @SuppressWarnings("unchecked")
    private static String getHtml(Map<String, Object> recipes, String key) {
        Map<String, Object> recipe = (Map<String, Object>) recipes.get(key);
        return (String) recipe.get("html");
    }

    @AfterAll
    static void tearDownAll() {
        if (mockedTime != null) {
            mockedTime.close();
        }
    }

    @Test
    void parseRecipePage_shouldReturnCorrectRecipe() {
        // Arrange
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

        // Act
        RecipeDto result = recipeParser.parseRecipePage(validDoc);

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
        // Act & Assert
        assertThrows(NullPointerException.class, () -> recipeParser.parseRecipePage(missingNameDoc));
    }

    @Test
    void parseRecipePage_shouldHandleMissingTimeParameters() {
        // Arrange
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

        // Act
        RecipeDto result = recipeParser.parseRecipePage(missingTimeDoc);

        // Assert
        assertNotNull(result);
        assertEquals(Duration.ofMinutes(0), result.minsForPreparing());
        assertEquals(Duration.ofMinutes(0), result.minsForCooking());
        assertEquals(Duration.ofMinutes(0), result.additionalMins());
        assertEquals(Duration.ofMinutes(0), result.totalMins());
    }

    @Test
    void parseRecipePage_shouldHandleEmptyDescription() {
        // Act & Assert
        assertThrows(NullPointerException.class, () -> recipeParser.parseRecipePage(emptyDescriptionDoc));
    }
}