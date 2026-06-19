package de.hundesportteam.app.ui.parcour

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import de.hundesportteam.app.data.local.entity.ElementTemplateEntity
import de.hundesportteam.app.data.model.CurveDirection
import de.hundesportteam.app.data.model.ElementCategory
import de.hundesportteam.app.data.model.LineGeometry
import de.hundesportteam.app.data.model.LineStrokeStyle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ElementTemplateManagerScreen(
    onBackClick: () -> Unit,
    viewModel: ParcourViewModel = hiltViewModel()
) {
    val templates by viewModel.templates.collectAsState()
    var editingTemplate by remember { mutableStateOf<ElementTemplateEntity?>(null) }
    var isCreatingNew by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Vorlagen verwalten") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Zurück")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { isCreatingNew = true }) {
                Icon(Icons.Default.Add, contentDescription = "Neue Vorlage")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item { Text("Punkte", style = MaterialTheme.typography.titleSmall) }
            items(templates.filter { it.category == ElementCategory.PUNKT }, key = { it.id }) { template ->
                TemplateRow(
                    template = template,
                    onEdit = { editingTemplate = template },
                    onDelete = { viewModel.deleteTemplate(template) }
                )
            }
            item { Text("Linien", style = MaterialTheme.typography.titleSmall, modifier = Modifier.padding(top = 12.dp)) }
            items(templates.filter { it.category == ElementCategory.LINIE }, key = { it.id }) { template ->
                TemplateRow(
                    template = template,
                    onEdit = { editingTemplate = template },
                    onDelete = { viewModel.deleteTemplate(template) }
                )
            }
        }
    }

    editingTemplate?.let { current ->
        TemplateEditDialog(
            initial = current,
            onConfirm = { template -> viewModel.upsertTemplate(template); editingTemplate = null },
            onDismiss = { editingTemplate = null }
        )
    }
    if (isCreatingNew) {
        TemplateEditDialog(
            initial = null,
            onConfirm = { template -> viewModel.upsertTemplate(template); isCreatingNew = false },
            onDismiss = { isCreatingNew = false }
        )
    }
}

@Composable
private fun TemplateRow(template: ElementTemplateEntity, onEdit: () -> Unit, onDelete: () -> Unit) {
    Card(onClick = onEdit, modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(12.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            val color = runCatching { Color(android.graphics.Color.parseColor(template.colorHex)) }.getOrDefault(Color.Gray)
            Box(modifier = Modifier.size(20.dp).background(color, CircleShape))
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("${template.abbreviation} – ${template.name}", style = MaterialTheme.typography.bodyLarge)
                Text(templateSubtitle(template), style = MaterialTheme.typography.labelSmall)
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Löschen")
            }
        }
    }
}

private fun templateSubtitle(template: ElementTemplateEntity): String = when {
    template.category == ElementCategory.PUNKT -> "Punkt"
    template.lineGeometry == LineGeometry.BOGEN ->
        "Bogen · ${template.defaultDirection ?: ""} · ${template.defaultSweepAngle ?: ""}° · r=${template.defaultRadius ?: ""} m"
    else -> "Linie · ${template.strokeStyle ?: ""}"
}

