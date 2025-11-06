package ru.practice.parser_service.kafka;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.Collection;
import java.util.stream.IntStream;

@Slf4j
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractKafkaProducerService<T> implements ProducerService<T> {
    private final KafkaTemplate<String, T> kafkaTemplate;
    private final String kafkaTopic;

    @Override
    public void sendMessage(T data) {
        kafkaTemplate.send(kafkaTopic, data);
        log.info("Message is sent to topic '{}'", kafkaTopic);
        logItemIfDebugLevel(data);
    }

    private void logItemIfDebugLevel(T data) {
        if (log.isDebugEnabled()) {
            if (data instanceof Collection<?> collection) {
                logEveryItemIfDebugLevel(collection);
                return;
            }
            log.debug("{} sent: {}", getItemType(), data);
        }
    }

    private void logEveryItemIfDebugLevel(Collection<?> collection) {
        var iterator = collection.iterator();
        IntStream.range(0, collection.size())
                .forEach(i -> log.debug("{} #{}: {}", getItemType(), i, iterator.next()));
    }

    protected abstract String getItemType();
}
