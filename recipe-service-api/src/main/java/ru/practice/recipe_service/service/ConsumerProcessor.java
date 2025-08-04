package ru.practice.recipe_service.service;

import ru.practice.recipe_service.model.dto.kafka.request.RecipeKafkaDto;

import java.util.List;

public interface ConsumerProcessor {
    void saveFromKafka(List<RecipeKafkaDto> recipes);
}
