package ru.practice.parser_service.kafka;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.practice.parser_service.model.Recipe;

import java.util.List;

@Service
@Slf4j
public class KafkaProducerService implements ProducerService {
    private final KafkaTemplate<String, List<Recipe>> kafkaTemplate;
    private final String kafkaTopic;

    public KafkaProducerService(KafkaTemplate<String, List<Recipe>> kafkaTemplate,
                                @Value("${custom-kafka.topic}") String kafkaTopic) {
        this.kafkaTemplate = kafkaTemplate;
        this.kafkaTopic = kafkaTopic;
    }

    @Override
    public void sendMessage(List<Recipe> recipes) {
        kafkaTemplate.send(kafkaTopic, recipes);
        log.info("Сообщение отправлено: {}", recipes);
    }
}
