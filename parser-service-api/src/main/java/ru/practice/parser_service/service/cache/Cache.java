package ru.practice.parser_service.service.cache;

import java.util.Collection;

public interface Cache<K, V> {
    void put(K key, V value);

    void putAll(K key, Collection<V> values);

    boolean contains(K key, V value);
}
