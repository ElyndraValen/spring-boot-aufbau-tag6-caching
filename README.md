# 🚀 Spring Boot Aufbau - Tag 6: Caching

> Komplettes Projekt für **Tag 6: Spring Boot Caching** aus dem Spring Boot Aufbau Kurs von Java Fleet Systems Consulting

## 📋 Über dieses Projekt

Dies ist das vollständige Beispielprojekt für Tag 6 des Spring Boot Aufbau Kurses. Du lernst:

- ✅ Spring Cache Abstraction verstehen
- ✅ `@Cacheable`, `@CacheEvict`, `@CachePut` einsetzen
- ✅ Performance durch Caching dramatisch verbessern (2000ms → 2ms!)
- ✅ Caffeine als Production-Cache konfigurieren
- ✅ Cache-Statistics mit Actuator überwachen

## 🎯 Was macht diese App?

Ein Calculator-Service mit simulierten langsamen Berechnungen (2 Sekunden pro Operation). Durch Spring Boot Caching werden wiederholte Aufrufe 1000x schneller!

**Demo:**
```bash
# Erster Aufruf: 2000ms
curl "http://localhost:8080/calc/add?a=10&b=20"
# Response: {"a":10.0,"b":20.0,"result":30.0,"durationMs":2001}

# Zweiter Aufruf: 2ms (aus Cache!)
curl "http://localhost:8080/calc/add?a=10&b=20"
# Response: {"a":10.0,"b":20.0,"result":30.0,"durationMs":2}
```

## 🏗️ Projekt-Struktur

```
spring-boot-aufbau-tag6-caching/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── de/
│   │   │       └── javafleet/
│   │   │           └── caching/
│   │   │               ├── CachingDemoApplication.java
│   │   │               ├── config/
│   │   │               │   └── CacheConfig.java
│   │   │               ├── controller/
│   │   │               │   ├── CalculatorController.java
│   │   │               │   └── CacheStatsController.java
│   │   │               └── service/
│   │   │                   └── CalculatorService.java
│   │   └── resources/
│   │       └── application.yml
│   └── test/
│       └── java/
│           └── de/
│               └── javafleet/
│                   └── caching/
│                       ├── CacheIntegrationTest.java
│                       └── CalculatorServiceTest.java
├── pom.xml
└── README.md
```

## 🚀 Quick Start

### Voraussetzungen

- ☕ Java 17 oder höher
- 📦 Maven 3.6+
- 🔧 IDE deiner Wahl (IntelliJ IDEA, Eclipse, VS Code)

### Installation & Start

```bash
# 1. Repository klonen
git clone https://github.com/java-fleet/spring-boot-aufbau-tag6-caching.git
cd spring-boot-aufbau-tag6-caching

# 2. Dependencies laden & App starten
mvn clean install
mvn spring-boot:run

# 3. Testen
curl "http://localhost:8080/calc/add?a=10&b=20"
```

## 📚 Features & Endpoints

### Rechner-Operationen

Alle Operationen haben simulierte 2-Sekunden-Verzögerung beim ersten Aufruf:

```bash
# Addition
curl "http://localhost:8080/calc/add?a=10&b=20"

# Subtraktion
curl "http://localhost:8080/calc/subtract?a=30&b=10"

# Multiplikation
curl "http://localhost:8080/calc/multiply?a=5&b=6"

# Division
curl "http://localhost:8080/calc/divide?a=100&b=5"

# Potenz
curl "http://localhost:8080/calc/power?base=2&exponent=10"
```

### Cache-Management

```bash
# Cache-Statistiken anzeigen
curl http://localhost:8080/cache/stats
# Response:
# {
#   "calculations": {
#     "hitCount": 47,
#     "missCount": 3,
#     "hitRate": "94.00%",
#     "size": 3
#   }
# }

# Kompletten Cache leeren
curl -X DELETE http://localhost:8080/cache/clear

# Einzelnen Cache-Eintrag löschen
curl -X DELETE "http://localhost:8080/cache/evict?a=10&b=20"
```

### Actuator Endpoints (mit Caffeine)

```bash
# Health Check
curl http://localhost:8080/actuator/health

# Cache Metrics
curl http://localhost:8080/actuator/caches

# Application Metrics
curl http://localhost:8080/actuator/metrics
```

## 🔧 Konfiguration

### application.yml

