package de.javafleet.caching;

import de.javafleet.caching.service.CalculatorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration Tests für CalculatorService mit Cache
 * 
 * Diese Tests demonstrieren den Cache-Effekt:
 * - Erster Aufruf: Langsam (~2000ms)
 * - Zweiter Aufruf: Schnell (~0ms) - aus Cache
 * 
 * @author Elyndra Valen
 */
@SpringBootTest
class CalculatorServiceTest {
    
    @Autowired
    private CalculatorService calculatorService;
    
    @Autowired
    private CacheManager cacheManager;
    
    @BeforeEach
    void setUp() {
        // Cache vor jedem Test leeren
        calculatorService.clearCache();
    }
    
    @Test
    @DisplayName("Addition sollte korrekt berechnet werden")
    void testAdd() {
        // When
        double result = calculatorService.add(10, 20);
        
        // Then
        assertThat(result).isEqualTo(30.0);
    }
    
    @Test
    @DisplayName("Cache sollte beim zweiten Aufruf verwendet werden")
    void testCacheHit() {
        // First call - should take ~2000ms
        long start1 = System.currentTimeMillis();
        double result1 = calculatorService.add(10, 20);
        long duration1 = System.currentTimeMillis() - start1;
        
        // Second call - should be from cache (~0ms)
        long start2 = System.currentTimeMillis();
        double result2 = calculatorService.add(10, 20);
        long duration2 = System.currentTimeMillis() - start2;
        
        // Assert
        assertThat(result1).isEqualTo(30.0);
        assertThat(result2).isEqualTo(30.0);
        assertThat(duration1).isGreaterThan(1900); // Erster Aufruf dauert ~2000ms
        assertThat(duration2).isLessThan(100);     // Zweiter Aufruf ist fast instant
        
        System.out.println("First call: " + duration1 + "ms");
        System.out.println("Second call (cached): " + duration2 + "ms");
        System.out.println("Speed improvement: " + (duration1 / Math.max(duration2, 1)) + "x faster!");
    }
    
    @Test
    @DisplayName("Verschiedene Parameter sollten verschiedene Cache-Keys haben")
    void testDifferentParameters() {
        // Different parameters = different cache keys
        double result1 = calculatorService.add(10, 20);
        double result2 = calculatorService.add(5, 15);
        
        assertThat(result1).isEqualTo(30.0);
        assertThat(result2).isEqualTo(20.0);
    }
    
    @Test
    @DisplayName("Cache sollte nach Evict geleert sein")
    void testCacheEvict() {
        // First call - populate cache
        calculatorService.add(10, 20);
        
        // Verify cache contains entry
        assertThat(cacheManager.getCache("calculations")).isNotNull();
        
        // Evict cache
        calculatorService.clearCache();
        
        // Second call should be slow again (cache was cleared)
        long start = System.currentTimeMillis();
        calculatorService.add(10, 20);
        long duration = System.currentTimeMillis() - start;
        
        assertThat(duration).isGreaterThan(1900); // Should be slow again
    }
    
    @Test
    @DisplayName("Alle Rechenoperationen sollten funktionieren")
    void testAllOperations() {
        assertThat(calculatorService.add(10, 5)).isEqualTo(15.0);
        assertThat(calculatorService.subtract(10, 5)).isEqualTo(5.0);
        assertThat(calculatorService.multiply(10, 5)).isEqualTo(50.0);
        assertThat(calculatorService.divide(10, 5)).isEqualTo(2.0);
        assertThat(calculatorService.power(2, 3)).isEqualTo(8.0);
    }
}
