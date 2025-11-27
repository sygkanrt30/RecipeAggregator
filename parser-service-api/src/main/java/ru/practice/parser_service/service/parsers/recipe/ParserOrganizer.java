package ru.practice.parser_service.service.parsers.recipe;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.practice.parser_service.service.exception.ParserException;
import ru.practice.shared.dto.RecipeDto;

import java.util.Deque;
import java.util.LinkedList;
import java.util.Optional;

@Slf4j
@Component
public class ParserOrganizer {

    private final Deque<RecipeParser> parsers;

    @Autowired
    public ParserOrganizer(@Qualifier("jsonLdParser") RecipeParser jsonldParser,
                           @Qualifier("htmlParser") RecipeParser htmlParser) {
        parsers = new LinkedList<>();
        parsers.addFirst(jsonldParser);
        parsers.addLast(htmlParser);
    }

    public RecipeDto parseByPriority(Document doc) {
        log.debug("Start parsing recipe from document {}", doc.baseUri());
        for (var parser : parsers) {
            Optional<RecipeDto> recipe = parser.parseRecipePage(doc);
            if (recipe.isPresent()) {
                log.debug("Successfully parsed recipe from parser class: {}", parser.getClass().getSimpleName());
                return recipe.get();
            }
        }
        throw new ParserException("Could not parse recipe from document " + doc.baseUri());
    }

    public void addParsersToQueue(RecipeParser parser1, RecipeParser... parsers) {
        addParserToQueue(parser1);
        for (var parser : parsers) {
            addParserToQueue(parser);
        }
    }

    public void addParserToQueue(RecipeParser parser) {
        if (isValidParser(parser)) {
            parsers.addLast(parser);
        }

    }

    private boolean isValidParser(RecipeParser parser) {
        return !parsers.contains(parser) && parser != null;
    }

    public void addAndMakeParserTopPriority(RecipeParser parser) {
        if (isValidParser(parser)) {
            parsers.addFirst(parser);
        }
    }
}