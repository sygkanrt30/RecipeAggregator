package ru.practice.parser_service.kafka;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.practice.shared.dto.RecipeDto;

import java.util.List;

@Component
public class KafkaRecipesProducerService extends AbstractKafkaProducerService<List<RecipeDto>> {

    public KafkaRecipesProducerService(
            KafkaTemplate<String, List<RecipeDto>> kafkaTemplate,
            @Value("${custom-kafka.topic}") String recipesTopic) {
        super(kafkaTemplate, recipesTopic);
    }

    @Override
    protected ItemType getItemType() {
        return ItemType.RECIPE;
    }
}
