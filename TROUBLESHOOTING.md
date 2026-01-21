# Troubleshooting Guide

## Häufige Probleme und Lösungen

### 1. Gradle Wrapper fehlt / "gradle-wrapper.jar not found"

**Problem:**
```
Could not find gradle-wrapper.jar
```

**Ursache:** Die gradle-wrapper.jar Datei fehlt im Projekt

**Lösung:**

#### Automatisch (Empfohlen - Einfachste Methode):
1. **Öffnen Sie das Projekt in Android Studio**
2. Android Studio erkennt automatisch den fehlenden Wrapper
3. Klicken Sie auf **"OK"** wenn gefragt wird, ob Gradle Wrapper erstellt werden soll
4. Android Studio lädt alles automatisch herunter
5. Warten Sie auf "Gradle sync finished"

**Das ist der einfachste Weg! Android Studio macht alles automatisch.**

#### Manuell (falls automatisch nicht funktioniert):

**Option A:** Gradle installieren und Wrapper generieren
```bash
# Gradle installieren (falls noch nicht vorhanden)
# Dann im Projekt-Verzeichnis:
gradle wrapper --gradle-version=8.0
```

**Option B:** Wrapper-Dateien manuell herunterladen
1. Laden Sie gradle-wrapper.jar herunter von:
   https://repo1.maven.org/maven2/org/gradle/gradle-wrapper/8.0/gradle-wrapper-8.0.jar
2. Speichern Sie die Datei in: `gradle/wrapper/gradle-wrapper.jar`
3. Sync in Android Studio: File → Sync Project with Gradle Files

### 2. Gradle Sync Fehler: "Failed to notify project evaluation listener"

**Problem:**
```
Failed to notify project evaluation listener.
'org.gradle.api.file.FileCollection org.gradle.api.artifacts.Configuration.fileCollection'
```

**Ursache:** Inkompatibilität zwischen Gradle-Version, Android Gradle Plugin und Kotlin-Version

**Lösung:**

#### Option A: Gradle Wrapper aktualisieren (Empfohlen)
```bash
# Im Projekt-Verzeichnis
./gradlew wrapper --gradle-version=8.0
```

#### Option B: Manuelle Anpassung
1. Öffnen Sie `gradle/wrapper/gradle-wrapper.properties`
2. Ändern Sie die distributionUrl:
   ```
   distributionUrl=https\://services.gradle.org/distributions/gradle-8.0-bin.zip
   ```

#### Option C: In Android Studio
1. File → Settings → Build, Execution, Deployment → Build Tools → Gradle
2. Gradle JDK: Wählen Sie JDK 17
3. Use Gradle from: 'gradle-wrapper.properties file'

### 2. Kompatibilitätsmatrix

Stellen Sie sicher, dass diese Versionen zusammenpassen:

| Komponente | Version |
|------------|---------|
| Gradle | 7.6.4 |
| Android Gradle Plugin | 7.4.2 |
| Kotlin | 1.8.22 |
| Compose Compiler | 1.4.8 |
| JDK | 17 |

### 3. "Unsupported class file major version"

**Lösung:**
```bash
# Prüfen Sie Ihre Java-Version
java -version

# Sollte Java 17 sein
# Falls nicht, installieren Sie JDK 17 und setzen Sie JAVA_HOME
```

**In Android Studio:**
1. File → Project Structure → SDK Location
2. JDK location: Wählen Sie JDK 17

### 4. Dependency Resolution Fehler

**Problem:** "Could not resolve all dependencies"

**Lösung:**

#### Schritt 1: Cache leeren
```bash
./gradlew clean
./gradlew --stop
```

#### Schritt 2: In Android Studio
```
File → Invalidate Caches → Invalidate and Restart
```

#### Schritt 3: Gradle Cache manuell löschen
```bash
# Windows
del /s /q %USERPROFILE%\.gradle\caches

# macOS/Linux
rm -rf ~/.gradle/caches
```

### 5. "Plugin [id: 'com.android.application'] was not found"

**Lösung:**

1. Überprüfen Sie `settings.gradle.kts`:
```kotlin
pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
```

2. Führen Sie Sync erneut durch

### 6. Hilt/Dagger Kompilierungsfehler

**Problem:** "Hilt processor error" oder "@HiltAndroidApp not found"

**Lösung:**

1. Stellen Sie sicher, dass kapt aktiviert ist in `app/build.gradle.kts`:
```kotlin
plugins {
    // ...
    kotlin("kapt")
}
```

2. Rebuild durchführen:
```
Build → Clean Project
Build → Rebuild Project
```

### 7. Room Database Fehler

**Problem:** "Cannot find implementation for database"

**Lösung:**

1. Rebuild durchführen (Room generiert Code zur Compile-Zeit)
2. Prüfen Sie, ob kapt korrekt konfiguriert ist
3. Stellen Sie sicher, dass alle `@Dao`, `@Entity` und `@Database` Annotationen vorhanden sind

### 8. Compose Preview funktioniert nicht

**Lösung:**

1. Invalidate Caches: `File → Invalidate Caches → Invalidate and Restart`
2. Stellen Sie sicher, dass die Compose Version mit Kotlin übereinstimmt
3. Rebuild: `Build → Rebuild Project`

