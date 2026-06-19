package de.hundesportteam.app.util

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.pdf.PdfDocument
import android.net.Uri
import androidx.core.content.FileProvider
import de.hundesportteam.app.data.local.entity.ElementTemplateEntity
import de.hundesportteam.app.data.model.CurveDirection
import de.hundesportteam.app.data.model.LineGeometry
import de.hundesportteam.app.data.model.LineStrokeStyle
import de.hundesportteam.app.data.model.ParcourData
import java.io.File
import java.io.FileOutputStream

/**
 * Rendert einen Parcour proportional korrekt auf eine A4-Seite (Maßstabsbalken statt
 * physischem 1:1-Druck, da 30x40 m nicht auf Papier passen) und liefert eine über
 * FileProvider teilbare Uri zurück.
 */
object PdfExporter {

    private const val PAGE_WIDTH_PT = 595f  // A4 bei 72 dpi
    private const val PAGE_HEIGHT_PT = 842f
    private const val MARGIN_PT = 36f       // 0,5 Zoll

    fun export(
        context: Context,
        title: String,
        description: String,
        fieldWidth: Double,
        fieldLength: Double,
        data: ParcourData,
        templates: List<ElementTemplateEntity>
    ): Uri {
        val templatesById = templates.associateBy { it.id }

        val document = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH_PT.toInt(), PAGE_HEIGHT_PT.toInt(), 1).create()
        val page = document.startPage(pageInfo)
        val canvas = page.canvas

        drawHeader(canvas, title, description, fieldWidth, fieldLength)

        val fieldAreaTop = 130f
        val legendHeight = 70f
        val fieldAreaBottom = PAGE_HEIGHT_PT - MARGIN_PT - legendHeight
        val fieldRect = computeFieldRect(fieldWidth, fieldLength, fieldAreaTop, fieldAreaBottom)

        drawField(canvas, fieldRect, fieldWidth, fieldLength, data, templatesById)
        drawScaleBar(canvas, fieldRect, fieldWidth)
        drawLegend(canvas, fieldAreaBottom + 12f, data, templatesById)

        document.finishPage(page)

        val outputDir = File(context.cacheDir, "exports").apply { mkdirs() }
        val safeTitle = title.ifBlank { "Parcour" }.replace(Regex("[^A-Za-z0-9_\\-]"), "_")
        val file = File(outputDir, "$safeTitle.pdf")
        FileOutputStream(file).use { document.writeTo(it) }
        document.close()

