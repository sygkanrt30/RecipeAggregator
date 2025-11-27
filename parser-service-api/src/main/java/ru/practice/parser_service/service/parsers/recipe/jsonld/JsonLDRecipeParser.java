package ru.practice.parser_service.service.parsers.recipe.jsonld;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import ru.practice.parser_service.service.mapper.RecipeMapper;
import ru.practice.parser_service.service.parsers.recipe.RecipeParser;
import ru.practice.parser_service.service.parsers.recipe.jsonld.recipe_parts.ServingsParser;
import ru.practice.shared.dto.RecipeDto;
import ru.practice.shared.dto.ingredient.IngredientDto;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;

import static ru.practice.parser_service.service.parsers.recipe.jsonld.FieldName.*;

@RequiredArgsConstructor
@Slf4j
@Component("jsonLdParser")
public class JsonLDRecipeParser implements RecipeParser {

    private final static String JSON_LD_SCRIPT = "script[type=application/ld+json]";
    private static final String RECIPE = "Recipe";

    private final RecipeMapper mapper;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Optional<RecipeDto> parseRecipePage(Document doc) {
        try {
            Elements jsonLdScripts = doc.select(JSON_LD_SCRIPT);

            log.debug("Found {} JSON-LD scripts", jsonLdScripts.size());

            for (Element script : jsonLdScripts) {
                String content = script.html().trim();
                log.debug("JSON-LD content length: {}", content.length());
                if (content.contains("\"" + TYPE.value() + "\"") && content.contains(RECIPE)) {
                    log.trace("Found JSON-LD recipe data");
                    try {
                        JsonNode json = objectMapper.readTree(content);
                        log.debug("JSON-LD parsed successfully, isArray: {}", json.isArray());
                        if (json.isArray()) {
                            for (var node : json) {
                                if (isRecipeNode(node)) {
                                    log.debug("Found Recipe node in JSON-LD array");
                                    return extractRecipeFromJsonLd(node);
                                }
                            }
                        } else if (isRecipeNode(json)) {
                            log.debug("Found Recipe object in JSON-LD");
                            return extractRecipeFromJsonLd(json);
                        }
                    } catch (Exception e) {
                        log.debug("Failed to parse JSON-LD: {}", e.getMessage());
                        log.trace("JSON-LD parsing error", e);
                    }
                }
            }
        } catch (Exception e) {
            log.debug("Failed to parse JSON-LD data: {}", e.getMessage());
            log.trace("JSON-LD data parsing error", e);
        }
        return Optional.empty();
    }

    private boolean isRecipeNode(JsonNode node) {
        if (!node.has(TYPE.value())) {
            return false;
        }
        JsonNode typeNode = node.get(TYPE.value());
        if (typeNode.isTextual()) {
            return RECIPE.equals(typeNode.asText());
        } else if (typeNode.isArray()) {
            for (JsonNode type : typeNode) {
                if (RECIPE.equals(type.asText())) {
                    return true;
                }
            }
        }
        return false;
    }


    private Optional<RecipeDto> extractRecipeFromJsonLd(JsonNode json) {
        try {
            log.debug("Extracting recipe from JSON-LD");

            String name = json.path(NAME.value()).asText();
            String description = json.path(DESCRIPTION.value()).asText();

            log.debug("Recipe name from JSON-LD: {}", name);
            log.debug("Recipe description from JSON-LD: {}", description);

            var prepTimeStr = json.path(TimeLabel.PREP_TIME.label()).asText();
            var cookTimeStr = json.path(TimeLabel.COOK_TIME.label()).asText();
            var totalTimeStr = json.path(TimeLabel.TOTAL_TIME.label()).asText();

            log.debug("Time strings - prep: {}, cook: {}, total: {}", prepTimeStr, cookTimeStr, totalTimeStr);

            Duration prepTime = Duration.parse(prepTimeStr);
            Duration cookTime = Duration.parse(cookTimeStr);
            Duration totalTime = Duration.parse(totalTimeStr);

            int servings = ServingsParser.parseServings(json.path(SERVINGS.value()).asText());
            log.debug("Servings from JSON-LD: {}", servings);

            var ingredients = new ArrayList<IngredientDto>();
            JsonNode ingredientsNode = json.path(INGREDIENTS.value());
            extractIngredients(ingredientsNode, ingredients);

            var directions = new StringBuilder();
            JsonNode instructionsNode = json.path(INSTRUCTIONS.value());
            extractInstructions(instructionsNode, directions);

            UUID id = extractRecipeIdFromJsonLd(json);
            log.debug("Extracted recipe ID: {}", id);

            var recipe = mapper.toRecipeDto(
                    id, name, prepTime, cookTime, totalTime, servings,
                    ingredients, directions.toString().trim(), description
            );

            log.debug("Successfully created RecipeDto from JSON-LD");
            return Optional.of(recipe);

        } catch (Exception e) {
            log.debug("Failed to extract recipe from JSON-LD: {}", e.getMessage());
            log.trace("JSON-LD extraction error", e);
            return Optional.empty();
        }
    }

    private void extractInstructions(JsonNode instructionsNode, StringBuilder directions) {
        if (instructionsNode.isArray()) {
            log.debug("Found {} instructions in JSON-LD", instructionsNode.size());
            int step = 1;
            for (JsonNode instruction : instructionsNode) {
                String instructionText = instruction.path(TEXT.value()).asText();
                if (!instructionText.isEmpty()) {
                    directions.append(step++).append(". ").append(instructionText).append("\n");
                    log.trace("Instruction {}: {}", step - 1, instructionText);
                }
            }
        }
    }

    private void extractIngredients(JsonNode ingredientsNode, ArrayList<IngredientDto> ingredients) {
        if (ingredientsNode.isArray()) {
            log.debug("Found {} ingredients in JSON-LD", ingredientsNode.size());
            for (JsonNode ing : ingredientsNode) {
                String ingredientText = ing.asText();
                ingredients.add(IngredientDto.of(ingredientText));
                log.trace("Ingredient: {}", ingredientText);
            }
        }
    }


    private UUID extractRecipeIdFromJsonLd(JsonNode json) {
        if (json.has(URL.value())) {
            return extractIdFromUrl(json.path(URL.value()).asText());
        }
        return UUID.randomUUID();
    }

    private UUID extractIdFromUrl(String url) {
        return Pattern.compile("/")
                .splitAsStream(url)
                .filter(part -> part.matches("\\d+"))
                .findFirst()
                .map(part -> UUID.nameUUIDFromBytes(part.getBytes()))
                .orElse(UUID.randomUUID());
    }
}