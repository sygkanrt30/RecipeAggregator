package ru.practice.recipe_aggregator.recipe_management.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.practice.recipe_aggregator.recipe_management.model.dto.kafka.RecipeKafkaDto;
import ru.practice.recipe_aggregator.recipe_management.recipe_service.ConsumerProcessor;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaConsumer implements Listener {
    private final ConsumerProcessor processor;

    @Override
    @KafkaListener(topics = "${custom.kafka.topic}", groupId = "${custom.kafka.group-id}")
    public void listen(List<RecipeKafkaDto> recipes) {
        log.info("Listening recipes: {}", recipes.size());
        logEveryRecipeIfDebugLevel(recipes);
        processor.saveFromKafka(recipes);
    }

    private void logEveryRecipeIfDebugLevel(List<RecipeKafkaDto> recipes) {
        if (log.isDebugEnabled()) {
            for (int i = 0; i < recipes.size(); i++) {
                var recipe = recipes.get(i);
                log.debug("Recipe #{}: {}", i, recipe.toString());
            }
        }
    }
}
