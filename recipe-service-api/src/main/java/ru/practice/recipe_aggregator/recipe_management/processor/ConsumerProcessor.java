package ru.practice.recipe_aggregator.recipe_management.processor;

public interface ConsumerProcessor<T> {

    void saveFromKafka(T data);
}
