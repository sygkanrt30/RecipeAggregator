package ru.practice.parser_service.service.cache;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;
import ru.practice.shared.dto.RecipeDto;

import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
class RecipeDtoCache implements RecipeCache<String, RecipeDto> {

    private static final String RECIPES_CACHE = "recipes";

    private final CacheManager cacheManager;

    public boolean contains(String key) {
        try {
            Cache cache = cacheManager.getCache(RECIPES_CACHE);
            return cache != null && cache.get(key) != null;
        } catch (Exception e) {
            log.error("Error checking for key {} in recipes cache", key, e);
            return false;
        }
    }

    public void putAll(Map<String, RecipeDto> entities) {
        if (entities == null || entities.isEmpty()) {
            return;
        }

        try {
            Cache cache = cacheManager.getCache(RECIPES_CACHE);
            if (cache != null) {
                entities.forEach(cache::put);
            }
        } catch (Exception e) {
            log.error("Error when batch saving to recipes cache", e);
        }
    }
}