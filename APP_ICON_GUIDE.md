# App Icon Guide

## Design-Konzept

Das App-Icon basiert auf dem Logo von https://hundesportteam.de/wp-content/uploads/2020/03/cropped-Hundesportteam_Icon_Face_Doghead_Landscape-scaled-1.jpg

### Vereinfachtes Icon-Design

Da ich keinen direkten Zugriff auf Bildbearbeitungssoftware habe, hier die Anleitung zur Erstellung des Icons:

## Schritt-für-Schritt Anleitung

### 1. Ausgangsbild vorbereiten
- Originalbild herunterladen von der Website
- Quadratisches Format erstellen (1024x1024 px für Android)
- Fokus auf den Hundekopf legen

### 2. Vereinfachen
- **Entfernen**: Alle Textelemente ("HUNDESPORTTEAM", "WIEHL")
- **Beibehalten**: Nur den stilisierten Hundekopf
- **Farben anpassen**: 
  - Hauptfarbe: Gold (#FFD48B)
  - Hintergrund: Dunkelgrün (#003A00)
  - Kontrast maximieren für bessere Sichtbarkeit auf kleinen Bildschirmen

### 3. Optimierungen für verschiedene Größen

#### Adaptive Icons (Android 8.0+)
Erstellen Sie zwei Versionen:

**Vordergrund (foreground):**
- 108x108 dp mit 66x66 dp sicherem Bereich
- Der Hundekopf sollte zentriert im sicheren Bereich sein
- Transparenter Hintergrund

**Hintergrund (background):**
- 108x108 dp
- Einfarbig: Dunkelgrün (#003A00)
- Oder subtiler Gradient

#### Legacy Icons (Android 7.1 und älter)
- 48x48 dp (mdpi)
- 72x72 dp (hdpi)
- 96x96 dp (xhdpi)
- 144x144 dp (xxhdpi)
- 192x192 dp (xxxhdpi)

### 4. Dateinamen und Speicherorte

```
app/src/main/res/
├── mipmap-mdpi/
│   ├── ic_launcher.png (48x48)
│   └── ic_launcher_round.png (48x48)
├── mipmap-hdpi/
│   ├── ic_launcher.png (72x72)
│   └── ic_launcher_round.png (72x72)
├── mipmap-xhdpi/
│   ├── ic_launcher.png (96x96)
│   └── ic_launcher_round.png (96x96)
├── mipmap-xxhdpi/
│   ├── ic_launcher.png (144x144)
│   └── ic_launcher_round.png (144x144)
├── mipmap-xxxhdpi/
│   ├── ic_launcher.png (192x192)
│   └── ic_launcher_round.png (192x192)
└── mipmap-anydpi-v26/
    ├── ic_launcher.xml
    └── ic_launcher_round.xml
```

### 5. XML-Definitionen für Adaptive Icons

**ic_launcher.xml:**
```xml
<?xml version="1.0" encoding="utf-8"?>
<adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android">
    <background android:drawable="@color/dark_green"/>
    <foreground android:drawable="@mipmap/ic_launcher_foreground"/>
</adaptive-icon>
```

**ic_launcher_round.xml:**
```xml
<?xml version="1.0" encoding="utf-8"?>
<adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android">
    <background android:drawable="@color/dark_green"/>
    <foreground android:drawable="@mipmap/ic_launcher_foreground"/>
</adaptive-icon>
```

## Design-Tipps

### Farben
- Verwenden Sie hohen Kontrast (Gold auf Dunkelgrün)
- Vermeiden Sie zu viele Details - Icons sind klein
- Testen Sie auf verschiedenen Hintergründen (hell und dunkel)

### Form
- Der Hundekopf sollte erkennbar sein, auch bei 48x48 px
- Klare, einfache Linien
- Keine feinen Details, die bei kleinen Größen verloren gehen

### Stil
- Sportlich und dynamisch
- Modern und klar
- Wiedererkennbar

## Tools zur Icon-Erstellung

### Online-Tools
1. **Android Asset Studio** (https://romannurik.github.io/AndroidAssetStudio/)
   - Automatische Generierung aller Größen
   - Adaptive Icon Support
   - Kostenlos

2. **App Icon Generator** (https://appicon.co/)
   - Multi-Plattform
   - Einfach zu bedienen

### Desktop-Software
1. **Adobe Illustrator** / **Inkscape** (Vektorgrafik)
2. **Photoshop** / **GIMP** (Rastergrafik)
3. **Figma** / **Sketch** (UI Design)

## Testing

Nach der Erstellung:
1. Testen Sie das Icon auf verschiedenen Android-Versionen
2. Prüfen Sie die Sichtbarkeit auf verschiedenen Hintergründen
3. Stellen Sie sicher, dass es bei 48x48 px noch erkennbar ist
4. Testen Sie sowohl im Light als auch im Dark Mode

## Placeholder

Bis Sie Ihr eigenes Icon erstellen, nutzt die App die Standard-Android-Icons. Diese sollten so schnell wie möglich durch Ihr eigenes Branding ersetzt werden.

## Zusammenfassung

Vereinfachtes Icon = **Hundekopf in Gold (#FFD48B) auf dunkelgrünem Hintergrund (#003A00)**
- Ohne Text
- Klare Linien
- Hoher Kontrast
- Sportlich und modern
