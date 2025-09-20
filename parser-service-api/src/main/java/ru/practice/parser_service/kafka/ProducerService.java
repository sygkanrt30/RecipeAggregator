package ru.practice.parser_service.kafka;

import ru.practice.parser_service.model.Recipe;

import java.util.List;

public interface ProducerService {
    void sendMessage(List<Recipe> recipes);
}
