package ru.practice.parser_service.service.cache;

public interface UrlCache<K, V> {
    void put(K key, V value);

    boolean contains(K key, V value);
}
