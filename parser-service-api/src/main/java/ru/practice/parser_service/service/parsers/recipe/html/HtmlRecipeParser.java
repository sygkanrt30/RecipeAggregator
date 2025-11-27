package ru.practice.parser_service.service.parsers.recipe.html;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import ru.practice.parser_service.service.exception.ParserException;
import ru.practice.parser_service.service.mapper.RecipeMapper;
import ru.practice.parser_service.service.parsers.recipe.RecipeParser;
import ru.practice.parser_service.service.parsers.recipe.html.recipe_parts.DirectionParser;
import ru.practice.parser_service.service.parsers.recipe.html.recipe_parts.IngredientsParser;
import ru.practice.parser_service.service.parsers.recipe.html.recipe_parts.TimeParser;
import ru.practice.shared.dto.RecipeDto;
import ru.practice.shared.dto.ingredient.IngredientDto;

import java.time.Duration;
import java.util.*;

import static ru.practice.parser_service.service.parsers.recipe.html.CssQueryOfHtmlRecipeParts.*;
import static ru.practice.parser_service.service.parsers.recipe.html.TimeLabel.*;

@RequiredArgsConstructor
@Slf4j
@Component("htmlParser")
public class HtmlRecipeParser implements RecipeParser {

    private final RecipeMapper mapper;

    @Override
    public Optional<RecipeDto> parseRecipePage(Document doc) {
        log.debug("Start parsing recipe from document {}", doc.baseUri());
        String name = findFirstValueByCssQuery(doc, NAME)
                .orElseThrow(() -> new ParserException("No name found"));
        String description = findFirstValueByCssQuery(doc, DESCRIPTION)
                .orElseThrow(() -> new ParserException("No description found"));

        var detailsMap = fillParamsMap(doc);
        Duration mins4Cook = parseTimeParam(COOK_TIME.label(), detailsMap);
        Duration totalMins = parseTimeParam(TOTAL_TIME.label(), detailsMap);
        Duration mins4Prep = parseTimeParam(PREP_TIME.label(), detailsMap);

        int servings = Integer.parseInt(detailsMap.get("servings"));

        List<IngredientDto> ingredients = IngredientsParser.parse(doc);
        String directions = DirectionParser.parse(doc);
        var recipe = mapper.toRecipeDto(
                UUID.randomUUID(),
                name,
                mins4Prep,
                mins4Cook,
                totalMins,
                servings,
                ingredients,
                directions,
                description);
        log.debug("Recipe {}", recipe.toString());
        return Optional.of(recipe);
    }

    private Optional<String> findFirstValueByCssQuery(Document doc, CssQueryOfHtmlRecipeParts cssQuery) {
        Element simpleElement = doc.selectFirst(cssQuery.cssQuery());
        return Optional.of(Objects.requireNonNull(simpleElement).text());
    }

    private Map<String, String> fillParamsMap(Document doc) {
        var detailsMap = new HashMap<String, String>();
        Elements items = doc.select(RECIPE_DETAILS_ITEM.cssQuery());
        for (var item : items) {
            String label = item.select(RECIPE_DETAILS_LABEL.cssQuery()).text();
            String value = item.select(RECIPE_DETAILS_VALUE.cssQuery()).text();
            var processedLabel = label.substring(0, label.length() - 1).toLowerCase();
            detailsMap.put(processedLabel, value.trim());
        }
        return detailsMap;
    }

    private Duration parseTimeParam(String timeLabel, Map<String, String> detailsMap) {
        if (detailsMap.containsKey(timeLabel)) {
            return TimeParser.parseMinsFromString(detailsMap.get(timeLabel));
        }
        return Duration.ofMinutes(0);
    }
}
