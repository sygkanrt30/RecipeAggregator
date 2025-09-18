package ru.practice.recipe_aggregator.kafka;

import ru.practice.recipe_aggregator.model.dto.kafka.RecipeKafkaDto;

import java.util.List;

public interface Listener {
    @SuppressWarnings("unused")
    void listen(List<RecipeKafkaDto> recipes);
}
