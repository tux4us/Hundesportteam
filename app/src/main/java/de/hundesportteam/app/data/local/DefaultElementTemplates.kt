package de.hundesportteam.app.data.local

import de.hundesportteam.app.data.local.entity.ElementTemplateEntity
import de.hundesportteam.app.data.model.CurveDirection
import de.hundesportteam.app.data.model.ElementCategory
import de.hundesportteam.app.data.model.LineGeometry
import de.hundesportteam.app.data.model.LineStrokeStyle

// Vereinsfarben, siehe ui/theme/Color.kt (GoldLight / DarkGreenLight)
private const val GOLD = "#FFD48B"
private const val DARK_GREEN = "#003A00"

/**
 * Start-Katalog, mit dem element_templates beim ersten App-Start befüllt wird
 * (siehe ElementTemplateRepository.seedDefaultsIfEmpty()). Danach vom Nutzer in
 * der App frei erweiterbar/änderbar – das hier sind nur sinnvolle Vorgaben.
 */
fun defaultElementTemplates(): List<ElementTemplateEntity> = listOf(
    // --- Punkte (Aktionspunkte) ---
    ElementTemplateEntity(name = "Sitz", abbreviation = "S", category = ElementCategory.PUNKT, colorHex = GOLD),
    ElementTemplateEntity(name = "Platz", abbreviation = "P", category = ElementCategory.PUNKT, colorHex = GOLD),
    ElementTemplateEntity(name = "Steh", abbreviation = "St", category = ElementCategory.PUNKT, colorHex = GOLD),
    ElementTemplateEntity(name = "Rechtswinkel", abbreviation = "RW", category = ElementCategory.PUNKT, colorHex = GOLD),
    ElementTemplateEntity(name = "Linkswinkel", abbreviation = "LW", category = ElementCategory.PUNKT, colorHex = GOLD),
    ElementTemplateEntity(name = "Kehrtwendung", abbreviation = "KW", category = ElementCategory.PUNKT, colorHex = GOLD),
    ElementTemplateEntity(name = "Abrufen", abbreviation = "AR", category = ElementCategory.PUNKT, colorHex = GOLD),
    ElementTemplateEntity(name = "Voraus", abbreviation = "VR", category = ElementCategory.PUNKT, colorHex = GOLD),

    // --- Linien: gerade Strecken ---
    ElementTemplateEntity(
        name = "Normalschritt", abbreviation = "N", category = ElementCategory.LINIE, colorHex = DARK_GREEN,
        lineGeometry = LineGeometry.GERADE, strokeStyle = LineStrokeStyle.SOLID
    ),
    ElementTemplateEntity(
        name = "Langsames Tempo", abbreviation = "LT", category = ElementCategory.LINIE, colorHex = DARK_GREEN,
        lineGeometry = LineGeometry.GERADE, strokeStyle = LineStrokeStyle.SOLID_THIN
    ),
    ElementTemplateEntity(
        name = "Laufschritt", abbreviation = "L", category = ElementCategory.LINIE, colorHex = DARK_GREEN,
        lineGeometry = LineGeometry.GERADE, strokeStyle = LineStrokeStyle.SOLID_THICK
    ),
    ElementTemplateEntity(
        name = "Slalom", abbreviation = "Sl", category = ElementCategory.LINIE, colorHex = DARK_GREEN,
        lineGeometry = LineGeometry.GERADE, strokeStyle = LineStrokeStyle.DASHED
    ),
    ElementTemplateEntity(
        name = "Freifolge", abbreviation = "FF", category = ElementCategory.LINIE, colorHex = DARK_GREEN,
        lineGeometry = LineGeometry.GERADE, strokeStyle = LineStrokeStyle.SOLID
    ),

    // --- Linien: Bögen / Kreis ---
    ElementTemplateEntity(
        name = "Rechtsbogen 90°", abbreviation = "RB90", category = ElementCategory.LINIE, colorHex = DARK_GREEN,
        lineGeometry = LineGeometry.BOGEN, defaultRadius = 1.0, defaultDirection = CurveDirection.RECHTS, defaultSweepAngle = 90
    ),
    ElementTemplateEntity(
        name = "Rechtsbogen 180°", abbreviation = "RB180", category = ElementCategory.LINIE, colorHex = DARK_GREEN,
        lineGeometry = LineGeometry.BOGEN, defaultRadius = 1.0, defaultDirection = CurveDirection.RECHTS, defaultSweepAngle = 180
    ),
    ElementTemplateEntity(
        name = "Linksbogen 90°", abbreviation = "LB90", category = ElementCategory.LINIE, colorHex = DARK_GREEN,
        lineGeometry = LineGeometry.BOGEN, defaultRadius = 1.0, defaultDirection = CurveDirection.LINKS, defaultSweepAngle = 90
    ),
    ElementTemplateEntity(
        name = "Linksbogen 180°", abbreviation = "LB180", category = ElementCategory.LINIE, colorHex = DARK_GREEN,
        lineGeometry = LineGeometry.BOGEN, defaultRadius = 1.0, defaultDirection = CurveDirection.LINKS, defaultSweepAngle = 180
    ),
    ElementTemplateEntity(
        name = "Vollkreis", abbreviation = "KR", category = ElementCategory.LINIE, colorHex = DARK_GREEN,
        lineGeometry = LineGeometry.BOGEN, defaultRadius = 1.5, defaultDirection = CurveDirection.RECHTS, defaultSweepAngle = 360
    )
)
