package ru.practice.parser_service.service.cache;

import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Slf4j
public class UrlCache implements Cache<String> {

    private final Set<String> cachedUrlsStorage = new HashSet<>();

    @Override
    public void put(String url) {
        boolean isAlreadyContains = !cachedUrlsStorage.add(url);
        if (isAlreadyContains){
            log.warn("Url({}) already contains in storage", url);
        }
    }

    @Override
    public void putAll(Collection<String> urls) {
        boolean isAddNewRecipes = cachedUrlsStorage.addAll(urls);
        if (!isAddNewRecipes){
            log.warn("Something urls already been in the storage");
        }
    }

    @Override
    public boolean contains(String url) {
        if (!cachedUrlsStorage.contains(url)) {
            put(url);
            return false;
        }
        return true;
    }
}
