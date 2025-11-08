package ru.practice.recipe_aggregator.recipe_management.kafka;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.practice.recipe_aggregator.recipe_management.processor.ConsumerProcessor;
import ru.practice.shared.dto.RecipeDto;

import java.util.List;


@Component
public class KafkaConsumer extends AbstractKafkaConsumerService<List<RecipeDto>> {
    public KafkaConsumer(ConsumerProcessor<List<RecipeDto>> processor,
                         @Value("${custom.kafka.topic}") String topic) {
        super(processor, topic);
    }

    @Override
    protected String getItemType() {
        return "Recipe";
    }
}
