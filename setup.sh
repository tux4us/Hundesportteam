#!/bin/bash

echo "======================================"
echo "Hundesportteam App - Quick Setup"
echo "======================================"
echo ""

# Prüfe ob Java installiert ist
if command -v java &> /dev/null; then
    JAVA_VERSION=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}' | cut -d'.' -f1)
    echo "✓ Java gefunden: Version $JAVA_VERSION"
    
    if [ "$JAVA_VERSION" -lt "17" ]; then
        echo "⚠️  Warnung: Java 17 oder höher wird empfohlen!"
        echo "   Aktuelle Version: $JAVA_VERSION"
    fi
else
    echo "✗ Java nicht gefunden!"
    echo "  Bitte installieren Sie JDK 17 oder höher"
    exit 1
fi

echo ""
echo "Schritt 1: Gradle Cache leeren..."
rm -rf .gradle
rm -rf build
rm -rf app/build
echo "✓ Cache geleert"

echo ""
echo "Schritt 2: Gradle Wrapper initialisieren..."
chmod +x gradlew
./gradlew wrapper --gradle-version=7.6.4
echo "✓ Gradle Wrapper aktualisiert"

echo ""
echo "Schritt 3: Dependencies herunterladen..."
./gradlew build --no-daemon --refresh-dependencies || {
    echo "⚠️  Build mit Fehlern. Versuche Clean Build..."
    ./gradlew clean
    ./gradlew build --no-daemon
}

echo ""
echo "======================================"
echo "Setup abgeschlossen!"
echo "======================================"
echo ""
echo "Nächste Schritte:"
echo "1. Öffnen Sie Android Studio"
echo "2. File → Open → Wählen Sie diesen Ordner"
echo "3. Warten Sie auf Gradle Sync"
echo "4. Klicken Sie auf 'Run' ▶️"
echo ""
echo "Bei Problemen siehe TROUBLESHOOTING.md"
echo ""
