package ru.practice.parser_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.practice.parser_service.kafka.ProducerService;
import ru.practice.parser_service.config.ParserConfig;
import ru.practice.parser_service.service.parsers.website.WebsiteParser;
import ru.practice.shared.dto.RecipeDto;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class ParserService {
    private final ParserConfig parserConfig;
    private final ProducerService<List<RecipeDto>> producer;
    private final WebsiteParser parser;

    @Scheduled(fixedDelay = 30, timeUnit = TimeUnit.MINUTES, initialDelay = 0)
    public void parseRecipesAndSend2Kafka() {
        var rootUrl = parserConfig.mainUrl();
        log.debug("The beginning of the planned parsing of all recipes");
        try {
            log.debug("Parsing with root url: {}", rootUrl);
            List<RecipeDto> recipes = parser.parse(rootUrl);
            if (!recipes.isEmpty()) {
                producer.sendMessage(recipes);
                log.debug("The scheduled parsing is completed. Total recipes sent: {}", recipes.size());
            }
        } catch (Exception e) {
            log.error("Parsing error url {}: {}", rootUrl, e.getMessage());
        }
    }
}
