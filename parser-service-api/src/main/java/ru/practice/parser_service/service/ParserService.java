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
    @Value("${parser.website-with-recipe.url.main-page}")
    private String rootUrl;
    private final ProducerService producer;
    private final WebsiteParser parser;

    @Scheduled(fixedDelay = 2, timeUnit = TimeUnit.DAYS, initialDelay = 0)
    public void parceRecipesAndSend2Kafka() {
        List<Recipe> recipes = parser.parse(rootUrl);
        producer.sendMessage(recipes);
    }
}
