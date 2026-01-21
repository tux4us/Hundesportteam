# Installations- und Setup-Anleitung

## Systemvoraussetzungen

### Entwicklungsumgebung
- **Android Studio**: Hedgehog (2023.1.1) oder neuer
- **Java Development Kit (JDK)**: Version 17
- **Betriebssystem**: Windows 10/11, macOS 10.14+, oder Linux
- **RAM**: Mindestens 8 GB (16 GB empfohlen)
- **Festplattenspeicher**: Mindestens 10 GB frei

### Android-Gerät / Emulator
- **Mindest-API Level**: 24 (Android 7.0 Nougat)
- **Empfohlenes API Level**: 34 (Android 14)
- **Internetverbindung**: Erforderlich für ersten Datenabr uf

## Installation

### 1. Android Studio installieren

1. Laden Sie Android Studio herunter: https://developer.android.com/studio
2. Führen Sie das Installationsprogramm aus
3. Folgen Sie dem Setup-Assistenten
4. Stellen Sie sicher, dass folgende Komponenten installiert sind:
   - Android SDK
   - Android SDK Platform (API 34)
   - Android SDK Build-Tools
   - Android Emulator (optional, für Tests ohne physisches Gerät)

### 2. JDK 17 konfigurieren

Android Studio sollte automatisch ein JDK mitbringen. Falls nicht:

**Windows:**
```
1. Laden Sie JDK 17 herunter (z.B. von Oracle oder OpenJDK)
2. Installieren Sie es
3. Setzen Sie JAVA_HOME in den Umgebungsvariablen
4. Fügen Sie %JAVA_HOME%\bin zu PATH hinzu
```

**macOS/Linux:**
```bash
# Installation mit Homebrew (macOS)
brew install openjdk@17

# Oder mit SDKMAN (macOS/Linux)
sdk install java 17.0.9-tem
```

### 3. Projekt in Android Studio öffnen

1. Starten Sie Android Studio
2. Klicken Sie auf **"Open"** oder **"File > Open"**
3. Navigieren Sie zum Projekt-Ordner `HundesportteamApp`
4. Klicken Sie auf **"OK"**
5. Android Studio beginnt automatisch mit dem Gradle Sync

### 4. Gradle Sync & Dependencies

Der erste Gradle Sync kann einige Minuten dauern:
- Android Studio lädt alle Dependencies herunter
- Dies benötigt eine stabile Internetverbindung
- Bei Fehlern: **"File > Invalidate Caches / Restart"**

### 5. Android SDK konfigurieren

Stellen Sie sicher, dass SDK API Level 34 installiert ist:

1. **"Tools > SDK Manager"** öffnen
2. Tab **"SDK Platforms"** auswählen
3. **"Android 14.0 (API 34)"** anhaken
4. Tab **"SDK Tools"** auswählen
5. Folgende Tools installieren:
   - Android SDK Build-Tools 34
   - Android Emulator
   - Android SDK Platform-Tools
6. **"Apply"** klicken und Installation abwarten

## App ausführen

### Option 1: Auf physischem Android-Gerät

1. **USB-Debugging aktivieren** auf Ihrem Android-Gerät:
   ```
   Einstellungen > Über das Telefon > 7x auf "Build-Nummer" tippen
   Einstellungen > Entwickleroptionen > USB-Debugging aktivieren
   ```

2. Gerät per USB mit dem Computer verbinden

3. Wenn gefragt, USB-Debugging-Verbindung auf dem Gerät bestätigen

4. In Android Studio: Gerät im Dropdown oben auswählen

5. Auf den **grünen "Run"**-Button klicken (oder Shift+F10)

### Option 2: Im Android Emulator

1. **"Tools > Device Manager"** öffnen

2. **"Create Device"** klicken

3. Ein Gerät auswählen (z.B. "Pixel 7") und **"Next"**

4. System Image auswählen:
   - **"API Level 34"** (Android 14) empfohlen
   - Falls nicht heruntergeladen: auf Download-Link klicken
   - **"Next"** und **"Finish"**

5. Emulator aus der Liste starten

6. Im Dropdown oben den Emulator auswählen

7. Auf den **grünen "Run"**-Button klicken

