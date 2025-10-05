package ru.practice.parser_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class ParserService {
    @Value("${parser.website-with-recipe.url.main-page}")
    private String rootUrl;
    private final ProducerService producer;
    private final WebsiteParser parser;

    @Scheduled(fixedDelay = 30, timeUnit = TimeUnit.MINUTES, initialDelay = 0)
    public void parceRecipesAndSend2Kafka() {
        log.info("Начало планового парсинга всех рецептов");
        try {
            log.info("Парсинг с корневого URL: {}", rootUrl);
            List<Recipe> recipes = parser.parse(rootUrl);
            if (!recipes.isEmpty()) {
                producer.sendMessage(recipes);
                log.info("Плановый парсинг завершен. Всего отправлено рецептов: {}", recipes.size());
            }
        } catch (Exception e) {
            log.error("Ошибка при парсинге URL {}: {}", rootUrl, e.getMessage());
        }
    }

    @Scheduled(initialDelay = 60, timeUnit = TimeUnit.DAYS)
    public void resetParcerContext(){
        parser.reset();
        log.info("Контекст парсера сброшен");
    }
}
