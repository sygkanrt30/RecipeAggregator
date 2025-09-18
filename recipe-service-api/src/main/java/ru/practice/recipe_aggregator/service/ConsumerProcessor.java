package ru.practice.recipe_aggregator.service;

import ru.practice.recipe_aggregator.model.dto.kafka.RecipeKafkaDto;

import java.util.List;

public interface ConsumerProcessor {
    void saveFromKafka(List<RecipeKafkaDto> recipes);
}
