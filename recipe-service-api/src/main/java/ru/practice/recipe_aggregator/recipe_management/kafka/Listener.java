package ru.practice.recipe_aggregator.recipe_management.kafka;

import ru.practice.recipe_aggregator.recipe_management.model.dto.kafka.RecipeKafkaDto;

import java.util.List;

public interface Listener {
    @SuppressWarnings("unused")
    void listen(List<RecipeKafkaDto> recipes);
}
