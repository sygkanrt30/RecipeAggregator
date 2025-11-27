package ru.practice.parser_service.service.cache;

//@Slf4j
//@Component
//public class RecipeCache implements Cache<UUID, RecipeDto> {
//
//    private final ValueOperations<String, RecipeDto> valueOperations;
//    private final RedisTemplate<String, RecipeDto> redisTemplate;
//    private static final String RECIPE_CACHE_PREFIX = "recipe:";
//
//    public RecipeCache(RedisTemplate<String, RecipeDto> redisTemplate) {
//        this.redisTemplate = redisTemplate;
//        this.valueOperations = redisTemplate.opsForValue();
//    }
//
//    @Override
//    public void put(UUID key, RecipeDto recipe) {
//        valueOperations.set(buildKey(key), recipe);
//    }
//
//    @Override
//    public void putAll(Map<UUID, RecipeDto> entries) {
//        if (!entries.isEmpty()) {
//            Map<String, RecipeDto> redisEntries = new HashMap<>();
//            for (Map.Entry<UUID, RecipeDto> entry : entries.entrySet()) {
//                redisEntries.put(buildKey(entry.getKey()), entry.getValue());
//            }
//            valueOperations.multiSet(redisEntries);
//            return;
//        }
//        log.debug("Map of recipes is empty");
//    }
//
//    @Override
//    public boolean contains(UUID key, RecipeDto recipe) {
//        RecipeDto cachedRecipe = valueOperations.get(buildKey(key));
//        if (cachedRecipe == null) {
//            put(key, recipe);
//            return false;
//        }
//        return true;
//    }
//
//    // Дополнительные методы, специфичные для RecipeDto
//    public RecipeDto get(UUID key) {
//        return valueOperations.get(buildKey(key));
//    }
//
//    public void evict(UUID key) {
//        redisTemplate.delete(buildKey(key));
//    }
//
//    public void evictAll() {
//        Set<String> keys = redisTemplate.keys(RECIPE_CACHE_PREFIX + "*");
//        if (keys != null && !keys.isEmpty()) {
//            redisTemplate.delete(keys);
//        }
//    }
//
//    public boolean exists(UUID key) {
//        return Boolean.TRUE.equals(redisTemplate.hasKey(buildKey(key)));
//    }
//
//    private String buildKey(UUID key) {
//        return RECIPE_CACHE_PREFIX + key.toString();
//    }
//}
