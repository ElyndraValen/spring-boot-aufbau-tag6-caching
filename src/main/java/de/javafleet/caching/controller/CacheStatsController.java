package de.javafleet.caching.controller;

import com.github.benmanes.caffeine.cache.stats.CacheStats;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * REST Controller für Cache-Statistiken
 * 
 * Zeigt Hit-Rate, Miss-Rate und Cache-Größe
 * für Monitoring und Performance-Analyse
 * 
 * @author Elyndra Valen
 */
@RestController
@RequestMapping("/cache")
public class CacheStatsController {
    
    private final CacheManager cacheManager;
    
    public CacheStatsController(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }
    
    /**
     * Zeigt Cache-Statistiken für alle Caches
     * 
     * Beispiel Response:
     * {
     *   "calculations": {
     *     "hitCount": 47,
     *     "missCount": 3,
     *     "hitRate": "94.00%",
     *     "missRate": "6.00%",
     *     "size": 3,
     *     "evictionCount": 0
     *   }
     * }
     */
    @GetMapping("/stats")
    public Map<String, Object> getStats() {
        Map<String, Object> allStats = new HashMap<>();
        
        cacheManager.getCacheNames().forEach(cacheName -> {
            Cache cache = cacheManager.getCache(cacheName);
            
            if (cache instanceof CaffeineCache) {
                CaffeineCache caffeineCache = (CaffeineCache) cache;
                com.github.benmanes.caffeine.cache.Cache<Object, Object> nativeCache = 
                    caffeineCache.getNativeCache();
                
                CacheStats stats = nativeCache.stats();
                
                Map<String, Object> cacheStats = new HashMap<>();
                cacheStats.put("hitCount", stats.hitCount());
                cacheStats.put("missCount", stats.missCount());
                cacheStats.put("hitRate", String.format("%.2f%%", stats.hitRate() * 100));
                cacheStats.put("missRate", String.format("%.2f%%", (1 - stats.hitRate()) * 100));
                cacheStats.put("loadSuccessCount", stats.loadSuccessCount());
                cacheStats.put("loadFailureCount", stats.loadFailureCount());
                cacheStats.put("evictionCount", stats.evictionCount());
                cacheStats.put("size", nativeCache.estimatedSize());
                
                allStats.put(cacheName, cacheStats);
            }
        });
        
        return allStats;
    }
    
    /**
     * Zeigt eine Zusammenfassung aller Caches
     * 
     * Nützlich für Quick-Check im Dashboard
     */
    @GetMapping("/summary")
    public Map<String, Object> getSummary() {
        Map<String, Object> summary = new HashMap<>();
        
        long totalHits = 0;
        long totalMisses = 0;
        long totalSize = 0;
        int cacheCount = 0;
        
        for (String cacheName : cacheManager.getCacheNames()) {
            Cache cache = cacheManager.getCache(cacheName);
            
            if (cache instanceof CaffeineCache) {
                CaffeineCache caffeineCache = (CaffeineCache) cache;
                com.github.benmanes.caffeine.cache.Cache<Object, Object> nativeCache = 
                    caffeineCache.getNativeCache();
                
                CacheStats stats = nativeCache.stats();
                
                totalHits += stats.hitCount();
                totalMisses += stats.missCount();
                totalSize += nativeCache.estimatedSize();
                cacheCount++;
            }
        }
        
        double overallHitRate = totalHits + totalMisses > 0 
            ? (double) totalHits / (totalHits + totalMisses) * 100 
            : 0.0;
        
        summary.put("cacheCount", cacheCount);
        summary.put("totalHits", totalHits);
        summary.put("totalMisses", totalMisses);
        summary.put("overallHitRate", String.format("%.2f%%", overallHitRate));
        summary.put("totalSize", totalSize);
        
        return summary;
    }
    
    /**
     * Zeigt alle verfügbaren Cache-Namen
     */
    @GetMapping("/names")
    public Map<String, Object> getCacheNames() {
        return Map.of(
            "caches", cacheManager.getCacheNames(),
            "count", cacheManager.getCacheNames().size()
        );
    }
}
