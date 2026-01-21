# Changelog

## Version 1.0.2 (Kompatibilitäts-Fix)

### 🔧 Kritischer Fix: Kotlin/Gradle Kompatibilität

**Problem behoben:**
- `NoSuchMethodError` bei Kapt-Plugin durch Kotlin 1.9.0 + Gradle 8.0 Inkompatibilität

**Neue stabile Konfiguration:**
- Gradle auf 7.6.4 downgraded (stabiler mit Kotlin 1.8.x)
- Kotlin auf 1.8.22 (vollständig kompatibel mit Gradle 7.6.x)
- Android Gradle Plugin auf 7.4.2 (passend zu Gradle 7.6)
- Compose Compiler auf 1.4.8 (für Kotlin 1.8.22)

### ✅ Getestete Kompatibilitätsmatrix

Diese Versionen funktionieren garantiert zusammen:

| Komponente | Version |
|------------|---------|
| **Gradle** | **7.6.4** |
| **Android Gradle Plugin** | **7.4.2** |
| **Kotlin** | **1.8.22** |
| **Compose Compiler** | **1.4.8** |
| **JDK** | **17** |
| **Min SDK** | **24** |
| **Target SDK** | **34** |

### 📝 Änderungen

**gradle-wrapper.properties:**
```
gradle-7.6.4-bin.zip (von 8.0)
```

**build.gradle.kts (Project):**
- AGP: 7.4.2 (von 8.1.4)
- Kotlin: 1.8.22 (von 1.9.0)

**app/build.gradle.kts:**
- Compose Compiler: 1.4.8 (von 1.5.1)

---

## Version 1.0.1 (Bugfix Release)

### 🔧 Behobene Probleme

**Gradle Kompatibilität:**
- Gradle-Version auf 8.0 aktualisiert (stabiler)
- Android Gradle Plugin auf 8.1.4 aktualisiert
- Kotlin-Version auf 1.9.0 vereinheitlicht
- Compose Compiler Extension auf 1.5.1 angepasst
- Alle Dependencies auf kompatible Versionen aktualisiert

**Build-System:**
- gradle-wrapper.properties hinzugefügt mit Gradle 8.0
- Plugin-Versionen in build.gradle.kts harmonisiert
- Serialization-Plugin korrekt konfiguriert

### 📁 Neue Dateien

- `TROUBLESHOOTING.md` - Umfassender Troubleshooting-Guide
- `setup.sh` - Automatisches Setup-Script für macOS/Linux
- `setup.bat` - Automatisches Setup-Script für Windows
- `gradle/wrapper/gradle-wrapper.properties` - Gradle-Wrapper-Konfiguration

### 🔄 Aktualisierte Dependencies

| Dependency | Alt | Neu |
|------------|-----|-----|
| Android Gradle Plugin | 8.2.0 | 8.1.4 |
| Kotlin | 1.9.20 | 1.9.0 |
| Compose Compiler | 1.5.4 | 1.5.1 |
| Gradle | - | 8.0 |
| Lifecycle | 2.7.0 | 2.6.2 |
| Activity Compose | 1.8.2 | 1.8.0 |
| Navigation | 2.7.6 | 2.7.5 |
| OkHttp | 4.12.0 | 4.11.0 |
| Kotlinx Serialization | 1.6.2 | 1.6.0 |
| Coil | 2.5.0 | 2.4.0 |
| Room | 2.6.1 | 2.6.0 |
| Jsoup | 1.17.2 | 1.16.1 |

### ✅ Kompatibilitätsmatrix

Diese Versionen sind getestet und funktionieren zusammen:

- **Gradle:** 8.0
- **Android Gradle Plugin:** 8.1.4
- **Kotlin:** 1.9.0
- **Compose Compiler:** 1.5.1
- **JDK:** 17
- **Min SDK:** 24 (Android 7.0)
- **Target SDK:** 34 (Android 14)

### 🚀 Quick Start

**macOS/Linux:**
```bash
chmod +x setup.sh
./setup.sh
```

**Windows:**
```cmd
setup.bat
```

### 📝 Weitere Verbesserungen

- Ausführliche Fehlerbehebungsanleitung hinzugefügt
- Setup-Scripte für automatische Konfiguration
- Verbesserte Dokumentation für häufige Probleme
- Kompatibilitätsmatrix für verschiedene Android Studio Versionen

---

## Version 1.0.0 (Initial Release)

### ✨ Features

- WordPress REST API Integration
- Offline-Caching mit Room Database
- Hell/Dunkel-Modus
- Sportliches Design in Vereinsfarben
- 3 Hauptbereiche: Blog, Verein, Training
- Bottom Navigation
- Pull-to-Refresh Funktionalität
- Bildanzeige mit Coil
- HTML-Content-Rendering in WebView
- MVVM-Architektur mit Jetpack Compose
- Hilt Dependency Injection
- Material Design 3

### 📱 Technischer Stack

- Kotlin
- Jetpack Compose
- Retrofit + OkHttp
- Room Database
- Hilt DI
- Coil Image Loading
- Material Design 3

### 📚 Dokumentation

- README.md mit Projektübersicht
- INSTALLATION.md mit Setup-Anleitung
- APP_ICON_GUIDE.md für Icon-Erstellung