@Composable
private fun TemplateEditDialog(
    initial: ElementTemplateEntity?,
    onConfirm: (ElementTemplateEntity) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf(initial?.name ?: "") }
    var abbreviation by remember { mutableStateOf(initial?.abbreviation ?: "") }
    var category by remember { mutableStateOf(initial?.category ?: ElementCategory.PUNKT) }
    var colorHex by remember { mutableStateOf(initial?.colorHex ?: "#FFD48B") }
    var lineGeometry by remember { mutableStateOf(initial?.lineGeometry ?: LineGeometry.GERADE) }
    var strokeStyle by remember { mutableStateOf(initial?.strokeStyle ?: LineStrokeStyle.SOLID) }
    var direction by remember { mutableStateOf(initial?.defaultDirection ?: CurveDirection.RECHTS) }
    var sweep by remember { mutableStateOf(initial?.defaultSweepAngle ?: 90) }
    var radiusText by remember { mutableStateOf((initial?.defaultRadius ?: 1.0).toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (initial == null) "Neue Vorlage" else "Vorlage bearbeiten") },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = name, onValueChange = { name = it },
                    label = { Text("Name") }, modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = abbreviation, onValueChange = { if (it.length <= 5) abbreviation = it },
                    label = { Text("Kürzel") }, modifier = Modifier.fillMaxWidth()
                )

                Text("Kategorie", style = MaterialTheme.typography.labelMedium)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(selected = category == ElementCategory.PUNKT, onClick = { category = ElementCategory.PUNKT }, label = { Text("Punkt") })
                    FilterChip(selected = category == ElementCategory.LINIE, onClick = { category = ElementCategory.LINIE }, label = { Text("Linie") })
                }

                Text("Farbe", style = MaterialTheme.typography.labelMedium)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("#FFD48B", "#003A00", "#B0413E", "#2D5C8A", "#6B4E9C").forEach { hex ->
                        val c = Color(android.graphics.Color.parseColor(hex))
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .background(c, CircleShape)
                                .border(
                                    width = if (colorHex == hex) 2.dp else 0.dp,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    shape = CircleShape
                                )
                                .clickable { colorHex = hex }
                        )
                    }
                }

                if (category == ElementCategory.LINIE) {
                    Text("Geometrie", style = MaterialTheme.typography.labelMedium)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(selected = lineGeometry == LineGeometry.GERADE, onClick = { lineGeometry = LineGeometry.GERADE }, label = { Text("Gerade") })
                        FilterChip(selected = lineGeometry == LineGeometry.BOGEN, onClick = { lineGeometry = LineGeometry.BOGEN }, label = { Text("Bogen") })
                    }

                    if (lineGeometry == LineGeometry.GERADE) {
                        Text("Strichart", style = MaterialTheme.typography.labelMedium)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf(
                                LineStrokeStyle.SOLID to "Normal",
                                LineStrokeStyle.SOLID_THIN to "Dünn",
                                LineStrokeStyle.SOLID_THICK to "Dick",
                                LineStrokeStyle.DASHED to "Gestrichelt"
                            ).forEach { (style, label) ->
                                FilterChip(selected = strokeStyle == style, onClick = { strokeStyle = style }, label = { Text(label) })
                            }
                        }
                    } else {
                        Text("Richtung", style = MaterialTheme.typography.labelMedium)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            FilterChip(selected = direction == CurveDirection.RECHTS, onClick = { direction = CurveDirection.RECHTS }, label = { Text("Rechts") })
                            FilterChip(selected = direction == CurveDirection.LINKS, onClick = { direction = CurveDirection.LINKS }, label = { Text("Links") })
                        }
                        Text("Standard-Winkel", style = MaterialTheme.typography.labelMedium)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf(90, 180, 270, 360).forEach { angle ->
                                FilterChip(selected = sweep == angle, onClick = { sweep = angle }, label = { Text("$angle°") })
                            }
                        }
                        OutlinedTextField(
                            value = radiusText,
                            onValueChange = { radiusText = it },
                            label = { Text("Standard-Radius (m)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                enabled = name.isNotBlank() && abbreviation.isNotBlank(),
                onClick = {
                    val result = ElementTemplateEntity(
                        id = initial?.id ?: 0,
                        name = name,
                        abbreviation = abbreviation,
                        category = category,
                        colorHex = colorHex,
                        lineGeometry = if (category == ElementCategory.LINIE) lineGeometry else null,
                        strokeStyle = if (category == ElementCategory.LINIE && lineGeometry == LineGeometry.GERADE) strokeStyle else null,
                        defaultRadius = if (category == ElementCategory.LINIE && lineGeometry == LineGeometry.BOGEN) radiusText.toDoubleOrNull() ?: 1.0 else null,
                        defaultDirection = if (category == ElementCategory.LINIE && lineGeometry == LineGeometry.BOGEN) direction else null,
                        defaultSweepAngle = if (category == ElementCategory.LINIE && lineGeometry == LineGeometry.BOGEN) sweep else null
                    )
                    onConfirm(result)
                }
            ) { Text("Speichern") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Abbrechen") } }
    )
}