```yaml
spring:
  application:
    name: caching-demo
  cache:
    type: caffeine
    caffeine:
      spec: maximumSize=100,expireAfterWrite=10m

management:
  endpoints:
    web:
      exposure:
        include: health, caches, metrics
  endpoint:
    caches:
      enabled: true

logging:
  level:
    de.javafleet: DEBUG
```

### Caffeine Cache Configuration

```java
@Configuration
@EnableCaching
public class CacheConfig {
    
    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = 
            new CaffeineCacheManager("calculations");
        cacheManager.setCaffeine(caffeineConfig());
        return cacheManager;
    }
    
    private Caffeine<Object, Object> caffeineConfig() {
        return Caffeine.newBuilder()
                .maximumSize(100)                        // Max 100 Einträge
                .expireAfterWrite(10, TimeUnit.MINUTES)  // TTL: 10 Min
                .recordStats();                          // Stats aktivieren
    }
}
```

## 🧪 Tests ausführen

```bash
# Alle Tests
mvn test

# Nur Integration-Tests
mvn test -Dtest=*IntegrationTest

# Mit Coverage-Report
mvn clean test jacoco:report
```

## 📖 Code-Beispiele

### Service mit @Cacheable

```java
@Service
public class CalculatorService {
    
    @Cacheable("calculations")
    public double add(double a, double b) {
        System.out.println("🔴 BERECHNUNG LÄUFT: " + a + " + " + b);
        simulateLongRunningCalculation();
        return a + b;
    }
    
    @CacheEvict(value = "calculations", key = "#a + '_' + #b")
    public void evictAdd(double a, double b) {
        System.out.println("🗑️ CACHE GELÖSCHT FÜR: " + a + " + " + b);
    }
    
    @CacheEvict(value = "calculations", allEntries = true)
    public void clearCache() {
        System.out.println("🗑️ KOMPLETTER CACHE GELÖSCHT");
    }
    
    private void simulateLongRunningCalculation() {
        try {
            Thread.sleep(2000); // 2 Sekunden
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
```

### Controller mit Timing

```java
@RestController
@RequestMapping("/calc")
@RequiredArgsConstructor
public class CalculatorController {
    
    private final CalculatorService calculatorService;
    
    @GetMapping("/add")
    public Map<String, Object> add(
            @RequestParam double a, 
            @RequestParam double b) {
        
        long start = System.currentTimeMillis();
        double result = calculatorService.add(a, b);
        long duration = System.currentTimeMillis() - start;
        
        return Map.of(
            "a", a,
            "b", b,
            "result", result,
            "durationMs", duration,
            "cached", duration < 100  // Heuristik für Cache-Hit
        );
    }
}
```

## 📊 Performance-Vergleich

### Ohne Cache

```bash
Request 1: 2001ms
Request 2: 2002ms
Request 3: 2000ms
Total: 6003ms
```

### Mit Cache

```bash
Request 1: 2001ms (Cache-Miss - Initial Load)
Request 2: 2ms    (Cache-Hit!)
Request 3: 2ms    (Cache-Hit!)
Total: 2005ms

→ 66% schneller! (bei nur 3 Requests)
→ Bei 100 Requests: 99% schneller!
```

## 🎓 Learning-Pfad

### 🟢 Grundlagen (2-3 Stunden)

1. **@EnableCaching** verstehen und aktivieren
2. **@Cacheable** auf Service-Methoden anwenden
3. **Cache-Keys** verstehen (automatisch vs. custom)
4. **Performance-Unterschied** sehen und messen

### 🟡 Advanced (3-4 Stunden)

5. **@CacheEvict** für Cache-Invalidierung nutzen
6. **@CachePut** für Update-Strategien
7. **Caffeine** konfigurieren (TTL, Max-Size)
8. **Cache-Statistics** mit Actuator überwachen

### 🔵 Expert (1-2 Stunden)

9. **Conditional Caching** (condition, unless)
10. **Multiple Cache-Namen** verwalten
11. **Custom Cache-Key-Generator** erstellen
12. **Distributed Caching** mit Redis (Bonus)

## 🔍 Troubleshooting

### Cache funktioniert nicht

**Problem:** Methode wird immer ausgeführt, trotz @Cacheable

**Lösungen:**
- ✅ `@EnableCaching` in Application-Klasse hinzugefügt?
- ✅ Methode ist `public`? (private wird nicht gecacht!)
- ✅ Aufruf von außen? (Self-Invocation Problem bei internen Aufrufen)

