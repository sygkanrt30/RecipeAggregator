package ru.practice.parser_service.kafka;

public interface ProducerService<T> {
    void sendMessage(T data);
}
