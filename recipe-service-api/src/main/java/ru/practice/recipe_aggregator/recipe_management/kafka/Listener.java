package ru.practice.recipe_aggregator.recipe_management.kafka;

public interface Listener<T> {

    @SuppressWarnings("unused")
    void listen(T recipes);
}