**Self-Invocation Beispiel:**
```java
// ❌ FALSCH - Cache funktioniert nicht!
@Service
public class MyService {
    
    @Cacheable("data")
    public Data getData() { ... }
    
    public void processData() {
        Data data = this.getData();  // ← Self-Invocation!
    }
}

// ✅ RICHTIG - Cache funktioniert
@Service
public class MyService {
    
    private final MyService self;  // Inject self
    
    @Cacheable("data")
    public Data getData() { ... }
    
    public void processData() {
        Data data = self.getData();  // ← Proxy wird genutzt
    }
}
```

### Caffeine nicht aktiv

**Problem:** Simple Cache wird genutzt statt Caffeine

**Lösungen:**
- ✅ Dependency in `pom.xml`?
```xml
<dependency>
    <groupId>com.github.ben-manes.caffeine</groupId>
    <artifactId>caffeine</artifactId>
</dependency>
```
- ✅ `CacheConfig` mit `@Bean CacheManager` erstellt?
- ✅ Spring Boot erkennt Caffeine automatisch bei korrekter Config

### OutOfMemoryError

**Problem:** Cache wächst unbegrenzt

**Lösung:** Caffeine mit Limits konfigurieren:
```java
Caffeine.newBuilder()
    .maximumSize(100)                        // Pflicht!
    .expireAfterWrite(10, TimeUnit.MINUTES)  // Pflicht!
```

## 📚 Weiterführende Ressourcen

### Offizielle Dokumentation

