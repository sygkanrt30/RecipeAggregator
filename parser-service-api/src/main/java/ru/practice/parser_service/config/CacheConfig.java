package ru.practice.parser_service.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.Map;

import static ru.practice.parser_service.service.cache.NameOfUrlCaches.PARSED_RECIPE_URLS;
import static ru.practice.parser_service.service.cache.NameOfUrlCaches.VISITED_URLS;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory,
                                     ObjectMapper objectMapper,
                                     @Value("${spring.data.redis.ttl.recipe}") long recipeTtl,
                                     @Value("${spring.data.redis.ttl.urls}") long urlsTtl,
                                     @Value("${recipe.cache.name}") String recipeCacheName) {

        var serializer = new GenericJackson2JsonRedisSerializer(objectMapper);

        var config = RedisCacheConfiguration.defaultCacheConfig()
                .serializeKeysWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(serializer))
                .disableCachingNullValues()
                .entryTtl(Duration.ofHours(1));

        Map<String, RedisCacheConfiguration> cacheConfigurations = Map.of(
                VISITED_URLS.value(),
                config.entryTtl(Duration.ofDays(urlsTtl)),

                PARSED_RECIPE_URLS.value(),
                config.entryTtl(Duration.ofDays(urlsTtl)),

                recipeCacheName,
                config.entryTtl(Duration.ofDays(recipeTtl))
        );

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(config)
                .withInitialCacheConfigurations(cacheConfigurations)
                .transactionAware()
                .build();
    }
}