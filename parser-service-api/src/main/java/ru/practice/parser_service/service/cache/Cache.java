package ru.practice.parser_service.service.cache;

import java.util.Collection;

public interface Cache<V> {
    void put(V value);

    void putAll(Collection<V> values);

    boolean contains(V value);


}
