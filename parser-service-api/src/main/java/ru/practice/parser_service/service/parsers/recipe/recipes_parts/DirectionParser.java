package ru.practice.parser_service.service.parsers.recipe.recipes_parts;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.practice.parser_service.service.parsers.enums.CssQueryOfRecipesParts;

import java.util.stream.Collectors;

@UtilityClass
@Slf4j
public class DirectionParser {
    public String parse(Document doc) {
        log.debug("parse(doc): Start parsing direction from document {}", doc.toString());
        Elements steps = doc.select(CssQueryOfRecipesParts.DIRECTIONS.cssQuery());
        return steps.stream()
                .map(Element::text)
                .map(String::trim)
                .collect(Collectors.joining("\n"));
    }
}
