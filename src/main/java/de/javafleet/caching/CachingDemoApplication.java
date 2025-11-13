package de.javafleet.caching;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * Spring Boot Aufbau - Tag 6: Caching Demo Application
 * Tag 6 von 10: Spring Boot Aufbau Kurs
 * 
 * @author Elyndra Valen - Java Fleet Systems Consulting
 */
@SpringBootApplication
@EnableCaching  // Aktiviert Spring Cache Abstraction
public class CachingDemoApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(CachingDemoApplication.class, args);
        
        System.out.println("\n");
        System.out.println("=================================================");
        System.out.println("🚀 Spring Boot Aufbau - Tag 6: Caching gestartet!");
        System.out.println("=================================================");
        System.out.println("📖 Teste die Endpoints:");
        System.out.println("   GET  http://localhost:8080/calc/add?a=10&b=20");
        System.out.println("   GET  http://localhost:8080/cache/stats");
        System.out.println("   DELETE http://localhost:8080/cache/clear");
        System.out.println("=================================================");
        System.out.println("\n");
    }
}
