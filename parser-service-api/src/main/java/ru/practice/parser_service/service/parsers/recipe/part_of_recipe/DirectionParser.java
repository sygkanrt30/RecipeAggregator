package ru.practice.parser_service.service.parsers.recipe.part_of_recipe;

import lombok.experimental.UtilityClass;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.practice.parser_service.service.parsers.enums.CssQueryOfPartsOfRecipes;

import java.util.stream.Collectors;

@UtilityClass
public class DirectionParser {
    public String parseDirections(Document doc) {
        Elements steps = doc.select(CssQueryOfPartsOfRecipes.DIRECTIONS.cssQuery());
        return steps.stream()
                .map(Element::text)
                .map(String::trim)
                .collect(Collectors.joining("\n"));
    }
}
