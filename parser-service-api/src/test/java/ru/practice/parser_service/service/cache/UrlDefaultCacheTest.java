package ru.practice.parser_service.service.cache;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest
class UrlDefaultCacheTest {

    @SuppressWarnings("resource")
    @Container
    static GenericContainer<?> redis = new GenericContainer<>(DockerImageName.parse("redis:7-alpine"))
            .withExposedPorts(6379);

    @DynamicPropertySource
    static void redisProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.cache.type", () -> "redis");
        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", () -> redis.getMappedPort(6379));
        registry.add("spring.cache.redis.time-to-live", () -> "60000");
    }

    @Autowired
    private UrlDefaultCache urlCache;

    @Autowired
    private CacheManager cacheManager;

    @BeforeEach
    void setUp() {
        cacheManager.getCacheNames()
                .forEach(cacheName -> {
                    Cache cache = cacheManager.getCache(cacheName);
                    if (cache != null) {
                        cache.clear();
                    }
                });
    }

    @Test
    void shouldPutUrlToCache() {
        // Given
        NameOfUrlCaches cacheKey = NameOfUrlCaches.VISITED_URLS;
        String testUrl = "https://example.com/test";

        // When
        urlCache.put(cacheKey, testUrl);

        // Then
        boolean exists = urlCache.contains(cacheKey, testUrl);
        assertThat(exists).isTrue();
    }

    @Test
    void shouldReturnFalseWhenUrlNotInCache() {
        // Given
        NameOfUrlCaches cacheKey = NameOfUrlCaches.VISITED_URLS;
        String testUrl = "https://example.com/not-cached";

        // When
        boolean exists = urlCache.contains(cacheKey, testUrl);

        // Then
        assertThat(exists).isFalse();
    }

    @Test
    void shouldHandleMultipleCachesIndependently() {
        // Given
        NameOfUrlCaches cache1 = NameOfUrlCaches.VISITED_URLS;
        NameOfUrlCaches cache2 = NameOfUrlCaches.PARSED_RECIPE_URLS;
        String url1 = "https://example.com/url1";
        String url2 = "https://example.com/url2";

        // When
        urlCache.put(cache1, url1);
        urlCache.put(cache2, url2);

        // Then
        assertThat(urlCache.contains(cache1, url1)).isTrue();
        assertThat(urlCache.contains(cache1, url2)).isFalse();
        assertThat(urlCache.contains(cache2, url2)).isTrue();
        assertThat(urlCache.contains(cache2, url1)).isFalse();
    }

    @Test
    void shouldOverwriteExistingUrl() {
        // Given
        NameOfUrlCaches cacheKey = NameOfUrlCaches.VISITED_URLS;
        String testUrl = "https://example.com/test";

        // When
        urlCache.put(cacheKey, testUrl);
        urlCache.put(cacheKey, testUrl);

        // Then
        boolean exists = urlCache.contains(cacheKey, testUrl);
        assertThat(exists).isTrue();
    }

    @Test
    void shouldCacheUrlWithSpecialCharacters() {
        // Given
        NameOfUrlCaches cacheKey = NameOfUrlCaches.VISITED_URLS;
        String urlWithSpecialChars = "https://example.com/test?param=value&another=test#section";

        // When
        urlCache.put(cacheKey, urlWithSpecialChars);

        // Then
        boolean exists = urlCache.contains(cacheKey, urlWithSpecialChars);
        assertThat(exists).isTrue();
    }
}