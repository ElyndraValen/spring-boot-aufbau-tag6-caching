package de.javafleet.caching.service;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * Calculator Service mit Cache-Demo
 * 
 * Alle Methoden simulieren langsame Berechnungen (2 Sekunden)
 * um den Cache-Effekt deutlich zu machen.
 * 
 * @author Elyndra Valen
 */
@Service
public class CalculatorService {
    
    /**
     * Addition mit Cache
     * 
     * Cache-Key wird automatisch aus Parametern generiert: "a_b"
     * Beispiel: add(10, 20) → Cache-Key: "10.0_20.0"
     */
    @Cacheable("calculations")
    public double add(double a, double b) {
        System.out.println("🔴 BERECHNUNG LÄUFT: " + a + " + " + b);
        simulateLongRunningCalculation();
        return a + b;
    }
    
    /**
     * Subtraktion mit Cache
     */
    @Cacheable("calculations")
    public double subtract(double a, double b) {
        System.out.println("🔴 BERECHNUNG LÄUFT: " + a + " - " + b);
        simulateLongRunningCalculation();
        return a - b;
    }
    
    /**
     * Multiplikation mit Cache
     */
    @Cacheable("calculations")
    public double multiply(double a, double b) {
        System.out.println("🔴 BERECHNUNG LÄUFT: " + a + " × " + b);
        simulateLongRunningCalculation();
        return a * b;
    }
    
    /**
     * Division mit Cache
     */
    @Cacheable("calculations")
    public double divide(double a, double b) {
        System.out.println("🔴 BERECHNUNG LÄUFT: " + a + " ÷ " + b);
        if (b == 0) {
            throw new IllegalArgumentException("Division durch Null nicht erlaubt!");
        }
        simulateLongRunningCalculation();
        return a / b;
    }
    
    /**
     * Potenz mit Cache
     */
    @Cacheable("calculations")
    public double power(double base, double exponent) {
        System.out.println("🔴 BERECHNUNG LÄUFT: " + base + " ^ " + exponent);
        simulateLongRunningCalculation();
        return Math.pow(base, exponent);
    }
    
    /**
     * Löscht einen spezifischen Cache-Eintrag
     * 
     * Custom Key Expression: "#a + '_' + #b"
     * Damit können wir gezielt einen Eintrag löschen
     */
    @CacheEvict(value = "calculations", key = "#a + '_' + #b")
    public void evictCalculation(double a, double b) {
        System.out.println("🗑️ CACHE GELÖSCHT FÜR: " + a + ", " + b);
    }
    
    /**
     * Löscht den kompletten Cache
     * 
     * allEntries = true → Alle Einträge werden gelöscht
     */
    @CacheEvict(value = "calculations", allEntries = true)
    public void clearCache() {
        System.out.println("🗑️ KOMPLETTER CACHE GELÖSCHT");
    }
    
    /**
     * Simuliert eine langsame Berechnung (2 Sekunden)
     * 
     * In der Realität wäre das:
     * - Ein komplexer Datenbank-Query
     * - Ein API-Call zu einem externen Service
     * - Eine aufwändige mathematische Berechnung
     */
    private void simulateLongRunningCalculation() {
        try {
            Thread.sleep(2000); // 2 Sekunden warten
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Berechnung wurde unterbrochen", e);
        }
    }
}
