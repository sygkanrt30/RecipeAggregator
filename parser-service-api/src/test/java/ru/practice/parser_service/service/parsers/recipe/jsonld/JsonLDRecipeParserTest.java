package ru.practice.parser_service.service.parsers.recipe.jsonld;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.yaml.snakeyaml.Yaml;
import ru.practice.parser_service.service.mapper.RecipeMapper;
import ru.practice.shared.dto.RecipeDto;
import ru.practice.shared.dto.ingredient.IngredientDto;

import java.io.IOException;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("OptionalGetWithoutIsPresent")
class JsonLDRecipeParserTest {

    private static final String TEST_APPLICATION_YAML = "test-application.yaml";

    @Mock
    private RecipeMapper mapper;

    private JsonLDRecipeParser recipeParser;

    private static Map<String, String> jsonLdContents;

    @BeforeAll
    static void setUpAll() throws IOException {
        jsonLdContents = loadJsonLdFromYaml();
    }

    @BeforeEach
    void setUp() {
        var extractor = new RecipeExtractor(mapper);
        recipeParser = new JsonLDRecipeParser(extractor);
    }

    @SuppressWarnings("unchecked")
    private static Map<String, String> loadJsonLdFromYaml() throws IOException {
        var yaml = new Yaml();
        try (var inputStream = JsonLDRecipeParserTest.class.getClassLoader()
                .getResourceAsStream(TEST_APPLICATION_YAML)) {

            Map<String, Object> data = yaml.load(inputStream);
            var test = (Map<String, Object>) data.get("test");
            var recipes = (Map<String, Object>) test.get("recipes");
            var jsonLd = (Map<String, Object>) recipes.get("json-ld");

            var result = new HashMap<String, String>();
            result.put("valid", (String) jsonLd.get("valid"));
            result.put("array", (String) jsonLd.get("array"));
            result.put("multipleTypes", (String) jsonLd.get("multiple-types"));
            result.put("missingFields", (String) jsonLd.get("missing-fields"));
            result.put("invalidTime", (String) jsonLd.get("invalid-time"));
            result.put("emptyIngredients", (String) jsonLd.get("empty-ingredients"));
            result.put("notRecipe", (String) jsonLd.get("not-recipe"));
            result.put("withUrlId", (String) jsonLd.get("with-url-id"));

            return result;
        }
    }

    private Document createHtmlWithJsonLd(String jsonLd) {
        var html = """
                <html>
                    <head>
                        <script type="application/ld+json">
                            %s
                        </script>
                    </head>
                    <body>
                        <h1>Test Page</h1>
                    </body>
                </html>
                """.formatted(jsonLd);
        return Jsoup.parse(html);
    }

    @Test
    void parseRecipePage_shouldReturnCorrectRecipe() {
        // Arrange
        var jsonLd = jsonLdContents.get("valid");
        var doc = createHtmlWithJsonLd(jsonLd);

        var expectedId = UUID.nameUUIDFromBytes("12345".getBytes());
        var expectedIngredients = List.of(
                IngredientDto.of("2 cups all-purpose flour"),
                IngredientDto.of("1 cup sugar"),
                IngredientDto.of("1/2 cup cocoa powder")
        );

        var expected = RecipeDto.builder()
                .id(expectedId)
                .name("Test Chocolate Cake")
                .description("A delicious and moist chocolate cake recipe")
                .timeForPreparing(Duration.ofMinutes(30))
                .timeForCooking(Duration.ofHours(1))
                .totalTime(Duration.ofMinutes(90))
                .servings(8)
                .ingredients(expectedIngredients)
                .direction("1. Preheat oven to 350°F (175°C)\n2. Mix dry ingredients together")
                .build();

        when(mapper.toRecipeDto(
                eq(expectedId),
                eq("Test Chocolate Cake"),
                eq(Duration.ofMinutes(30)),
                eq(Duration.ofHours(1)),
                eq(Duration.ofMinutes(90)),
                eq(8),
                eq(expectedIngredients),
                eq("1. Preheat oven to 350°F (175°C)\n2. Mix dry ingredients together"),
                eq("A delicious and moist chocolate cake recipe")
        )).thenReturn(expected);

        // Act
        var result = recipeParser.parseRecipePage(doc).get();

        // Assert
        assertEquals(expected, result);
    }

    @Test
    void parseRecipePage_shouldReturnRecipeFromArray() {
        // Arrange
        var jsonLd = jsonLdContents.get("array");
        var doc = createHtmlWithJsonLd(jsonLd);

        var expectedIngredients = List.of(
                IngredientDto.of("1 cup flour"),
                IngredientDto.of("2 eggs")
        );

        var expected = RecipeDto.builder().build();

        when(mapper.toRecipeDto(
                any(UUID.class),
                eq("Test Recipe in Array"),
                eq(Duration.ofMinutes(15)),
                eq(Duration.ofMinutes(45)),
                eq(Duration.ofHours(1)),
                eq(4),
                eq(expectedIngredients),
                eq("1. Step 1 instruction"),
                eq("Recipe description")
        )).thenReturn(expected);

        // Act
        var result = recipeParser.parseRecipePage(doc);

        // Assert
        assertTrue(result.isPresent());
    }

