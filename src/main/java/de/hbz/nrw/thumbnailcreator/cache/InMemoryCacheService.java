package de.hbz.nrw.thumbnailcreator.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

import jakarta.annotation.PreDestroy;

@Slf4j
@Component
public class InMemoryCacheService {

    private final Cache<String, byte[]> cache;

    public InMemoryCacheService() {
        this.cache = Caffeine.newBuilder()
                .maximumSize(1000) // max 1000 entries in cache
                .expireAfterAccess(10, TimeUnit.MINUTES) // entries expire after 10 minutes
                .build();
    }
    
    public String generateCacheKey(String url, int size) {
    	return Base64.getEncoder().encodeToString((url + "_" + size)
    			.getBytes(StandardCharsets.UTF_8))
    			.replaceAll("[^a-zA-Z0-9-_]", "_");
    }

    public void saveToCache(String key, byte[] data) {
        cache.put(key, data);
        log.info("Saved thumbnail to in-memory cache with key: {}", key);
    }

    public byte[] loadFromCache(String key) {
        return cache.getIfPresent(key);
    }

    public boolean isCached(String key) {
        return cache.getIfPresent(key) != null;
    }
    
    @PreDestroy
    public void clearCache() {
        cache.invalidateAll();
        log.info("Cache cleared");
    }
}

