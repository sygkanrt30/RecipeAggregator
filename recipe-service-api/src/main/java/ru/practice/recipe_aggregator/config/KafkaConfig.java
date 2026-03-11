package ru.practice.recipe_aggregator.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.util.backoff.FixedBackOff;
import ru.practice.shared.dto.RecipeDto;

import java.util.HashMap;
import java.util.List;

@Configuration
@EnableKafka
public class KafkaConfig {

    @Value("${custom.kafka.offset-reset}")
    private String autoOffsetReset;
    @Value("${custom.kafka.auto-commit}")
    private String autoCommitConfig;
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;
    @Value("${custom.kafka.trusted.package}")
    private String trustedPackage;
    @Value("${custom.kafka.group-id}")
    private String groupId;

    @Bean
    public ConsumerFactory<String, List<RecipeDto>> consumerFactory() {
        var configProps = new HashMap<String, Object>();
        configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        configProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, autoOffsetReset);
        configProps.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, autoCommitConfig);
        configProps.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class);

        var deserializer = new JsonDeserializer<List<RecipeDto>>(
                new TypeReference<>() {},
                objectMapper()
        );
        deserializer.addTrustedPackages(trustedPackage);
        deserializer.setUseTypeHeaders(false);

        return new DefaultKafkaConsumerFactory<>(
                configProps,
                new StringDeserializer(),
                new ErrorHandlingDeserializer<>(deserializer)
        );
    }

    @Bean
    public ObjectMapper objectMapper() {
        var objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, List<RecipeDto>> kafkaListenerContainerFactory() {
        var factory = new ConcurrentKafkaListenerContainerFactory<String, List<RecipeDto>>();
        factory.setConsumerFactory(consumerFactory());

        var errorHandler = new DefaultErrorHandler(
                new FixedBackOff(1000L, 2L)
        );
        errorHandler.addNotRetryableExceptions(SerializationException.class);
        factory.setCommonErrorHandler(errorHandler);

        return factory;
    }


}