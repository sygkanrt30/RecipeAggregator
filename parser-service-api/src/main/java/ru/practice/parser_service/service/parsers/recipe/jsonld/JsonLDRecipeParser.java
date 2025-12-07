package ru.practice.parser_service.service.parsers.recipe.jsonld;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import ru.practice.parser_service.service.parsers.recipe.RecipeParser;
import ru.practice.shared.dto.RecipeDto;

import java.util.Optional;

import static ru.practice.parser_service.service.parsers.recipe.jsonld.FieldName.TYPE;

@RequiredArgsConstructor
@Slf4j
@Component("jsonLdParser")
public class JsonLDRecipeParser implements RecipeParser {

    private final static String JSON_LD_SCRIPT = "script[type=application/ld+json]";
    private static final String RECIPE = "Recipe";

    private final RecipeExtractor extractor;
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
                                    return extractor.extractRecipeFromJson(node);
                                }
                            }
                        } else if (isRecipeNode(json)) {
                            log.debug("Found Recipe object in JSON-LD");
                            return extractor.extractRecipeFromJson(json);
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
}