@echo off
echo ======================================
echo Hundesportteam App - Quick Setup
echo ======================================
echo.

REM Prüfe ob Java installiert ist
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo X Java nicht gefunden!
    echo   Bitte installieren Sie JDK 17 oder hoeher
    pause
    exit /b 1
)

echo + Java gefunden
echo.

echo Schritt 1: Gradle Cache leeren...
if exist .gradle rmdir /s /q .gradle
if exist build rmdir /s /q build
if exist app\build rmdir /s /q app\build
echo + Cache geleert
echo.

echo Schritt 2: Gradle Wrapper initialisieren...
gradlew.bat wrapper --gradle-version=7.6.4
echo + Gradle Wrapper aktualisiert
echo.

echo Schritt 3: Dependencies herunterladen...
gradlew.bat build --no-daemon --refresh-dependencies
if %errorlevel% neq 0 (
    echo ! Build mit Fehlern. Versuche Clean Build...
    gradlew.bat clean
    gradlew.bat build --no-daemon
)

echo.
echo ======================================
echo Setup abgeschlossen!
echo ======================================
echo.
echo Naechste Schritte:
echo 1. Oeffnen Sie Android Studio
echo 2. File - Open - Waehlen Sie diesen Ordner
echo 3. Warten Sie auf Gradle Sync
echo 4. Klicken Sie auf 'Run' (Spielbutton)
echo.
echo Bei Problemen siehe TROUBLESHOOTING.md
echo.
pause
