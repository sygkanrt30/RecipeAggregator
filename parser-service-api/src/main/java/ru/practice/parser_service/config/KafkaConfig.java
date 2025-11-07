package ru.practice.parser_service.config;

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
import ru.practice.shared.dto.RecipeDto;

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
    public ProducerFactory<String, List<RecipeDto>> producerFactory() {
        var configProps = new HashMap<String, Object>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ProducerConfig.ACKS_CONFIG, "all");
        configProps.put(ProducerConfig.RETRIES_CONFIG, 1);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        configProps.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, false);

        return new DefaultKafkaProducerFactory<>(
                configProps,
                new StringSerializer(),
                new JsonSerializer<>(objectMapper())
        );
    }

    @Bean
    public ObjectMapper objectMapper() {
        var objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper;
    }

    @Bean
    public KafkaTemplate<String, List<RecipeDto>> kafkaTemplate(ProducerFactory<String, List<RecipeDto>> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

    @Bean("mainTopic")
    public NewTopic topic() {
        return new NewTopic(topic, 1, (short) 1);
    }
}
