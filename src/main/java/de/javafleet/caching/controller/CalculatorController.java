package de.javafleet.caching.controller;

import de.javafleet.caching.service.CalculatorService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * REST Controller für Calculator-Operationen
 * 
 * Alle Endpoints messen die Ausführungszeit um den Cache-Effekt zu zeigen:
 * - Erster Aufruf: ~2000ms (Cache-Miss)
 * - Zweiter Aufruf: ~2ms (Cache-Hit)
 * 
 * @author Elyndra Valen
 */
@RestController
@RequestMapping("/calc")
public class CalculatorController {
    
    private final CalculatorService calculatorService;

    /**
     * Constructor.
     * 
     * @param calculatorService for calculating numbers.
     */
    public CalculatorController(CalculatorService calculatorService) {
        this.calculatorService = calculatorService;
    }
    
    
    
    /**
     * Addition: a + b
     * 
     * Beispiel: GET /calc/add?a=10&b=20
     * Response: {"a":10.0,"b":20.0,"result":30.0,"durationMs":2001,"cached":false}
     */
    @GetMapping("/add")
    public Map<String, Object> add(
            @RequestParam double a, 
            @RequestParam double b) {
        
        long start = System.currentTimeMillis();
        double result = calculatorService.add(a, b);
        long duration = System.currentTimeMillis() - start;
        
        return buildResponse(a, b, result, duration, "addition");
    }
    
    /**
     * Subtraktion: a - b
     * 
     * Beispiel: GET /calc/subtract?a=30&b=10
     */
    @GetMapping("/subtract")
    public Map<String, Object> subtract(
            @RequestParam double a, 
            @RequestParam double b) {
        
        long start = System.currentTimeMillis();
        double result = calculatorService.subtract(a, b);
        long duration = System.currentTimeMillis() - start;
        
        return buildResponse(a, b, result, duration, "subtraction");
    }
    
    /**
     * Multiplikation: a × b
     * 
     * Beispiel: GET /calc/multiply?a=5&b=6
     */
    @GetMapping("/multiply")
    public Map<String, Object> multiply(
            @RequestParam double a, 
            @RequestParam double b) {
        
        long start = System.currentTimeMillis();
        double result = calculatorService.multiply(a, b);
        long duration = System.currentTimeMillis() - start;
        
        return buildResponse(a, b, result, duration, "multiplication");
    }
    
    /**
     * Division: a ÷ b
     * 
     * Beispiel: GET /calc/divide?a=100&b=5
     */
    @GetMapping("/divide")
    public Map<String, Object> divide(
            @RequestParam double a, 
            @RequestParam double b) {
        
        long start = System.currentTimeMillis();
        double result = calculatorService.divide(a, b);
        long duration = System.currentTimeMillis() - start;
        
        return buildResponse(a, b, result, duration, "division");
    }
    
    /**
     * Potenz: base ^ exponent
     * 
     * Beispiel: GET /calc/power?base=2&exponent=10
     */
    @GetMapping("/power")
    public Map<String, Object> power(
            @RequestParam double base, 
            @RequestParam double exponent) {
        
        long start = System.currentTimeMillis();
        double result = calculatorService.power(base, exponent);
        long duration = System.currentTimeMillis() - start;
        
        Map<String, Object> response = new HashMap<>();
        response.put("base", base);
        response.put("exponent", exponent);
        response.put("result", result);
        response.put("durationMs", duration);
        response.put("cached", duration < 100); // Heuristik: < 100ms = Cache-Hit
        response.put("operation", "power");
        
        return response;
    }
    
    /**
     * Löscht einen spezifischen Cache-Eintrag
     * 
     * Beispiel: DELETE /calc/evict?a=10&b=20
     */
    @DeleteMapping("/evict")
    public Map<String, String> evict(
            @RequestParam double a, 
            @RequestParam double b) {
        
        calculatorService.evictCalculation(a, b);
        
        return Map.of(
            "message", "Cache-Eintrag gelöscht",
            "a", String.valueOf(a),
            "b", String.valueOf(b)
        );
    }
    
    /**
     * Löscht den kompletten Cache
     * 
     * Beispiel: DELETE /calc/clear
     */
    @DeleteMapping("/clear")
    public Map<String, String> clear() {
        calculatorService.clearCache();
        return Map.of("message", "Kompletter Cache gelöscht");
    }
    
    /**
     * Helper-Methode zum Erstellen der Response
     */
    private Map<String, Object> buildResponse(double a, double b, double result, 
                                              long duration, String operation) {
        Map<String, Object> response = new HashMap<>();
        response.put("a", a);
        response.put("b", b);
        response.put("result", result);
        response.put("durationMs", duration);
        response.put("cached", duration < 100); // Heuristik: < 100ms = Cache-Hit
        response.put("operation", operation);
        
        return response;
    }
}
