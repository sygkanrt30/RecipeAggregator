package ru.practice.parser_service.service.parsers.html;

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
import ru.practice.parser_service.service.parsers.recipe.html.HtmlRecipeParser;
import ru.practice.parser_service.service.parsers.recipe.html.recipe_parts.TimeParser;
import ru.practice.shared.dto.RecipeDto;
import ru.practice.shared.dto.ingredient.IngredientDto;

import java.io.IOException;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("OptionalGetWithoutIsPresent")
class HtmlRecipeParserTest {

    private static final String TEST_APPLICATION_YAML = "test-application.yaml";
    @Mock
    private RecipeMapper mapper;

    @InjectMocks
    private HtmlRecipeParser recipeParser;

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
        try (var inputStream = HtmlRecipeParserTest.class.getClassLoader()
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
        var expectedTotalMins = Duration.ofMinutes(0);
        int expectedServings = 4;
        var expectedIngredients = List.of(IngredientDto.of(
                "vanilla extract", "1/2", "teaspoon"
        ));
        var expectedDirection = "Test Directions";
        var expectedDescription = "Test Description";

        var expected = RecipeDto.builder()
                .id(UUID.randomUUID())
                .name(expectedName)
                .description(expectedDescription)
                .direction(expectedDirection)
                .ingredients(expectedIngredients)
                .servings(expectedServings)
                .timeForPreparing(expectedPrepTime)
                .timeForCooking(expectedCookTime)
                .totalTime(expectedTotalMins)
                .build();
        when(mapper.toRecipeDto(
                any(UUID.class),
                eq(expectedName),
                eq(expectedPrepTime),
                eq(expectedCookTime),
                eq(expectedTotalMins),
                eq(expectedServings),
                eq(expectedIngredients),
                eq(expectedDirection),
                eq(expectedDescription)
        )).thenReturn(expected);

        RecipeDto result = recipeParser.parseRecipePage(validDoc).get();

        assertEquals(expected, result);
    }

    @Test
    void parseRecipePage_shouldThrowExceptionWhenNameIsMissing() {
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

        var expected = RecipeDto.builder()
                .id(UUID.randomUUID())
                .name(expectedName)
                .description(expectedDescription)
                .direction(expectedDirection)
                .ingredients(expectedIngredients)
                .servings(expectedServings)
                .timeForPreparing(zeroDuration)
                .timeForCooking(zeroDuration)
                .totalTime(zeroDuration)
                .build();

        when(mapper.toRecipeDto(
                any(UUID.class),
                eq(expectedName),
                eq(zeroDuration),
                eq(zeroDuration),
                eq(zeroDuration),
                eq(expectedServings),
                eq(expectedIngredients),
                eq(expectedDirection),
                eq(expectedDescription)
        )).thenReturn(expected);

        // Act
        RecipeDto result = recipeParser.parseRecipePage(missingTimeDoc).get();

        // Assert
        assertNotNull(result);
        assertEquals(Duration.ofMinutes(0), result.timeForPreparing());
        assertEquals(Duration.ofMinutes(0), result.timeForCooking());
        assertEquals(Duration.ofMinutes(0), result.totalTime());
    }

    @Test
    void parseRecipePage_shouldHandleEmptyDescription() {
        // Act & Assert
        assertThrows(NullPointerException.class, () -> recipeParser.parseRecipePage(emptyDescriptionDoc));
    }
}