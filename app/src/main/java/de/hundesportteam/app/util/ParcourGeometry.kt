package de.hundesportteam.app.util

import de.hundesportteam.app.data.local.entity.ElementTemplateEntity
import de.hundesportteam.app.data.model.CurveDirection
import de.hundesportteam.app.data.model.LineGeometry
import de.hundesportteam.app.data.model.Waypoint
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

/** Punkt in Metern (Feld-Koordinatensystem: x ∈ [0, fieldWidth], y ∈ [0, fieldLength]). */
data class MeterPoint(val x: Double, val y: Double)

/** Start-/Sweepwinkel im Format, das Compose's drawArc() erwartet. */
data class ComposeArcParams(val startAngleDegrees: Float, val sweepAngleDegrees: Float)

/**
 * Geometrie-Hilfsfunktionen für die Parcour-Wegpunktkette.
 *
 * Eigene Winkelkonvention ("heading"): 0° = Blickrichtung +y-Achse, Winkel steigen
 * im Uhrzeigersinn (90° = +x-Achse, 180° = -y-Achse, 270° = -x-Achse). Diese
 * Konvention ist unabhängig von Compose's drawArc()-Winkelsystem; composeArcParams()
 * übernimmt die Umrechnung nur für die reine Darstellung.
 */
object ParcourGeometry {

    private fun headingVector(headingDegrees: Double): MeterPoint {
        val rad = Math.toRadians(headingDegrees)
        return MeterPoint(-sin(rad), cos(rad))
    }

    private fun normalizeDegrees(value: Double): Double = ((value % 360.0) + 360.0) % 360.0

    fun computeArcCenter(
        entry: MeterPoint,
        headingDegrees: Double,
        radius: Double,
        direction: CurveDirection
    ): MeterPoint {
        val sign = if (direction == CurveDirection.RECHTS) 1.0 else -1.0
        val centerOffsetAngle = headingDegrees + 90.0 * sign
        val v = headingVector(centerOffsetAngle)
        return MeterPoint(entry.x + radius * v.x, entry.y + radius * v.y)
    }

    /**
     * Austrittspunkt + Austrittsrichtung eines Bogensegments.
     * sweepDegrees: 90, 180, 270 oder 360 (360 = Vollkreis, identischer Ein-/Austrittspunkt).
     */
    fun computeArcExit(
        entry: MeterPoint,
        headingDegrees: Double,
        radius: Double,
        direction: CurveDirection,
        sweepDegrees: Int
    ): Pair<MeterPoint, Double> {
        val sign = if (direction == CurveDirection.RECHTS) 1.0 else -1.0
        val centerOffsetAngle = headingDegrees + 90.0 * sign
        val center = computeArcCenter(entry, headingDegrees, radius, direction)
        val signedSweep = sign * sweepDegrees
        val exitVectorAngle = centerOffsetAngle + 180.0 + signedSweep
        val v = headingVector(exitVectorAngle)
        val exitPoint = MeterPoint(center.x + radius * v.x, center.y + radius * v.y)
        val exitHeading = normalizeDegrees(headingDegrees + signedSweep)
        return exitPoint to exitHeading
    }

    /** Wandelt das interne heading-System in Compose's drawArc()-Gradangaben um. */
    fun composeArcParams(
        entryHeadingDegrees: Double,
        direction: CurveDirection,
        sweepDegrees: Int
    ): ComposeArcParams {
        val sign = if (direction == CurveDirection.RECHTS) 1.0 else -1.0
        val centerOffsetAngle = entryHeadingDegrees + 90.0 * sign
        val alpha0 = centerOffsetAngle + 180.0
        val composeStartAngle = normalizeDegrees(alpha0 + 90.0)
        val composeSweepAngle = sign * sweepDegrees
        return ComposeArcParams(composeStartAngle.toFloat(), composeSweepAngle.toFloat())
    }

    /** Rundet einen Meterwert auf das nächste Vielfache von gridSize (z. B. 0.5 m). */
    fun snapToGrid(value: Double, gridSize: Double): Double {
        if (gridSize <= 0.0) return value
        return Math.round(value / gridSize) * gridSize
    }

    /**
     * Blickrichtung unmittelbar VOR dem Wegpunkt mit Index [uptoIndexExclusive] –
     * läuft die Kette einmal durch und summiert alle Richtungsänderungen auf.
     * Wird sowohl beim Einfügen neuer Bögen als auch beim Zeichnen gebraucht.
     */
    fun headingBeforeWaypoint(
        waypoints: List<Waypoint>,
        uptoIndexExclusive: Int,
        initialHeadingDegrees: Double,
        templateLookup: (Long) -> ElementTemplateEntity?
    ): Double {
        var heading = initialHeadingDegrees
        for (i in 1 until uptoIndexExclusive) {
            val segment = waypoints[i].incomingSegment ?: continue
            val template = templateLookup(segment.templateId) ?: continue
            heading = if (template.lineGeometry == LineGeometry.BOGEN) {
                computeArcExit(
                    entry = MeterPoint(waypoints[i - 1].x, waypoints[i - 1].y),
                    headingDegrees = heading,
                    radius = segment.radius ?: template.defaultRadius ?: 1.0,
                    direction = segment.direction ?: template.defaultDirection ?: CurveDirection.RECHTS,
                    sweepDegrees = segment.sweepAngle ?: template.defaultSweepAngle ?: 90
                ).second
            } else {
                val dx = waypoints[i].x - waypoints[i - 1].x
                val dy = waypoints[i].y - waypoints[i - 1].y
                normalizeDegrees(Math.toDegrees(atan2(-dx, dy)))
            }
        }
        return heading
    }
}
