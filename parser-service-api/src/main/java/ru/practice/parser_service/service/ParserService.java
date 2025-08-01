package ru.practice.parser_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.practice.parser_service.kafka.ProducerService;
import ru.practice.parser_service.model.Recipe;
import ru.practice.parser_service.service.parsers.website.WebsiteParser;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@EnableScheduling
@RequiredArgsConstructor
public class ParserService {
    private final ProducerService producerService;
    private final WebsiteParser parser;
    @Value("${parser.website-with-recipe.url.main-page}")
    private String rootUrl;

    @Scheduled(fixedRate = 2, timeUnit = TimeUnit.DAYS)
    public void parceRecipesAndSend2Kafka() {
        List<Recipe> recipes = parser.parseWebsite(rootUrl);
        producerService.sendMessage(recipes);
    }
}
