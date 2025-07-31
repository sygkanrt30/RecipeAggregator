package ru.practice.recipe_service.kafka;

import ru.practice.recipe_service.model.dto.request.RecipeKafkaDto;

import java.util.List;

public interface Listener {
    @SuppressWarnings("unused")
    void listen(List<RecipeKafkaDto> recipes);
}
