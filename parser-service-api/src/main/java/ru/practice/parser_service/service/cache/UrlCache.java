//package ru.practice.parser_service.service.cache;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.data.redis.core.SetOperations;
//import org.springframework.stereotype.Service;
//import ru.practice.parser_service.config.NameOfCaches;
//
//import java.util.Collection;
//
//@Slf4j
//@Service
//public class UrlCache implements Cache<NameOfCaches, String> {
//
//    private final SetOperations<String, String> setOperations;
//
//    public UrlCache(RedisTemplate<String, String> redisTemplate) {
//        this.setOperations = redisTemplate.opsForSet();
//    }
//
//    @Override
//    public void put(NameOfCaches key, String url) {
//        setOperations.add(key.name(), url);
//
//    }
//
//    @Override
//    public void putAll(NameOfCaches key, Collection<String> urls) {
//        if (!urls.isEmpty()) {
//            setOperations.add(key.name(), urls.toArray(new String[0]));
//            return;
//        }
//        log.debug("Collection of url is empty");
//    }
//
//    @Override
//    public boolean contains(NameOfCaches key, String url) {
//        if (!Boolean.TRUE.equals(setOperations.isMember(key.name(), url))) {
//            put(key, url);
//            return false;
//        }
//        return true;
//    }
//}
