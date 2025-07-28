package ru.practice.parser_service.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.practice.parser_service.kafka.KafkaProducerService;
import ru.practice.parser_service.kafka.ProducerService;
import ru.practice.parser_service.model.Recipe;
import ru.practice.parser_service.service.parsers.website.WebsiteParserImpl;
import ru.practice.parser_service.service.parsers.website.WebsiteParser;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@EnableScheduling
public class ParserService {
    private final ProducerService producerService;
    private final WebsiteParser parser;
    private final String rootUrl;

    public ParserService(
            KafkaProducerService producerService,
            WebsiteParserImpl parser,
            @Value("${parser.website-with-recipe.url.main-page}") String rootUrl) {
        this.producerService = producerService;
        this.parser = parser;
        this.rootUrl = rootUrl;
    }

    @Scheduled(fixedRate = 1, timeUnit = TimeUnit.DAYS)
    public void parceRecipesAndSend2Kafka() {
        List<Recipe> recipes = parser.parseWebsite(rootUrl);
        producerService.sendMessage(recipes);
    }
}
