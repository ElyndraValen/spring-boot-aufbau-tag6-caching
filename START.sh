#!/bin/bash

echo "================================================="
echo "🚀 Spring Boot Aufbau - Tag 6: Caching"
echo "================================================="
echo ""
echo "📦 Starte Maven Build..."
echo ""

mvn clean install

if [ $? -eq 0 ]; then
    echo ""
    echo "✅ Build erfolgreich!"
    echo ""
    echo "🏃 Starte Anwendung..."
    echo ""
    mvn spring-boot:run
else
    echo ""
    echo "❌ Build fehlgeschlagen!"
    echo "Bitte prüfe die Fehlermeldungen oben."
    exit 1
fi
