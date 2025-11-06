package ru.practice.recipe_aggregator.recipe_management.recipe_service;

public interface ConsumerProcessor<T> {
    void saveFromKafka(T data);
}
