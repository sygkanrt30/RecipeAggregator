package ru.practice.recipe_aggregator.recipe_management.recipe_service;

import ru.practice.recipe_aggregator.recipe_management.model.dto.kafka.RecipeKafkaDto;

import java.util.List;

public interface ConsumerProcessor {
    void saveFromKafka(List<RecipeKafkaDto> recipes);
}
