package de.javafleet.caching.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * Cache Configuration mit Caffeine
 * 
 * Production-Ready Setup:
 * - Maximum Size: 100 Einträge
 * - TTL: 10 Minuten
 * - Statistics aktiviert für Monitoring
 * 
 * @author Elyndra Valen
 */
@Configuration
@EnableCaching
public class CacheConfig {
    
    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager("calculations");
        cacheManager.setCaffeine(caffeineConfig());
        return cacheManager;
    }
    
    private Caffeine<Object, Object> caffeineConfig() {
        return Caffeine.newBuilder()
                .maximumSize(100)                        // Max 100 Einträge im Cache
                .expireAfterWrite(10, TimeUnit.MINUTES)  // TTL: 10 Minuten
                .recordStats();                          // Statistiken aktivieren
    }
}
