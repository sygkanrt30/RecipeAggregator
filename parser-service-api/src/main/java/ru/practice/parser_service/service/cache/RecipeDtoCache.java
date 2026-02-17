package ru.practice.parser_service.service.cache;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;
import ru.practice.shared.dto.RecipeDto;

import java.util.Map;

@Component
@Slf4j
class RecipeDtoCache implements RecipeCache<String, RecipeDto> {

    private static final String RECIPES_CACHE = "recipes";

    private final Cache cache;

    RecipeDtoCache(CacheManager cacheManager) {
        this.cache = cacheManager.getCache(RECIPES_CACHE);
    }

    public boolean contains(String key) {
        return cache != null && cache.get(key) != null;
    }

    public void putAll(Map<String, RecipeDto> entities) {
        if (entities == null || entities.isEmpty()) {
            return;
        }
        entities.forEach(cache::put);
    }
}