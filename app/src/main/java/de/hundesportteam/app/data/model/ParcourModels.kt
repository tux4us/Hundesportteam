package de.hundesportteam.app.data.model

import kotlinx.serialization.Serializable

/**
 * Kategorie eines Katalog-Elements: PUNKT = stationäre Aktion (Sitz, Platz, ...),
 * LINIE = Bewegungsart zwischen zwei Wegpunkten (Normalschritt, Slalom, Bogen, ...).
 */
enum class ElementCategory {
    PUNKT,
    LINIE
}

/**
 * Geometrie eines LINIE-Elements. GERADE = einfache Strecke zwischen Start- und
 * Endwegpunkt. BOGEN = Kreisbogen (inkl. Vollkreis als Sonderfall sweepAngle = 360°),
 * bestimmt durch Radius, Richtung und Bogenwinkel statt durch einen frei wählbaren Endpunkt.
 */
enum class LineGeometry {
    GERADE,
    BOGEN
}

/** Drehrichtung eines Bogens/Kreises, von oben auf das Feld geschaut. */
enum class CurveDirection {
    RECHTS, // im Uhrzeigersinn
    LINKS   // gegen den Uhrzeigersinn
}

/** Strichart für GERADE-Linien (rein optisch, zur Unterscheidung im Plan). */
enum class LineStrokeStyle {
    SOLID,
    SOLID_THIN,
    SOLID_THICK,
    DASHED
}

/**
 * Ein Segment beschreibt den Übergang VOM vorherigen Wegpunkt ZU diesem Wegpunkt.
 * Verweist auf eine LINIE-Vorlage in ElementTemplateEntity. Bei geometryType == BOGEN
 * dort wird radius/direction/sweepAngle pro Instanz gespeichert (kann vom Vorlagen-
 * Standardwert abweichen, z. B. individueller Kreisradius).
 */
@Serializable
data class Segment(
    val templateId: Long,
    val radius: Double? = null,           // nur bei BOGEN
    val direction: CurveDirection? = null, // nur bei BOGEN
    val sweepAngle: Int? = null            // nur bei BOGEN: 90, 180, 270 oder 360
)

/**
 * Ein Wegpunkt der Parcour-Kette. x/y in Metern, Ursprung (0,0) = linke obere Ecke
 * der Übungsfläche. incomingSegment ist null beim allerersten Wegpunkt.
 */
@Serializable
data class Waypoint(
    val id: String,
    val x: Double,
    val y: Double,
    val actionTemplateId: Long? = null, // verweist auf eine PUNKT-Vorlage
    val note: String? = null,           // freier Zusatztext, unabhängig vom Vorlagentyp
    val incomingSegment: Segment? = null
)

/**
 * Vollständiger Inhalt eines Parcours, wird als JSON in ParcourEntity.dataJson abgelegt.
 * initialHeadingDegrees: Blickrichtung am Start (0° = Richtung +y/"oben" auf der Fläche,
 * im Uhrzeigersinn steigend) – nur relevant, falls das allererste Segment ein BOGEN ist.
 */
@Serializable
data class ParcourData(
    val initialHeadingDegrees: Double = 0.0,
    val waypoints: List<Waypoint> = emptyList()
)