### 9. App stürzt beim Start ab

**Debugging-Schritte:**

1. **Logcat öffnen:**
   - View → Tool Windows → Logcat
   - Filter auf "Error" setzen

2. **Häufige Fehler:**
   - **NetworkOnMainThreadException**: Netzwerk-Call im Main Thread
   - **ClassNotFoundException**: Dependency fehlt oder Proguard-Regeln fehlen
   - **NullPointerException**: Uninitialisierte Variable

3. **Hilt-spezifische Fehler:**
   - Stellen Sie sicher, dass `@HiltAndroidApp` in der Application-Klasse ist
   - Prüfen Sie, ob alle ViewModels mit `@HiltViewModel` annotiert sind
   - MainActivity muss mit `@AndroidEntryPoint` annotiert sein

### 10. Netzwerkfehler / API-Zugriff

**Problem:** "UnknownHostException" oder "Unable to resolve host"

**Lösung:**

1. **Internetverbindung prüfen:**
   - Emulator: Prüfen Sie, ob der Emulator Internet hat
   - Gerät: WLAN/Mobile Daten aktiviert?

2. **AndroidManifest.xml prüfen:**
```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```

3. **Cleartex Traffic (nur für Entwicklung):**
```xml
<application
    android:usesCleartextTraffic="true"
    ...>
```

**Hinweis:** Für Produktion sollte `usesCleartextTraffic="false"` sein und HTTPS verwendet werden.

### 11. Build dauert sehr lange

**Optimierungen:**

1. **gradle.properties anpassen:**
```properties
org.gradle.jvmargs=-Xmx4096m -XX:+HeapDumpOnOutOfMemoryError -Dfile.encoding=UTF-8
org.gradle.parallel=true
org.gradle.caching=true
org.gradle.configureondemand=true
```

2. **Build Cache aktivieren:**
```bash
./gradlew --build-cache
```

### 12. "Duplicate class" Fehler

**Ursache:** Gleiche Klasse wird von mehreren Dependencies bereitgestellt

**Lösung:**

1. Prüfen Sie Dependencies:
```bash
./gradlew app:dependencies
```

2. Excludieren Sie doppelte Dependencies:
```kotlin
implementation("some.library:name:version") {
    exclude(group = "duplicate.group", module = "duplicate-module")
}
```

### 13. Kotlin Version Konflikt

**Problem:** "Incompatible Kotlin version"

**Lösung:**

Stellen Sie sicher, dass alle Kotlin-Plugins die gleiche Version verwenden:
```kotlin
// In build.gradle.kts (Project-level)
plugins {
    id("org.jetbrains.kotlin.android") version "1.9.0" apply false
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.0" apply false
}
```

### 14. Emulator startet nicht

**Lösungen:**

1. **Virtualisierung aktivieren:**
   - BIOS öffnen
   - Intel VT-x oder AMD-V aktivieren

2. **Hyper-V deaktivieren (Windows):**
   ```
   Systemsteuerung → Programme → Windows-Features
   Hyper-V deaktivieren → Neustart
   ```

3. **HAXM installieren (Intel):**
   - SDK Manager → SDK Tools
   - Intel x86 Emulator Accelerator (HAXM) installieren

### 15. Speicherfehler beim Build

**Problem:** "Out of memory" oder "Java heap space"

**Lösung:**

1. **Heap Size erhöhen:**
```properties
# gradle.properties
org.gradle.jvmargs=-Xmx4096m
```

2. **Andere Anwendungen schließen**

3. **Build einzeln durchführen:**
```bash
./gradlew clean
./gradlew assembleDebug
```

## Allgemeine Tipps

### Vor jedem Build
1. Internetverbindung prüfen
2. Gradle Daemon läuft: `./gradlew --status`
3. Genügend Speicher verfügbar

### Bei hartnäckigen Problemen
```bash
# Alles zurücksetzen
./gradlew clean
./gradlew --stop
rm -rf .gradle
rm -rf build
rm -rf app/build

# In Android Studio
File → Invalidate Caches → Invalidate and Restart

# Erneut versuchen
./gradlew assembleDebug
```

### Debug-Optionen

```bash
# Ausführliche Ausgabe
./gradlew assembleDebug --info

# Noch mehr Details
./gradlew assembleDebug --debug

# Stack Trace anzeigen
./gradlew assembleDebug --stacktrace

# Vollständiger Scan
./gradlew assembleDebug --scan
```

## Weitere Hilfe

- **Android Developer Docs:** https://developer.android.com
- **Stack Overflow:** Tag `android` + spezifischer Fehler
- **Gradle Docs:** https://docs.gradle.org
- **Kotlin Docs:** https://kotlinlang.org/docs/home.html

## Versionsspezifische Probleme

Falls Sie eine andere Android Studio Version verwenden:

| Android Studio | AGP Version | Gradle Version |
|----------------|-------------|----------------|
| Hedgehog | 8.1.x | 8.0 |
| Giraffe | 8.0.x | 8.0 |
| Flamingo | 8.0.x | 8.0 |
| Electric Eel | 7.4.x | 7.5 |

Passen Sie die Versionen in `build.gradle.kts` entsprechend an.
