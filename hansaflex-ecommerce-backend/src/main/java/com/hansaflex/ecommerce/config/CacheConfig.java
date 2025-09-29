package com.hansaflex.ecommerce.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(caffeineCacheBuilder());
        
        // Define specific caches with different configurations
        cacheManager.setCacheNames(java.util.Arrays.asList(
            "products",           // Product catalog cache
            "pricingConfig",      // VAT rules cache
            "users",             // User authentication cache
            "orders",            // Order history cache
            "productDetails",    // Individual product cache
            "productPrice",      // Calculated prices cache
            "categories",        // Available categories cache
            "regions"            // Available regions cache
        ));
        
        return cacheManager;
    }

    private Caffeine<Object, Object> caffeineCacheBuilder() {
        return Caffeine.newBuilder()
                .initialCapacity(100)
                .maximumSize(1000)
                .expireAfterWrite(Duration.ofMinutes(30)) // Cache expires after 30 minutes
                .expireAfterAccess(Duration.ofMinutes(10)) // Cache expires after 10 minutes of inactivity
                .recordStats(); // Enable cache statistics
    }
}