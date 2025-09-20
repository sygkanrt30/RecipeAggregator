package ru.practice.parser_service.service.parsers.recipe.recipes_parts;

import lombok.experimental.UtilityClass;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.practice.parser_service.service.parsers.enums.CssQueryOfRecipesParts;

import java.util.stream.Collectors;

@UtilityClass
public class DirectionParser {
    public String parse(Document doc) {
        Elements steps = doc.select(CssQueryOfRecipesParts.DIRECTIONS.cssQuery());
        return steps.stream()
                .map(Element::text)
                .map(String::trim)
                .collect(Collectors.joining("\n"));
    }
}
