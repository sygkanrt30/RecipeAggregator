package ru.practice.parser_service.service.parsers.recipe.jsonld;

import org.jsoup.nodes.Document;
import ru.practice.shared.dto.RecipeDto;

import java.util.Optional;


public interface RecipeParser {

    Optional<RecipeDto> parseRecipePage(Document doc);
}
