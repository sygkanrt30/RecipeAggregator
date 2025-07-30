package ru.practice.parser_service.kafka.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;
import ru.practice.parser_service.model.Recipe;

import java.util.HashMap;
import java.util.List;

@EnableKafka
@Configuration
public class KafkaConfig {
    @Value("${custom-kafka.topic}")
    private String topic;
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Bean
    public NewTopic topic() {
        return new NewTopic(topic, 1, (short) 1);
    }

    @Bean
    public ProducerFactory<String, List<Recipe>> producerFactory() {
        var configProps = new HashMap<String, Object>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ProducerConfig.ACKS_CONFIG, "all");
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        var objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        return new DefaultKafkaProducerFactory<>(
                configProps,
                new StringSerializer(),
                new JsonSerializer<>(objectMapper)
        );
    }

    @Bean
    public KafkaTemplate<String, List<Recipe>> kafkaTemplate(ProducerFactory<String, List<Recipe>> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }
}