    @Test
    void parseRecipePage_shouldReturnRecipeWithMultipleTypes() {
        // Arrange
        var jsonLd = jsonLdContents.get("multipleTypes");
        var doc = createHtmlWithJsonLd(jsonLd);

        var expectedIngredients = List.of(
                IngredientDto.of("1 tomato"),
                IngredientDto.of("1 onion")
        );

        var expected = RecipeDto.builder().build();

        when(mapper.toRecipeDto(
                any(UUID.class),
                eq("Test Recipe Multiple Types"),
                eq(Duration.ofMinutes(10)),
                eq(Duration.ofMinutes(20)),
                eq(Duration.ofMinutes(30)),
                eq(2),
                eq(expectedIngredients),
                eq("1. Chop vegetables"),
                eq("Recipe with multiple types")
        )).thenReturn(expected);

        // Act
        var result = recipeParser.parseRecipePage(doc);

        // Assert
        assertTrue(result.isPresent());
    }

    @Test
    void parseRecipePage_shouldReturnEmptyWhenMissingRequiredFields() {
        // Arrange
        var jsonLd = jsonLdContents.get("missingFields");
        var doc = createHtmlWithJsonLd(jsonLd);

        // Act
        var result = recipeParser.parseRecipePage(doc);

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void parseRecipePage_shouldReturnEmptyWhenInvalidTimeFormat() {
        // Arrange
        var jsonLd = jsonLdContents.get("invalidTime");
        var doc = createHtmlWithJsonLd(jsonLd);

        // Act
        var result = recipeParser.parseRecipePage(doc);

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void parseRecipePage_shouldReturnEmptyWhenNotRecipe() {
        // Arrange
        var jsonLd = jsonLdContents.get("notRecipe");
        var doc = createHtmlWithJsonLd(jsonLd);

        // Act
        var result = recipeParser.parseRecipePage(doc);

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void parseRecipePage_shouldReturnRecipeWithEmptyIngredients() {
        // Arrange
        var jsonLd = jsonLdContents.get("emptyIngredients");
        var doc = createHtmlWithJsonLd(jsonLd);

        var expected = RecipeDto.builder().build();

        when(mapper.toRecipeDto(
                any(UUID.class),
                eq("Test Recipe Empty Ingredients"),
                eq(Duration.ofMinutes(15)),
                eq(Duration.ofMinutes(30)),
                eq(Duration.ofMinutes(45)),
                eq(4),
                eq(List.of()),
                eq(""),
                eq("Recipe with empty ingredients")
        )).thenReturn(expected);

        // Act
        var result = recipeParser.parseRecipePage(doc);

        // Assert
        assertTrue(result.isPresent());
    }


    @Test
    void parseRecipePage_shouldFindRecipeAmongMultipleScripts() {
        // Arrange
        var jsonLd = jsonLdContents.get("valid");
        var html = """
                <html>
                    <head>
                        <script type="application/ld+json">
                            {
                                "@context": "https://schema.org",
                                "@type": "Article",
                                "headline": "Some Article"
                            }
                        </script>
                        <script type="application/ld+json">
                            %s
                        </script>
                    </head>
                </html>
                """.formatted(jsonLd);
        var doc = Jsoup.parse(html);

        var expected = RecipeDto.builder().build();

        when(mapper.toRecipeDto(
                any(UUID.class),
                anyString(),
                any(Duration.class),
                any(Duration.class),
                any(Duration.class),
                anyInt(),
                anyList(),
                anyString(),
                anyString()
        )).thenReturn(expected);

        // Act
        var result = recipeParser.parseRecipePage(doc);

        // Assert
        assertTrue(result.isPresent());
    }

    @Test
    void parseRecipePage_shouldReturnEmptyWhenInvalidJson() {
        // Arrange
        var html = """
                <html>
                    <head>
                        <script type="application/ld+json">
                            { invalid json
                        </script>
                    </head>
                </html>
                """;
        var doc = Jsoup.parse(html);

        // Act
        var result = recipeParser.parseRecipePage(doc);

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void parseRecipePage_shouldReturnEmptyWhenNoJsonLd() {
        // Arrange
        var html = """
                <html>
                    <head>
                        <script type="text/javascript">
                            var someVar = "value";
                        </script>
                    </head>
                </html>
                """;
        var doc = Jsoup.parse(html);

        // Act
        var result = recipeParser.parseRecipePage(doc);

        // Assert
        assertTrue(result.isEmpty());
    }
}