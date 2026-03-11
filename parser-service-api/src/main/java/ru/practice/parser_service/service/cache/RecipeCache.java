package ru.practice.parser_service.service.cache;

import java.util.Map;

public interface RecipeCache<K, V> {

    boolean contains(K key);

    void putAll(Map<K, V> entities);
}