## Build-Varianten

### Debug Build (Entwicklung)
```
Run > Run 'app'
oder
./gradlew assembleDebug
```
- Schneller Build-Prozess
- Debugging aktiviert
- Nicht optimiert

### Release Build (Produktion)
```
Build > Generate Signed Bundle / APK
oder
./gradlew assembleRelease
```
- Optimiert und minimiert
- Erfordert Signing-Konfiguration
- Für Veröffentlichung im Play Store

## Signing-Konfiguration (für Release Build)

### Keystore erstellen

```bash
keytool -genkey -v -keystore hundesportteam.keystore -alias hundesportteam -keyalg RSA -keysize 2048 -validity 10000
```

### In build.gradle.kts konfigurieren

```kotlin
android {
    signingConfigs {
        create("release") {
            storeFile = file("path/to/hundesportteam.keystore")
            storePassword = "your-store-password"
            keyAlias = "hundesportteam"
            keyPassword = "your-key-password"
        }
    }
    
    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("release")
            // ... rest der Konfiguration
        }
    }
}
```

**Sicherheitshinweis:** Niemals Passwörter in Git committen!

## Troubleshooting

### Gradle Sync fehlgeschlagen
```
Lösung:
1. File > Invalidate Caches / Restart
2. Internetverbindung prüfen
3. Gradle-Version in gradle-wrapper.properties prüfen
4. ./gradlew clean im Terminal ausführen
```

### "SDK Location not found"
```
Lösung:
1. SDK Manager öffnen und SDK-Pfad prüfen
2. local.properties erstellen mit:
   sdk.dir=/path/to/android/sdk
```

### Build-Fehler: "Duplicate class"
```
Lösung:
1. Build > Clean Project
2. Build > Rebuild Project
3. Cache leeren: File > Invalidate Caches / Restart
```

### App startet nicht / stürzt ab
```
Lösung:
1. Logcat in Android Studio überprüfen (View > Tool Windows > Logcat)
2. Filter auf "Error" setzen
3. Fehler identifizieren und beheben
4. Bei Netzwerkfehlern: Internet-Verbindung prüfen
```

### Emulator startet nicht
```
Lösung:
1. Virtualisierung im BIOS aktivieren (Intel VT-x / AMD-V)
2. Hyper-V deaktivieren (Windows)
3. Emulator neu erstellen mit anderem System Image
4. HAXM installieren (Intel-basierte Systeme)
```

## Erste Schritte nach Installation

1. **App starten** - Beim ersten Start werden Daten von der Website geladen

2. **Daten aktualisieren** - Refresh-Button oder Pull-to-Refresh nutzen

3. **Offline-Modus testen** - Internet ausschalten und prüfen, ob gecachte Daten angezeigt werden

4. **Dark Mode testen** - Theme-Button in der App nutzen

5. **Navigation testen** - Zwischen Blog, Verein und Training wechseln

## Tipps für Entwickler

### Logcat nutzen
```
- Filter auf Package Name setzen: de.hundesportteam.app
- Severity auf "Debug" oder "Info" für mehr Details
- Tags nutzen für spezifische Komponenten
```

### Android Profiler
```
View > Tool Windows > Profiler
- CPU-Nutzung überwachen
- Speicher-Leaks identifizieren
- Netzwerk-Requests analysieren
```

### Layout Inspector
```
Tools > Layout Inspector
- UI-Hierarchie visualisieren
- View-Eigenschaften inspizieren
- Performance-Probleme identifizieren
```

## Nächste Schritte

Nach erfolgreicher Installation:

1. **Code erkunden** - Projekt-Struktur in README.md verstehen
2. **Anpassungen vornehmen** - Farben, Texte, Features
3. **Testen** - Verschiedene Geräte und Android-Versionen
4. **App-Icon erstellen** - Siehe APP_ICON_GUIDE.md
5. **Release vorbereiten** - Signing konfigurieren

## Support

Bei Problemen:
1. Android Studio Logs prüfen
2. Gradle Build Output lesen
3. Stack Overflow durchsuchen
4. Android Developer Dokumentation konsultieren: https://developer.android.com

---

**Viel Erfolg mit der Hundesportteam App! 🐕**