- [Spring Cache Abstraction](https://docs.spring.io/spring-framework/reference/integration/cache.html)
- [Caffeine Cache](https://github.com/ben-manes/caffeine)
- [Spring Boot Actuator](https://docs.spring.io/spring-boot/reference/actuator/index.html)

### Blog-Serie

- 📝 [Tag 6: Spring Boot Caching - Der komplette Guide](#)
- 📝 [Performance-Optimierung mit Caching](#)
- 📝 [Distributed Caching: Caffeine vs. Redis vs. Hazelcast](#)

### Community

- 💬 [Java Fleet Discord](#) - Fragen stellen
- 🐛 [Issues melden](https://github.com/java-fleet/spring-boot-caching-demo/issues)
- 🤝 [Contributing Guidelines](CONTRIBUTING.md)

## 🐛 Übung: Cache-Bug finden

> **Achtung:** Diese Demo enthält einen absichtlichen Bug zum Lernen!

### Das Problem entdecken

Führe folgende Befehle aus und beobachte das Ergebnis:

```bash
# 1. Addition aufrufen
curl "http://localhost:8080/calc/add?a=5&b=3"
# Ergebnis: 8 ✅ (dauert 2 Sekunden)

# 2. Subtraktion mit gleichen Werten aufrufen
curl "http://localhost:8080/calc/subtract?a=5&b=3"
# Ergebnis: 8 ❌ (sollte 2 sein! - kommt sofort aus Cache)
```

**Was ist passiert?**

### Analyse

Alle Methoden im `CalculatorService` nutzen:
- Denselben Cache-Namen: `"calculations"`
- Die Standard-Key-Generierung (nur Parameter, NICHT Methodenname!)

```java
@Cacheable("calculations")
public double add(double a, double b) { ... }

@Cacheable("calculations")
public double subtract(double a, double b) { ... }
```

Der Cache-Key wird nur aus den Parametern generiert:
- `add(5, 3)` → Key: `SimpleKey[5.0, 3.0]` → Ergebnis: 8 (gecacht)
- `subtract(5, 3)` → Key: `SimpleKey[5.0, 3.0]` → Ergebnis: 8 (aus Cache!)

### Lösungsansätze

**Lösung A: Separate Caches pro Operation**

```java
@Cacheable("additions")
public double add(double a, double b) { ... }

@Cacheable("subtractions")
public double subtract(double a, double b) { ... }
```

Bei dieser Lösung muss auch die `CacheConfig` angepasst werden:

```java
@Bean
public CacheManager cacheManager() {
    CaffeineCacheManager cacheManager = new CaffeineCacheManager(
        "additions",
        "subtractions",
        "multiplications",
        "divisions",
        "powers"
    );
    cacheManager.setCaffeine(Caffeine.newBuilder()
            .maximumSize(100)
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .recordStats());
    return cacheManager;
}
```

**Lösung B: Custom Key mit SpEL (elegant!)**

```java
@Cacheable(value = "calculations", key = "#root.methodName + '_' + #a + '_' + #b")
public double add(double a, double b) { ... }
```

Hier wird der Methodenname Teil des Keys:
- `add(5, 3)` → Key: `"add_5.0_3.0"`
- `subtract(5, 3)` → Key: `"subtract_5.0_3.0"`

**Lösung C: String-Präfix (explizit)**

```java
@Cacheable(value = "calculations", key = "'add_' + #a + '_' + #b")
public double add(double a, double b) { ... }

@Cacheable(value = "calculations", key = "'subtract_' + #a + '_' + #b")
public double subtract(double a, double b) { ... }
```

### Diskussionsfragen

1. Welche Lösung ist am wartbarsten?
2. Wann macht ein gemeinsamer Cache Sinn, wann separate?
3. Was wären die Konsequenzen dieses Bugs in Produktion?

---

## 🏆 Challenges

Hast du Tag 6 durchgearbeitet? Teste dein Wissen:

### Challenge 1: Custom Cache-Key
Implementiere einen Cache der NUR die erste Nachkommastelle berücksichtigt:
```java
// add(10.1, 20.2) und add(10.9, 20.8) sollten GLEICHEN Cache-Key haben
@Cacheable(value = "calculations", key = "???")
public double add(double a, double b) { ... }
```

### Challenge 2: Time-Based Eviction
Erstelle einen Endpoint der automatisch jeden Morgen um 6:00 Uhr den Cache leert.

### Challenge 3: Hit-Rate Monitoring
Implementiere einen Alert wenn Hit-Rate unter 80% fällt.

### Challenge 4: Distributed Cache
Migriere das Projekt auf Redis als Cache-Backend.

**Lösungen:** Siehe [SOLUTIONS.md](SOLUTIONS.md)

## 👥 Über Java Fleet Systems Consulting

Dieses Projekt wurde erstellt von **Java Fleet Systems Consulting** - einem fiktiven Team für authentisches Java-Learning:

- **Elyndra Valen** - Senior Entwicklerin, Maven & Build-Expertin
- **Nova Trent** - Junior Entwicklerin, begeisterte Lernende
- **Code Sentinel** - Security-Experte, CI/CD-Architekt
- **Dr. Cassian Holt** - Senior Architect, Testing-Wissenschaftler

Mehr auf [java-developer.online](#)

## 📄 Lizenz

MIT License - siehe [LICENSE](LICENSE) für Details

## 🤝 Contributing

Beiträge sind willkommen! Siehe [CONTRIBUTING.md](CONTRIBUTING.md) für Guidelines.

**Häufige Beiträge:**
- 🐛 Bug-Fixes
- 📚 Dokumentations-Verbesserungen
- ✨ Neue Beispiele für Cache-Strategien
- 🧪 Zusätzliche Tests

## 📮 Kontakt

- 📧 Email: elyndra@java-developer.online
- 🐦 Twitter: [@JavaFleetHQ](#)
- 💬 Discord: [Java Fleet Community](#)

---

**Happy Caching!** 🚀

*Entwickelt mit ❤️ von Java Fleet Systems Consulting für Tag 6 des Spring Boot Aufbau Kurses*

---

## 🔖 Quick Reference

### Wichtigste Annotations

```java
@EnableCaching           // In Application-Klasse
@Cacheable("cacheName")  // Cache Ergebnis
@CacheEvict("cacheName") // Lösche aus Cache
@CachePut("cacheName")   // Update Cache
@Caching                 // Kombiniere mehrere
```

### Caffeine Best Practice

```java
Caffeine.newBuilder()
    .maximumSize(100)                        // Max Entries
    .expireAfterWrite(10, TimeUnit.MINUTES)  // TTL
    .expireAfterAccess(5, TimeUnit.MINUTES)  // Idle Timeout
    .recordStats()                           // Statistics
    .removalListener((key, value, cause) -> {
        // Optional: Log removals
    })
```

### Cache-Key Patterns

```java
// Simple Key
key = "#id"

// Composite Key
key = "#user.id + '_' + #user.email"

// Method Name + Args
key = "#root.methodName + '_' + #id"

// Conditional
condition = "#id > 0"
unless = "#result == null"
```

---

*Last updated: Oktober 2025 | Version: 1.0*