        return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    }

    private fun drawHeader(canvas: Canvas, title: String, description: String, fieldWidth: Double, fieldLength: Double) {
        val titlePaint = Paint().apply {
            color = Color.parseColor("#003A00")
            textSize = 22f
            isFakeBoldText = true
            isAntiAlias = true
        }
        canvas.drawText(title.ifBlank { "Trainingsparcour" }, MARGIN_PT, 50f, titlePaint)

        if (description.isNotBlank()) {
            val descPaint = Paint().apply {
                color = Color.DKGRAY
                textSize = 12f
                isAntiAlias = true
            }
            drawWrappedText(canvas, description, MARGIN_PT, 72f, PAGE_WIDTH_PT - 2 * MARGIN_PT, descPaint)
        }

        val infoPaint = Paint().apply {
            color = Color.GRAY
            textSize = 10f
            isAntiAlias = true
        }
        canvas.drawText(
            "Übungsfläche: ${fieldWidth.toInt()} × ${fieldLength.toInt()} m",
            MARGIN_PT, 110f, infoPaint
        )
    }

    private fun drawWrappedText(canvas: Canvas, text: String, x: Float, startY: Float, maxWidth: Float, paint: Paint) {
        val words = text.split(" ")
        var line = StringBuilder()
        var y = startY
        for (word in words) {
            val candidate = if (line.isEmpty()) word else "$line $word"
            if (paint.measureText(candidate) > maxWidth && line.isNotEmpty()) {
                canvas.drawText(line.toString(), x, y, paint)
                line = StringBuilder(word)
                y += paint.textSize + 4f
            } else {
                line = StringBuilder(candidate)
            }
        }
        if (line.isNotEmpty()) canvas.drawText(line.toString(), x, y, paint)
    }

    private fun computeFieldRect(fieldWidth: Double, fieldLength: Double, top: Float, bottom: Float): RectF {
        val availableWidth = PAGE_WIDTH_PT - 2 * MARGIN_PT
        val availableHeight = bottom - top
        val fieldAspect = (fieldWidth / fieldLength).toFloat()
        var w = availableWidth
        var h = w / fieldAspect
        if (h > availableHeight) {
            h = availableHeight
            w = h * fieldAspect
        }
        val left = MARGIN_PT + (availableWidth - w) / 2
        return RectF(left, top, left + w, top + h)
    }

    private fun drawField(
        canvas: Canvas,
        fieldRect: RectF,
        fieldWidth: Double,
        fieldLength: Double,
        data: ParcourData,
        templatesById: Map<Long, ElementTemplateEntity>
    ) {
        val scale = fieldRect.width() / fieldWidth.toFloat()

        val gridPaint = Paint().apply { color = Color.parseColor("#E0E0D8"); strokeWidth = 0.5f }
        val gridPaintMajor = Paint().apply { color = Color.parseColor("#C0C0B0"); strokeWidth = 1f }
        var gx = 0.0
        while (gx <= fieldWidth) {
            val isMajor = (gx % 5.0) < 0.001
            val x = fieldRect.left + (gx * scale).toFloat()
            canvas.drawLine(x, fieldRect.top, x, fieldRect.bottom, if (isMajor) gridPaintMajor else gridPaint)
            gx += 1.0
        }
        var gy = 0.0
        while (gy <= fieldLength) {
            val isMajor = (gy % 5.0) < 0.001
            val y = fieldRect.top + (gy * scale).toFloat()
            canvas.drawLine(fieldRect.left, y, fieldRect.right, y, if (isMajor) gridPaintMajor else gridPaint)
            gy += 1.0
        }

        val borderPaint = Paint().apply { color = Color.parseColor("#707060"); style = Paint.Style.STROKE; strokeWidth = 1.5f }
        canvas.drawRect(fieldRect, borderPaint)

        val waypoints = data.waypoints
        for (i in waypoints.indices) {
            val wp = waypoints[i]
            val posX = fieldRect.left + (wp.x * scale).toFloat()
            val posY = fieldRect.top + (wp.y * scale).toFloat()

            if (i > 0) {
                val prev = waypoints[i - 1]
                val prevX = fieldRect.left + (prev.x * scale).toFloat()
                val prevY = fieldRect.top + (prev.y * scale).toFloat()
                val segment = wp.incomingSegment
                val template = segment?.let { templatesById[it.templateId] }
                val color = template?.colorHex?.let { hex -> runCatching { Color.parseColor(hex) }.getOrNull() }
                    ?: Color.parseColor("#003A00")
                val linePaint = Paint().apply {
                    this.color = color
                    style = Paint.Style.STROKE
                    isAntiAlias = true
                    strokeWidth = when (template?.strokeStyle) {
                        LineStrokeStyle.SOLID_THIN -> 1f
                        LineStrokeStyle.SOLID_THICK -> 3f
                        else -> 2f
                    }
                    if (template?.strokeStyle == LineStrokeStyle.DASHED) {
                        pathEffect = DashPathEffect(floatArrayOf(6f, 4f), 0f)
                    }
                }

                if (template?.lineGeometry == LineGeometry.BOGEN && segment != null) {
                    val radius = segment.radius ?: template.defaultRadius ?: 1.0
                    val direction = segment.direction ?: template.defaultDirection ?: CurveDirection.RECHTS
                    val sweep = segment.sweepAngle ?: template.defaultSweepAngle ?: 90
                    val entryHeading = ParcourGeometry.headingBeforeWaypoint(
                        waypoints, i, data.initialHeadingDegrees
                    ) { templatesById[it] }
                    val center = ParcourGeometry.computeArcCenter(
                        MeterPoint(prev.x, prev.y), entryHeading, radius, direction
                    )
                    val centerX = fieldRect.left + (center.x * scale).toFloat()
                    val centerY = fieldRect.top + (center.y * scale).toFloat()
                    val radiusPx = (radius * scale).toFloat()
                    val arcParams = ParcourGeometry.composeArcParams(entryHeading, direction, sweep)
                    val arcRect = RectF(centerX - radiusPx, centerY - radiusPx, centerX + radiusPx, centerY + radiusPx)
                    canvas.drawArc(arcRect, arcParams.startAngleDegrees, arcParams.sweepAngleDegrees, false, linePaint)
                } else {
                    canvas.drawLine(prevX, prevY, posX, posY, linePaint)
                }

                template?.abbreviation?.let { label ->
                    val labelPaint = Paint().apply {
                        this.color = color
                        textSize = 9f
                        textAlign = Paint.Align.CENTER
                        isAntiAlias = true
                    }
                    canvas.drawText(label, (prevX + posX) / 2, (prevY + posY) / 2 - 4f, labelPaint)
                }
            }

            val actionTemplate = wp.actionTemplateId?.let { templatesById[it] }
            val markerColor = actionTemplate?.colorHex?.let { hex -> runCatching { Color.parseColor(hex) }.getOrNull() }
                ?: Color.parseColor("#707060")
            val markerPaint = Paint().apply { color = markerColor; isAntiAlias = true }
            canvas.drawCircle(posX, posY, if (actionTemplate != null) 6f else 2.5f, markerPaint)
            actionTemplate?.let { tmpl ->
                val textPaint = Paint().apply {
                    color = Color.WHITE
                    textSize = 8f
                    textAlign = Paint.Align.CENTER
                    isFakeBoldText = true
                    isAntiAlias = true
                }
                canvas.drawText(tmpl.abbreviation, posX, posY + 3f, textPaint)
            }
        }
    }

    private fun drawScaleBar(canvas: Canvas, fieldRect: RectF, fieldWidth: Double) {
        val scale = fieldRect.width() / fieldWidth.toFloat()
        val barMeters = if (fieldWidth >= 20) 5.0 else 1.0
        val barLengthPx = (barMeters * scale).toFloat()
        val barY = fieldRect.bottom + 18f
        val barX = fieldRect.left

        val paint = Paint().apply { color = Color.parseColor("#003A00"); strokeWidth = 2f }
        canvas.drawLine(barX, barY, barX + barLengthPx, barY, paint)
        canvas.drawLine(barX, barY - 4f, barX, barY + 4f, paint)
        canvas.drawLine(barX + barLengthPx, barY - 4f, barX + barLengthPx, barY + 4f, paint)

        val textPaint = Paint().apply { color = Color.parseColor("#003A00"); textSize = 9f; isAntiAlias = true }
        canvas.drawText("${barMeters.toInt()} m", barX, barY - 8f, textPaint)
    }

    private fun drawLegend(
        canvas: Canvas,
        startY: Float,
        data: ParcourData,
        templatesById: Map<Long, ElementTemplateEntity>
    ) {
        val usedTemplateIds = mutableSetOf<Long>()
        data.waypoints.forEach { wp ->
            wp.actionTemplateId?.let { usedTemplateIds += it }
            wp.incomingSegment?.let { usedTemplateIds += it.templateId }
        }
        val usedTemplates = usedTemplateIds.mapNotNull { templatesById[it] }.sortedBy { it.name }
        if (usedTemplates.isEmpty()) return

        val labelPaint = Paint().apply { color = Color.DKGRAY; textSize = 9f; isAntiAlias = true }
        var x = MARGIN_PT
        var y = startY + 12f
        val maxX = PAGE_WIDTH_PT - MARGIN_PT
        usedTemplates.forEach { template ->
            val text = "${template.abbreviation} = ${template.name}"
            val textWidth = labelPaint.measureText(text) + 16f
            if (x + textWidth > maxX) {
                x = MARGIN_PT
                y += 14f
            }
            val dotPaint = Paint().apply {
                color = runCatching { Color.parseColor(template.colorHex) }.getOrDefault(Color.DKGRAY)
                isAntiAlias = true
            }
            canvas.drawCircle(x + 3f, y - 3f, 3f, dotPaint)
            canvas.drawText(text, x + 10f, y, labelPaint)
            x += textWidth
        }
    }
}
