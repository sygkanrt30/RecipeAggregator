package ru.practice.parser_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.practice.parser_service.config.RecipeParserConfig;
import ru.practice.parser_service.kafka.ProducerService;
import ru.practice.parser_service.service.parsers.website.WebsiteParser;
import ru.practice.shared.dto.RecipeDto;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public final class RecipeParserService extends AbstractParserService<List<RecipeDto>> {
    private final RecipeParserConfig parserConfig;
    private final ProducerService<List<RecipeDto>> producer;
    private final WebsiteParser parser;

    @Override
    protected List<RecipeDto> parseData(String source) {
        return parser.parse(source);
    }

    @Override
    protected void sendData(List<RecipeDto> data) {
        producer.sendMessage(data);
    }

    @Override
    protected String getDataSource() {
        return parserConfig.mainUrl();
    }

    @Scheduled(fixedDelay = 30, timeUnit = TimeUnit.MINUTES, initialDelay = 0)
    public void parseRecipesAndSend2Kafka() {
        parseAndSend();
    }
}
