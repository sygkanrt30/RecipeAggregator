package ru.practice.parser_service.service.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.instancio.Instancio;
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
import ru.practice.shared.dto.RecipeDto;
import ru.practice.shared.dto.ingredient.IngredientDto;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;

@Testcontainers
@SpringBootTest
class RecipeDtoCacheTest {

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
    private ObjectMapper objectMapper;

    @Autowired
    private RecipeDtoCache recipeDtoCache;

    @Autowired
    private CacheManager cacheManager;

    @BeforeEach
    void setUp() {
        Cache cache = cacheManager.getCache("recipes");
        if (cache != null) {
            cache.clear();
        }
    }

    @Test
    void shouldReturnFalseWhenKeyNotInCache() {
        String nonExistentKey = "non-existent-key";
        boolean exists = recipeDtoCache.contains(nonExistentKey);
        assertThat(exists).isFalse();
    }

    @Test
    void shouldReturnTrueWhenKeyInCache() {
        String key = "test-recipe-1";
        RecipeDto recipe = Instancio.create(RecipeDto.class);

        Cache cache = cacheManager.getCache("recipes");
        assertThat(cache).isNotNull();
        cache.put(key, recipe);

        boolean exists = recipeDtoCache.contains(key);
        assertThat(exists).isTrue();
    }

    @Test
    void shouldPutAllRecipesToCache() {
        Map<String, RecipeDto> recipes = new HashMap<>();
        recipes.put("recipe-1", createTestRecipe("Recipe 1"));
        recipes.put("recipe-2", createTestRecipe("Recipe 2"));
        recipes.put("recipe-3", createTestRecipe("Recipe 3"));

        recipeDtoCache.putAll(recipes);

        Cache cache = cacheManager.getCache("recipes");
        assertThat(cache).isNotNull();

        assertThat(recipeDtoCache.contains("recipe-1")).isTrue();
        assertThat(recipeDtoCache.contains("recipe-2")).isTrue();
        assertThat(recipeDtoCache.contains("recipe-3")).isTrue();

        Cache.ValueWrapper value1 = cache.get("recipe-1");
        Cache.ValueWrapper value2 = cache.get("recipe-2");
        Cache.ValueWrapper value3 = cache.get("recipe-3");

        assertThat(value1).isNotNull();
        assertThat(value2).isNotNull();
        assertThat(value3).isNotNull();

        var recipe1 = objectMapper.convertValue(value1.get(), RecipeDto.class);

        assertThat(recipe1.name()).isEqualTo("Recipe 1");
        assertThat(recipe1).isInstanceOf(RecipeDto.class);
    }

    @Test
    void shouldHandleEmptyMapInPutAll() {
        Map<String, RecipeDto> emptyMap = new HashMap<>();
        recipeDtoCache.putAll(emptyMap);
        assertThat(recipeDtoCache.contains("any-key")).isFalse();
    }

    @Test
    void shouldHandleNullMapInPutAll() {
        Map<String, RecipeDto> nullMap = null;
        recipeDtoCache.putAll(nullMap);
        assertThat(recipeDtoCache.contains("any-key")).isFalse();
    }

    @Test
    void shouldHandleSpecialCharactersInKeys() {
        String keyWithSpecialChars = "recipe#123?param=value&test=true";
        RecipeDto recipe = createTestRecipe("Special Recipe");

        Map<String, RecipeDto> recipes = Map.of(keyWithSpecialChars, recipe);
        recipeDtoCache.putAll(recipes);

        boolean exists = recipeDtoCache.contains(keyWithSpecialChars);
        assertThat(exists).isTrue();
    }

    @Test
    void shouldHandleEmptyKey() {
        String emptyKey = "";
        RecipeDto recipe = createTestRecipe("Test Recipe");

        Map<String, RecipeDto> recipes = Map.of(emptyKey, recipe);
        recipeDtoCache.putAll(recipes);

        boolean exists = recipeDtoCache.contains(emptyKey);
        assertThat(exists).isTrue();
    }

    @Test
    void shouldPutAndCheckMultipleEntries() {
        Map<String, RecipeDto> recipes = new HashMap<>();
        for (int i = 1; i <= 10; i++) {
            recipes.put("recipe-" + i, createTestRecipe("Recipe " + i));
        }

        recipeDtoCache.putAll(recipes);

        for (int i = 1; i <= 10; i++) {
            String key = "recipe-" + i;
            assertThat(recipeDtoCache.contains(key))
                    .as("Should contain key: " + key)
                    .isTrue();
        }
    }

    @Test
    void shouldCacheLargeNumberOfRecipes() {
        Map<String, RecipeDto> recipes = new HashMap<>();
        for (int i = 1; i <= 100; i++) {
            recipes.put("recipe-" + i, createTestRecipe("Recipe " + i));
        }

        recipeDtoCache.putAll(recipes);

        assertThat(recipeDtoCache.contains("recipe-1")).isTrue();
        assertThat(recipeDtoCache.contains("recipe-50")).isTrue();
        assertThat(recipeDtoCache.contains("recipe-100")).isTrue();
    }

    private RecipeDto createTestRecipe(String name) {
        return Instancio.of(RecipeDto.class)
                .set(field(RecipeDto::id), UUID.randomUUID())
                .set(field(RecipeDto::name), name)
                .generate(field(RecipeDto::timeForPreparing), gen ->
                        gen.temporal().duration().of(10, 30, ChronoUnit.MINUTES))
                .generate(field(RecipeDto::timeForCooking), gen ->
                        gen.temporal().duration().of(20, 60, ChronoUnit.MINUTES))
                .set(field(RecipeDto::totalTime), Duration.ofMinutes(45))
                .generate(field(RecipeDto::servings), gen -> gen.ints().range(1, 10))
                .supply(field(RecipeDto::ingredients), this::createTestIngredients)
                .set(field(RecipeDto::direction), "Test cooking directions for " + name)
                .set(field(RecipeDto::description), "Test description for " + name)
                .create();
    }

    private List<IngredientDto> createTestIngredients() {
        return List.of(
                IngredientDto.of("Flour 200g"),
                IngredientDto.of("Sugar 100g"),
                IngredientDto.of("Eggs 2 pcs"),
                IngredientDto.of("Milk 1 cup")
        );
    }
}