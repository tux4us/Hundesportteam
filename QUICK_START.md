# Quick Start Guide - Hundesportteam App

## ⚡ Schnellstart (Empfohlen für Anfänger)

### Schritt 1: Projekt in Android Studio öffnen

1. **Android Studio starten**
2. Klicken Sie auf **"Open"** (oder **"File → Open"**)
3. Navigieren Sie zum Ordner **"HundesportteamApp"**
4. Klicken Sie auf **"OK"**

### Schritt 2: Gradle Wrapper automatisch erstellen lassen

Beim ersten Öffnen wird Android Studio:
- ✅ Automatisch den Gradle Wrapper einrichten
- ✅ Alle Dependencies herunterladen
- ✅ Das Projekt konfigurieren

**Das kann 5-10 Minuten dauern beim ersten Mal!**

### Schritt 3: Warten auf "Gradle Sync"

In der Statusleiste unten sehen Sie:
```
Gradle sync in progress...
```

**Warten Sie, bis steht:**
```
Gradle sync finished
```

### Schritt 4: JDK 17 einstellen (falls nötig)

Falls Sie eine Fehlermeldung bezüglich JDK sehen:

1. **File → Project Structure**
2. Links: **"SDK Location"**
3. **"Gradle Settings"** klicken
4. **"Gradle JDK"**: Wählen Sie **JDK 17** (oder **"Download JDK..."** wenn nicht vorhanden)
5. **"OK"** klicken
6. Warten Sie erneut auf Gradle Sync

### Schritt 5: App ausführen

1. Wählen Sie ein Gerät aus:
   - **Physisches Gerät** (USB-Debugging aktiviert)
   - Oder **"Device Manager → Create Device"** für einen Emulator

2. Klicken Sie auf den **grünen "Run" Button** ▶️

3. Die App wird gebaut und gestartet!

---

## 🔧 Falls Probleme auftreten

### Problem: "Gradle sync failed"

**Lösung 1: Cache leeren**
```
File → Invalidate Caches → Invalidate and Restart
```

**Lösung 2: JDK prüfen**
```
File → Settings → Build, Execution, Deployment → Build Tools → Gradle
Gradle JDK: JDK 17 auswählen
```

**Lösung 3: Gradle Version manuell setzen**
1. Öffnen Sie `gradle/wrapper/gradle-wrapper.properties`
2. Prüfen Sie, ob diese Zeile vorhanden ist:
   ```
   distributionUrl=https\://services.gradle.org/distributions/gradle-8.0-bin.zip
   ```
3. Falls nicht, fügen Sie sie hinzu
4. File → Sync Project with Gradle Files

### Problem: "SDK not found"

**Lösung:**
1. **File → Project Structure → SDK Location**
2. Prüfen Sie den **Android SDK Location** Pfad
3. Falls leer: Klicken Sie auf **"Edit"** und wählen Sie Ihren SDK-Pfad
4. Standard-Pfade:
   - **Windows:** `C:\Users\[IhrName]\AppData\Local\Android\Sdk`
   - **macOS:** `~/Library/Android/sdk`
   - **Linux:** `~/Android/Sdk`

### Problem: Build dauert sehr lange

**Normal!** Der erste Build kann 10-15 Minuten dauern:
- Gradle lädt alle Dependencies herunter (~500 MB)
- Projekt wird kompiliert
- Caches werden erstellt

**Nachfolgende Builds sind viel schneller** (30 Sekunden - 2 Minuten)

---

## 📱 Emulator erstellen (falls kein Gerät)

1. **Tools → Device Manager**
2. **"Create Device"** klicken
3. Wählen Sie ein Gerät (z.B. **"Pixel 7"**)
4. **"Next"** klicken
5. System Image wählen:
   - **"Tiramisu"** (API Level 33) oder
   - **"UpsideDownCake"** (API Level 34)
   - Falls nicht heruntergeladen: auf **Download** klicken
6. **"Next"** → **"Finish"**
7. Emulator starten mit dem **Play-Button** ▶️

---

## ✅ Checkliste vor dem Start

- [ ] Android Studio installiert (Hedgehog oder neuer)
- [ ] JDK 17 installiert
- [ ] Internetverbindung aktiv (für ersten Download)
- [ ] Mindestens 10 GB freier Speicherplatz
- [ ] 8 GB RAM (16 GB empfohlen)

---

## 🎯 Erwartetes Verhalten beim ersten Start

### In Android Studio:

1. **Gradle Wrapper wird eingerichtet** (1-2 Min.)
2. **Dependencies werden heruntergeladen** (5-8 Min.)
3. **Projekt wird gebaut** (2-3 Min.)
4. **App wird auf Gerät installiert** (30 Sek.)
5. **App startet** ✅

### In der App:

1. **Ladebildschirm** erscheint
2. **Daten werden von Website geladen** (5-10 Sek.)
3. **Blog-Tab** wird angezeigt mit Beiträgen
4. Sie können zwischen **Blog**, **Verein** und **Training** wechseln

---

## 🆘 Weitere Hilfe

Wenn nichts funktioniert:

1. **TROUBLESHOOTING.md** lesen - dort sind alle Lösungen!
2. Android Studio Logs prüfen:
   - **View → Tool Windows → Build**
   - **View → Tool Windows → Logcat**
3. Google den genauen Fehler + "Android Studio"

---

## 💡 Tipps

**Tipp 1:** Beim ersten Mal dauert alles länger - Geduld! ☕

**Tipp 2:** Lassen Sie Android Studio die Arbeit machen - es konfiguriert automatisch!

**Tipp 3:** Bei Fehlern: Erstmal "Invalidate Caches" versuchen!

**Tipp 4:** Schauen Sie in die **Build-Ausgabe** unten für Fehlermeldungen!

---

## 🎉 Geschafft!

Wenn die App läuft, haben Sie es geschafft! 

Die App sollte jetzt:
- ✅ Blog-Beiträge anzeigen
- ✅ Vereinsseiten auflisten
- ✅ Trainingsseiten darstellen
- ✅ Offline funktionieren (nach erstem Laden)
- ✅ Hell/Dunkel-Modus unterstützen

---

**Viel Erfolg! 🐕**

Bei Fragen: Siehe **TROUBLESHOOTING.md** oder **INSTALLATION.md**
