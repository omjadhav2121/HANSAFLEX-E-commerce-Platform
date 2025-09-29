package com.hansaflex.ecommerce.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class CacheService {

    private final CacheManager cacheManager;

    /**
     * Clear all product-related caches
     */
    public void clearProductCaches() {
        log.info("Manually clearing all product-related caches");
        
        String[] cacheNames = {"products", "productDetails", "productPrice", "pricingConfig"};
        
        for (String cacheName : cacheNames) {
            if (cacheManager.getCache(cacheName) != null) {
                Objects.requireNonNull(cacheManager.getCache(cacheName)).clear();
                log.info("Cleared cache: {}", cacheName);
            }
        }
    }

    /**
     * Clear all caches
     */
    public void clearAllCaches() {
        log.info("Manually clearing all caches");
        
        cacheManager.getCacheNames().forEach(cacheName -> {
            Objects.requireNonNull(cacheManager.getCache(cacheName)).clear();
            log.info("Cleared cache: {}", cacheName);
        });
    }

    /**
     * Clear specific cache by name
     */
    public void clearCache(String cacheName) {
        log.info("Manually clearing cache: {}", cacheName);
        
        if (cacheManager.getCache(cacheName) != null) {
            Objects.requireNonNull(cacheManager.getCache(cacheName)).clear();
            log.info("Cleared cache: {}", cacheName);
        } else {
            log.warn("Cache not found: {}", cacheName);
        }
    }
}
