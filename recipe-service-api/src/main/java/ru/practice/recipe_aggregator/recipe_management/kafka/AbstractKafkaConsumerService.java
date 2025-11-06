package ru.practice.recipe_aggregator.recipe_management.kafka;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import ru.practice.recipe_aggregator.recipe_management.recipe_service.ConsumerProcessor;

import java.util.Collection;
import java.util.stream.IntStream;

@Slf4j
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractKafkaConsumerService<T> implements Listener<T> {
    private final ConsumerProcessor<T> processor;
    @Getter
    private final String topic;

    @Override
    @KafkaListener(topics = "#{__listener.topic}", groupId = "${custom.kafka.group-id}")
    public void listen(T recipes) {
        log.info("Listening recipes from topic: {}", topic);
        logItemIfDebugLevel(recipes);
        processor.saveFromKafka(recipes);
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

