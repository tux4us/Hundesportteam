package de.hundesportteam.app.ui.parcour

import android.content.Intent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import de.hundesportteam.app.data.local.entity.ElementTemplateEntity
import de.hundesportteam.app.data.model.CurveDirection
import de.hundesportteam.app.data.model.ElementCategory
import de.hundesportteam.app.data.model.LineGeometry
import de.hundesportteam.app.data.model.LineStrokeStyle
import de.hundesportteam.app.data.model.ParcourData
import de.hundesportteam.app.util.MeterPoint
import de.hundesportteam.app.util.ParcourGeometry
import de.hundesportteam.app.util.PdfExporter

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ParcourEditorScreen(
    parcourId: Long?,
    onBackClick: () -> Unit,
    onSaved: () -> Unit,
    onManageTemplatesClick: () -> Unit,
    viewModel: ParcourViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(parcourId) {
        if (parcourId != state.editingParcourId) {
            if (parcourId != null) viewModel.loadParcour(parcourId) else viewModel.resetEditor()
        }
    }
    val templates by viewModel.templates.collectAsState()
    var showDetailsDialog by remember { mutableStateOf(false) }

    val selectedTemplate = templates.find { it.id == state.selectedTemplateId }
    val isBogenSelected = selectedTemplate?.category == ElementCategory.LINIE &&
        selectedTemplate.lineGeometry == LineGeometry.BOGEN

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(state.title.ifBlank { "Neuer Parcour" }) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Zurück")
                    }
                },
                actions = {
                    IconButton(onClick = onManageTemplatesClick) {
                        Icon(Icons.Default.Settings, contentDescription = "Vorlagen verwalten")
                    }
                    IconButton(onClick = viewModel::undoLastWaypoint, enabled = state.waypoints.isNotEmpty()) {
                        Icon(Icons.AutoMirrored.Filled.Undo, contentDescription = "Letzten Punkt entfernen")
                    }
                    IconButton(
                        enabled = state.waypoints.isNotEmpty(),
                        onClick = {
                            val uri = PdfExporter.export(
                                context = context,
                                title = state.title,
                                description = state.description,
                                fieldWidth = state.fieldWidth,
                                fieldLength = state.fieldLength,
                                data = ParcourData(
                                    initialHeadingDegrees = state.initialHeadingDegrees,
                                    waypoints = state.waypoints
                                ),
                                templates = templates
                            )
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "application/pdf"
                                putExtra(Intent.EXTRA_STREAM, uri)
                                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "Parcour als PDF teilen"))
                        }
                    ) {
                        Icon(Icons.Default.PictureAsPdf, contentDescription = "Als PDF exportieren")
                    }
                    IconButton(onClick = {
                        viewModel.saveCurrentParcour { onSaved() }
                    }) {
                        Icon(Icons.Default.Save, contentDescription = "Speichern")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            // Titel + Details
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = state.title,
                    onValueChange = viewModel::updateTitle,
                    label = { Text("Titel") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                TextButton(onClick = { showDetailsDialog = true }) {
                    Text("Details")
                }
            }

            // Toolbar: Grid-Snap
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Raster-Snap", style = MaterialTheme.typography.bodyMedium)
                    Switch(checked = state.gridSnapEnabled, onCheckedChange = { viewModel.toggleGridSnap() })
                }
                Text(
                    "${state.fieldWidth.toInt()} × ${state.fieldLength.toInt()} m",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            // Startrichtung (nur relevant, solange noch kein Wegpunkt gesetzt ist)
            if (state.waypoints.isEmpty()) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Startrichtung:", style = MaterialTheme.typography.bodyMedium)
                    listOf(0.0 to "↓", 90.0 to "→", 180.0 to "↑", 270.0 to "←").forEach { (deg, arrow) ->
                        FilterChip(
                            selected = state.initialHeadingDegrees == deg,
                            onClick = { viewModel.setInitialHeading(deg) },
                            label = { Text(arrow) }
                        )
                    }
                }
            }

            // Übungsfläche
            Box(modifier = Modifier.padding(16.dp)) {
                ParcourCanvas(
                    state = state,
                    templates = templates,
                    onTap = viewModel::onCanvasTap
                )
            }

            // Bogen-Konfiguration, wenn eine BOGEN-Vorlage gewählt ist
            if (isBogenSelected) {
                BogenConfigPanel(
                    radius = state.bogenRadius,
                    sweep = state.bogenSweep,
                    onRadiusChange = viewModel::updateBogenRadius,
                    onSweepChange = viewModel::updateBogenSweep,
                    onInsert = viewModel::insertBogen
                )
            }

            // Element-Palette
            ElementPalette(
                templates = templates,
                selectedId = state.selectedTemplateId,
                onSelect = viewModel::selectTemplate
            )
        }
    }

    if (showDetailsDialog) {
        DetailsDialog(
            description = state.description,
            fieldWidth = state.fieldWidth,
            fieldLength = state.fieldLength,
            onConfirm = { desc, width, length ->
                viewModel.updateDescription(desc)
                viewModel.updateFieldSize(width, length)
                showDetailsDialog = false
            },
            onDismiss = { showDetailsDialog = false }
        )
    }
}

