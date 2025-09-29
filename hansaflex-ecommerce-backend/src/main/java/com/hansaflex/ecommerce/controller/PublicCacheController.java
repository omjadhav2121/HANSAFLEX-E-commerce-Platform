package com.hansaflex.ecommerce.controller;

import com.hansaflex.ecommerce.service.CacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/public/cache")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class PublicCacheController {

    private final CacheService cacheService;

    /**
     * Clear all product caches (Public endpoint for testing)
     * POST /api/public/cache/clear-products
     */
    @PostMapping("/clear-products")
    public ResponseEntity<Map<String, String>> clearProductCaches() {
        log.info("Public request to clear product caches");
        
        try {
            cacheService.clearProductCaches();
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "Product caches cleared successfully");
            response.put("timestamp", java.time.LocalDateTime.now().toString());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to clear product caches: {}", e.getMessage(), e);
            
            Map<String, String> response = new HashMap<>();
            response.put("error", "Failed to clear product caches: " + e.getMessage());
            response.put("timestamp", java.time.LocalDateTime.now().toString());
            
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * Clear all caches (Public endpoint for testing)
     * POST /api/public/cache/clear-all
     */
    @PostMapping("/clear-all")
    public ResponseEntity<Map<String, String>> clearAllCaches() {
        log.info("Public request to clear all caches");
        
        try {
            cacheService.clearAllCaches();
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "All caches cleared successfully");
            response.put("timestamp", java.time.LocalDateTime.now().toString());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to clear all caches: {}", e.getMessage(), e);
            
            Map<String, String> response = new HashMap<>();
            response.put("error", "Failed to clear all caches: " + e.getMessage());
            response.put("timestamp", java.time.LocalDateTime.now().toString());
            
            return ResponseEntity.internalServerError().body(response);
        }
    }
}
