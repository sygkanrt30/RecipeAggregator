package ru.practice.recipe_service.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.practice.recipe_service.model.dto.request.RecipeKafkaDto;
import ru.practice.recipe_service.service.ConsumerProcessor;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaConsumer implements Listener {
    private final ConsumerProcessor processor;

    @Override
    @KafkaListener(topics = "${custom.kafka.topic}", groupId = "${custom.kafka.group-id}")
    public void listen(List<RecipeKafkaDto> recipes) {
        processor.saveFromKafka(recipes);
        log.info("Listening recipes: {}", recipes.size());
    }
}
