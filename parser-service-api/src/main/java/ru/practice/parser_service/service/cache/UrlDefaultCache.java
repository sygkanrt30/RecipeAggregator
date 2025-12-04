package ru.practice.parser_service.service.cache;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;


@Slf4j
@Component
@RequiredArgsConstructor
class UrlDefaultCache implements UrlCache<NameOfUrlCaches, String> {

    private final CacheManager cacheManager;

    @Override
    public void put(NameOfUrlCaches key, String url) {
        try {
            Cache cache = getCache(key);
            if (cache != null) {
                cache.put(url, Boolean.TRUE.toString());
            }
        } catch (Exception e) {
            log.error("Failed to add URL to cache: {}", url, e);
        }
    }

    @Override
    public boolean contains(NameOfUrlCaches key, String url) {
        Cache cache = getCache(key);

        if (cache != null) {
            Cache.ValueWrapper valueWrapper = cache.get(url);
            return valueWrapper != null;
        }
        return false;
    }

    private Cache getCache(NameOfUrlCaches key) {
        return cacheManager.getCache(key.value());
    }
}
