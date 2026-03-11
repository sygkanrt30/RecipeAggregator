package ru.practice.parser_service.kafka;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.Collection;

@Slf4j
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractKafkaProducerService<T> implements ProducerService<T> {

    private final KafkaTemplate<String, T> kafkaTemplate;
    private final String kafkaTopic;

    @Override
    public void sendMessage(T data) {
        kafkaTemplate.send(kafkaTopic, data);
        log.debug("Message is sent to topic '{}'", kafkaTopic);
        logItemIfDebugLevel(data);
    }

    private void logItemIfDebugLevel(T data) {
        if (log.isTraceEnabled()) {
            if (data instanceof Collection<?> collection) {
                logEveryItemIfDebugLevel(collection);
                return;
            }
            log.trace("{} sent: {}", getItemType().name(), data);
        }
    }

    private void logEveryItemIfDebugLevel(Collection<?> collection) {
        final int[] i = {0};
        collection.forEach(item -> log.trace("{} #{}: {}", getItemType().name(), ++i[0], item));
    }

    protected abstract ItemType getItemType();
}
