package com.hansaflex.ecommerce.controller;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.stats.CacheStats;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/api/admin/cache")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class CacheController {

    private final CacheManager cacheManager;

    /**
     * Get cache statistics (Admin only)
     * GET /api/admin/cache/stats
     */
    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getCacheStats() {
        log.info("Admin requesting cache statistics");
        
        Map<String, Object> stats = new HashMap<>();
        
        // Get all cache names
        cacheManager.getCacheNames().forEach(cacheName -> {
            CaffeineCache caffeineCache = (CaffeineCache) cacheManager.getCache(cacheName);
            if (caffeineCache != null) {
                Cache<Object, Object> nativeCache = caffeineCache.getNativeCache();
                CacheStats cacheStats = nativeCache.stats();
                
                Map<String, Object> cacheInfo = new HashMap<>();
                cacheInfo.put("size", nativeCache.estimatedSize());
                cacheInfo.put("hitCount", cacheStats.hitCount());
                cacheInfo.put("missCount", cacheStats.missCount());
                cacheInfo.put("hitRate", cacheStats.hitRate());
                cacheInfo.put("missRate", cacheStats.missRate());
                cacheInfo.put("evictionCount", cacheStats.evictionCount());
                cacheInfo.put("loadCount", cacheStats.loadCount());
                cacheInfo.put("loadSuccessCount", cacheStats.loadSuccessCount());
                cacheInfo.put("averageLoadPenalty", cacheStats.averageLoadPenalty());
                
                stats.put(cacheName, cacheInfo);
            }
        });
        
        return ResponseEntity.ok(stats);
    }

    /**
     * Clear all caches (Admin only)
     * DELETE /api/admin/cache
     */
    @DeleteMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> clearAllCaches() {
        log.info("Admin clearing all caches");
        
        cacheManager.getCacheNames().forEach(cacheName -> {
            Objects.requireNonNull(cacheManager.getCache(cacheName)).clear();
            log.info("Cleared cache: {}", cacheName);
        });
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "All caches cleared successfully");
        response.put("timestamp", java.time.LocalDateTime.now().toString());
        
        return ResponseEntity.ok(response);
    }

    /**
     * Clear specific cache (Admin only)
     * DELETE /api/admin/cache/{cacheName}
     */
    @DeleteMapping("/{cacheName}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> clearCache(@PathVariable String cacheName) {
        log.info("Admin clearing cache: {}", cacheName);
        
        if (cacheManager.getCache(cacheName) != null) {
            Objects.requireNonNull(cacheManager.getCache(cacheName)).clear();
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "Cache '" + cacheName + "' cleared successfully");
            response.put("timestamp", java.time.LocalDateTime.now().toString());
            
            return ResponseEntity.ok(response);
        } else {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Cache '" + cacheName + "' not found");
            response.put("timestamp", java.time.LocalDateTime.now().toString());
            
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Get cache names (Admin only)
     * GET /api/admin/cache/names
     */
    @GetMapping("/names")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getCacheNames() {
        log.info("Admin requesting cache names");
        
        Map<String, Object> response = new HashMap<>();
        response.put("cacheNames", cacheManager.getCacheNames());
        response.put("count", cacheManager.getCacheNames().size());
        
        return ResponseEntity.ok(response);
    }
}