@Composable
private fun ParcourCanvas(
    state: ParcourEditorState,
    templates: List<ElementTemplateEntity>,
    onTap: (xMeter: Double, yMeter: Double) -> Unit,
    modifier: Modifier = Modifier
) {
    val templatesById = remember(templates) { templates.associateBy { it.id } }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio((state.fieldWidth / state.fieldLength).toFloat())
            .background(Color(0xFFF5F5F0))
            .pointerInput(state.fieldWidth, state.fieldLength) {
                detectTapGestures { offset ->
                    val scale = size.width / state.fieldWidth
                    onTap(offset.x / scale, offset.y / scale)
                }
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val scale = size.width / state.fieldWidth.toFloat()

            // Raster: 1 m dünn, 5 m kräftiger
            val gridColor = Color(0xFFD8D8D0)
            val gridColorMajor = Color(0xFFB8B8AC)
            var gx = 0.0
            while (gx <= state.fieldWidth) {
                val isMajor = (gx % 5.0) < 0.001
                drawLine(
                    color = if (isMajor) gridColorMajor else gridColor,
                    start = Offset((gx * scale).toFloat(), 0f),
                    end = Offset((gx * scale).toFloat(), size.height),
                    strokeWidth = if (isMajor) 1.5f else 0.75f
                )
                gx += 1.0
            }
            var gy = 0.0
            while (gy <= state.fieldLength) {
                val isMajor = (gy % 5.0) < 0.001
                drawLine(
                    color = if (isMajor) gridColorMajor else gridColor,
                    start = Offset(0f, (gy * scale).toFloat()),
                    end = Offset(size.width, (gy * scale).toFloat()),
                    strokeWidth = if (isMajor) 1.5f else 0.75f
                )
                gy += 1.0
            }

            drawRect(
                color = Color(0xFF707060),
                topLeft = Offset.Zero,
                size = size,
                style = Stroke(width = 2f)
            )

            val waypoints = state.waypoints
            for (i in waypoints.indices) {
                val wp = waypoints[i]
                val posPx = Offset((wp.x * scale).toFloat(), (wp.y * scale).toFloat())

                if (i > 0) {
                    val prev = waypoints[i - 1]
                    val prevPx = Offset((prev.x * scale).toFloat(), (prev.y * scale).toFloat())
                    val segment = wp.incomingSegment
                    val template = segment?.let { templatesById[it.templateId] }
                    val color = template?.colorHex?.let { hex ->
                        runCatching { Color(android.graphics.Color.parseColor(hex)) }.getOrNull()
                    } ?: Color(0xFF003A00)

                    if (template?.lineGeometry == LineGeometry.BOGEN && segment != null) {
                        val radius = segment.radius ?: template.defaultRadius ?: 1.0
                        val direction = segment.direction ?: template.defaultDirection ?: CurveDirection.RECHTS
                        val sweep = segment.sweepAngle ?: template.defaultSweepAngle ?: 90
                        val entryHeading = ParcourGeometry.headingBeforeWaypoint(
                            waypoints, i, state.initialHeadingDegrees
                        ) { templatesById[it] }
                        val center = ParcourGeometry.computeArcCenter(
                            MeterPoint(prev.x, prev.y), entryHeading, radius, direction
                        )
                        val centerPx = Offset((center.x * scale).toFloat(), (center.y * scale).toFloat())
                        val radiusPx = (radius * scale).toFloat()
                        val arcParams = ParcourGeometry.composeArcParams(entryHeading, direction, sweep)
                        drawArc(
                            color = color,
                            startAngle = arcParams.startAngleDegrees,
                            sweepAngle = arcParams.sweepAngleDegrees,
                            useCenter = false,
                            topLeft = Offset(centerPx.x - radiusPx, centerPx.y - radiusPx),
                            size = Size(radiusPx * 2, radiusPx * 2),
                            style = Stroke(width = 6f)
                        )
                    } else {
                        val strokeWidth = when (template?.strokeStyle) {
                            LineStrokeStyle.SOLID_THIN -> 4f
                            LineStrokeStyle.SOLID_THICK -> 10f
                            else -> 6f
                        }
                        val pathEffect = if (template?.strokeStyle == LineStrokeStyle.DASHED) {
                            PathEffect.dashPathEffect(floatArrayOf(20f, 15f))
                        } else null
                        drawLine(
                            color = color,
                            start = prevPx,
                            end = posPx,
                            strokeWidth = strokeWidth,
                            pathEffect = pathEffect
                        )
                    }

                    template?.let { tmpl ->
                        val midPx = Offset((prevPx.x + posPx.x) / 2, (prevPx.y + posPx.y) / 2)
                        drawContext.canvas.nativeCanvas.drawText(
                            tmpl.abbreviation, midPx.x, midPx.y - 12f,
                            android.graphics.Paint().apply {
                                this.color = android.graphics.Color.parseColor(tmpl.colorHex)
                                textSize = 36f
                                textAlign = android.graphics.Paint.Align.CENTER
                                isFakeBoldText = true
                            }
                        )
                    }
                }

                val actionTemplate = wp.actionTemplateId?.let { templatesById[it] }
                val markerColor = actionTemplate?.colorHex?.let { hex ->
                    runCatching { Color(android.graphics.Color.parseColor(hex)) }.getOrNull()
                } ?: Color(0xFF707060)
                drawCircle(
                    color = markerColor,
                    radius = if (actionTemplate != null) 24f else 10f,
                    center = posPx
                )
                actionTemplate?.let { tmpl ->
                    drawContext.canvas.nativeCanvas.drawText(
                        tmpl.abbreviation, posPx.x, posPx.y + 12f,
                        android.graphics.Paint().apply {
                            color = android.graphics.Color.WHITE
                            textSize = 32f
                            textAlign = android.graphics.Paint.Align.CENTER
                            isFakeBoldText = true
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun BogenConfigPanel(
    radius: Double,
    sweep: Int,
    onRadiusChange: (Double) -> Unit,
    onSweepChange: (Int) -> Unit,
    onInsert: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Radius:", style = MaterialTheme.typography.bodyMedium)
                IconButton(onClick = { onRadiusChange(radius - 0.25) }) { Text("–") }
                Text("%.2f m".format(radius))
                IconButton(onClick = { onRadiusChange(radius + 0.25) }) { Text("+") }
            }
            Row(
                modifier = Modifier.padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(90, 180, 270, 360).forEach { angle ->
                    FilterChip(
                        selected = sweep == angle,
                        onClick = { onSweepChange(angle) },
                        label = { Text("$angle°") }
                    )
                }
            }
            OutlinedButton(
                onClick = onInsert,
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            ) {
                Text("Bogen einfügen")
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ElementPalette(
    templates: List<ElementTemplateEntity>,
    selectedId: Long?,
    onSelect: (Long) -> Unit
) {
    val points = templates.filter { it.category == ElementCategory.PUNKT }
    val lines = templates.filter { it.category == ElementCategory.LINIE }

    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
        Text("Punkte", style = MaterialTheme.typography.labelLarge)
        FlowRow(
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            points.forEach { template ->
                FilterChip(
                    selected = selectedId == template.id,
                    onClick = { onSelect(template.id) },
                    label = { Text("${template.abbreviation} ${template.name}") }
                )
            }
        }
        Text(
            "Linien",
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(top = 8.dp)
        )
        FlowRow(
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            lines.forEach { template ->
                FilterChip(
                    selected = selectedId == template.id,
                    onClick = { onSelect(template.id) },
                    label = { Text("${template.abbreviation} ${template.name}") }
                )
            }
        }
    }
}

@Composable
private fun DetailsDialog(
    description: String,
    fieldWidth: Double,
    fieldLength: Double,
    onConfirm: (description: String, width: Double, length: Double) -> Unit,
    onDismiss: () -> Unit
) {
    var descState by remember { mutableStateOf(description) }
    var widthState by remember { mutableStateOf(fieldWidth.toString()) }
    var lengthState by remember { mutableStateOf(fieldLength.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Parcour-Details") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = descState,
                    onValueChange = { descState = it },
                    label = { Text("Beschreibung") },
                    modifier = Modifier.fillMaxWidth()
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = widthState,
                        onValueChange = { widthState = it },
                        label = { Text("Breite (m)") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = lengthState,
                        onValueChange = { lengthState = it },
                        label = { Text("Länge (m)") },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val width = widthState.toDoubleOrNull() ?: fieldWidth
                val length = lengthState.toDoubleOrNull() ?: fieldLength
                onConfirm(descState, width, length)
            }) { Text("Übernehmen") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Abbrechen") }
        }
    )
}
