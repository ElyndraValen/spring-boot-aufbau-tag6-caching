# 🚀 Quick Start Guide - Spring Boot Aufbau Tag 6

## 📋 Schritt-für-Schritt Anleitung für Tag 6: Caching

### 1️⃣ Projekt starten

```bash
# Option A: Mit Start-Script (empfohlen)
./START.sh

# Option B: Manuell
mvn clean install
mvn spring-boot:run
```

**Erwartete Ausgabe:**
```
🚀 Spring Boot Aufbau - Tag 6: Caching gestartet!
📖 Teste die Endpoints:
   GET  http://localhost:8080/calc/add?a=10&b=20
   ...
```

---

## 2️⃣ Cache-Effekt testen

### Test 1: Addition mit Cache

```bash
# Erster Aufruf (Cache-Miss - dauert ~2000ms)
curl "http://localhost:8080/calc/add?a=10&b=20"

# Response:
# {
#   "a": 10.0,
#   "b": 20.0,
#   "result": 30.0,
#   "durationMs": 2001,
#   "cached": false,
#   "operation": "addition"
# }
```

**In der Console siehst du:**
```
🔴 BERECHNUNG LÄUFT: 10.0 + 20.0
```

```bash
# Zweiter Aufruf (Cache-Hit - dauert ~2ms)
curl "http://localhost:8080/calc/add?a=10&b=20"

# Response:
# {
#   "a": 10.0,
#   "b": 20.0,
#   "result": 30.0,
#   "durationMs": 2,
#   "cached": true,
#   "operation": "addition"
# }
```

**In der Console siehst du:** NICHTS! (Cache-Hit)

**🎉 Erfolg!** Der zweite Aufruf ist 1000x schneller!

---

### Test 2: Alle Operationen testen

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

---

## 3️⃣ Cache-Statistiken ansehen

```bash
# Vollständige Statistiken
curl http://localhost:8080/cache/stats

# Response:
# {
#   "calculations": {
#     "hitCount": 5,
#     "missCount": 5,
#     "hitRate": "50.00%",
#     "missRate": "50.00%",
#     "size": 5,
#     "evictionCount": 0
#   }
# }
```

**Was bedeuten die Zahlen?**
- `hitCount`: Wie oft wurde aus Cache gelesen
- `missCount`: Wie oft musste neu berechnet werden
- `hitRate`: Prozentsatz der Cache-Hits (je höher, desto besser!)
- `size`: Aktuelle Anzahl Einträge im Cache

```bash
# Zusammenfassung
curl http://localhost:8080/cache/summary

# Cache-Namen
curl http://localhost:8080/cache/names
```

---

## 4️⃣ Cache verwalten

```bash
# Kompletten Cache leeren
curl -X DELETE http://localhost:8080/cache/clear

# Response:
# {
#   "message": "Kompletter Cache gelöscht"
# }
```

```bash
# Einzelnen Eintrag löschen
curl -X DELETE "http://localhost:8080/calc/evict?a=10&b=20"

# Response:
# {
#   "message": "Cache-Eintrag gelöscht",
#   "a": "10.0",
#   "b": "20.0"
# }
```

---

## 5️⃣ Actuator Endpoints

```bash
# Health Check
curl http://localhost:8080/actuator/health

# Alle Caches
curl http://localhost:8080/actuator/caches

# Metrics
curl http://localhost:8080/actuator/metrics

# App Info
curl http://localhost:8080/actuator/info
```

---

## 🎯 Performance-Test

Teste den dramatischen Performance-Unterschied:

```bash
# Script für Performance-Test
for i in {1..10}; do
  echo "Request $i:"
  curl -w "\nTime: %{time_total}s\n\n" "http://localhost:8080/calc/add?a=10&b=20"
done

# Erwartetes Ergebnis:
# Request 1: Time: 2.001s (Cache-Miss)
# Request 2: Time: 0.002s (Cache-Hit)
# Request 3: Time: 0.002s (Cache-Hit)
# ...
```

**Performance-Gewinn:**
- Request 1: 2001ms
- Requests 2-10: je 2ms
- **Total: 2019ms statt 20.010ms**
- **→ 90% schneller!** 🚀

---

## 🔍 Debugging-Tipps

### Cache funktioniert nicht?

1. **Console-Output prüfen:**
   - Siehst du "🔴 BERECHNUNG LÄUFT"?
   - Wenn JA bei jedem Request → Cache funktioniert nicht
   - Wenn NUR beim ersten Request → Cache funktioniert! ✅

2. **@EnableCaching aktiviert?**
   ```java
   @SpringBootApplication
   @EnableCaching  // ← Wichtig!
   public class CachingDemoApplication { ... }
   ```

3. **Logs prüfen:**
   ```bash
   # Spring Boot sollte zeigen:
   # "...CaffeineCacheManager... initialized"
   ```

### Performance-Test zeigt keinen Unterschied?

- **Warte zwischen Tests:** Cache hat 10 Minuten TTL
- **Verschiedene Parameter:** Teste mit a=10&b=20, dann a=5&b=15
- **Cache leeren:** `curl -X DELETE http://localhost:8080/cache/clear`

---

## 📚 Nächste Schritte

### Experimentiere mit:

1. **TTL ändern** (in `CacheConfig.java`):
   ```java
   .expireAfterWrite(1, TimeUnit.MINUTES)  // 1 Minute statt 10
   ```

2. **Max-Size reduzieren**:
   ```java
   .maximumSize(5)  // Nur 5 Einträge
   ```

3. **Eigene Berechnungen** hinzufügen

4. **Redis als Cache-Backend** (siehe README.md)

---

## ❓ Häufige Fragen

**Q: Warum dauert der erste Request so lange?**  
A: Das ist absichtlich! `Thread.sleep(2000)` simuliert eine langsame DB-Query oder API-Call.

**Q: Wird der Cache automatisch geleert?**  
A: Ja! Nach 10 Minuten (TTL) oder wenn 100 Einträge erreicht sind (Max-Size).

**Q: Kann ich den Cache im Browser testen?**  
A: Ja! Öffne `http://localhost:8080/calc/add?a=10&b=20` im Browser und lade mehrfach neu.

**Q: Wo sehe ich die Console-Ausgabe "🔴 BERECHNUNG LÄUFT"?**  
A: Im Terminal wo `mvn spring-boot:run` läuft.

---

**Viel Erfolg beim Testen!** 🎉

Bei Fragen: elyndra@java-developer.online
