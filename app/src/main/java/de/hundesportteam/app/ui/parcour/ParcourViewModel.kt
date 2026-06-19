package de.hundesportteam.app.ui.parcour

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.hundesportteam.app.data.local.entity.ElementTemplateEntity
import de.hundesportteam.app.data.local.entity.ParcourEntity
import de.hundesportteam.app.data.model.CurveDirection
import de.hundesportteam.app.data.model.ElementCategory
import de.hundesportteam.app.data.model.LineGeometry
import de.hundesportteam.app.data.model.ParcourData
import de.hundesportteam.app.data.model.Segment
import de.hundesportteam.app.data.model.Waypoint
import de.hundesportteam.app.data.repository.ElementTemplateRepository
import de.hundesportteam.app.data.repository.ParcourRepository
import de.hundesportteam.app.util.MeterPoint
import de.hundesportteam.app.util.ParcourGeometry
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.UUID
import javax.inject.Inject

data class ParcourEditorState(
    val editingParcourId: Long? = null,
    val title: String = "",
    val description: String = "",
    val fieldWidth: Double = 30.0,
    val fieldLength: Double = 40.0,
    val initialHeadingDegrees: Double = 0.0,
    val waypoints: List<Waypoint> = emptyList(),
    val gridSnapEnabled: Boolean = true,
    val gridSizeMeters: Double = 0.5,
    val selectedTemplateId: Long? = null,
    val bogenRadius: Double = 1.0,
    val bogenSweep: Int = 90
)

@HiltViewModel
class ParcourViewModel @Inject constructor(
    private val parcourRepository: ParcourRepository,
    private val templateRepository: ElementTemplateRepository,
    private val json: Json
) : ViewModel() {

    val templates: StateFlow<List<ElementTemplateEntity>> =
        templateRepository.getAllTemplates()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val savedParcours: StateFlow<List<ParcourEntity>> =
        parcourRepository.getAllParcours()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _uiState = MutableStateFlow(ParcourEditorState())
    val uiState: StateFlow<ParcourEditorState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch { templateRepository.seedDefaultsIfEmpty() }
    }

    fun resetEditor() {
        _uiState.value = ParcourEditorState()
    }

    fun selectTemplate(templateId: Long) {
        val template = templates.value.find { it.id == templateId } ?: return
        _uiState.update {
            it.copy(
                selectedTemplateId = templateId,
                bogenRadius = template.defaultRadius ?: it.bogenRadius,
                bogenSweep = template.defaultSweepAngle ?: it.bogenSweep
            )
        }
    }

    fun updateBogenRadius(radius: Double) {
        _uiState.update { it.copy(bogenRadius = radius.coerceIn(0.5, 10.0)) }
    }

    fun updateBogenSweep(sweep: Int) {
        _uiState.update { it.copy(bogenSweep = sweep) }
    }

    fun toggleGridSnap() {
        _uiState.update { it.copy(gridSnapEnabled = !it.gridSnapEnabled) }
    }

    fun setInitialHeading(degrees: Double) {
        _uiState.update { it.copy(initialHeadingDegrees = degrees) }
    }

    fun updateTitle(value: String) = _uiState.update { it.copy(title = value) }
    fun updateDescription(value: String) = _uiState.update { it.copy(description = value) }
    fun updateFieldSize(width: Double, length: Double) =
        _uiState.update { it.copy(fieldWidth = width, fieldLength = length) }

    /** Tippen auf die Fläche: platziert einen Wegpunkt mit PUNKT-Aktion oder GERADE-Übergang. */
    fun onCanvasTap(xMeter: Double, yMeter: Double) {
        val state = _uiState.value
        val template = templates.value.find { it.id == state.selectedTemplateId }

        // Bögen werden nicht per Tap platziert, da Radius/Winkel den Endpunkt bereits festlegen.
        if (template?.category == ElementCategory.LINIE && template.lineGeometry == LineGeometry.BOGEN) return

        val snappedX = if (state.gridSnapEnabled) ParcourGeometry.snapToGrid(xMeter, state.gridSizeMeters) else xMeter
        val snappedY = if (state.gridSnapEnabled) ParcourGeometry.snapToGrid(yMeter, state.gridSizeMeters) else yMeter
        val clampedX = snappedX.coerceIn(0.0, state.fieldWidth)
        val clampedY = snappedY.coerceIn(0.0, state.fieldLength)

        val isFirstWaypoint = state.waypoints.isEmpty()
        val actionId = if (template?.category == ElementCategory.PUNKT) template.id else null

        val segment = if (isFirstWaypoint) {
            null
        } else {
            val lineTemplateId = when {
                template?.category == ElementCategory.LINIE -> template.id
                else -> state.waypoints.last().incomingSegment?.templateId
                    ?: templates.value.firstOrNull { it.lineGeometry == LineGeometry.GERADE }?.id
            }
            lineTemplateId?.let { Segment(templateId = it) }
        }

        val newWaypoint = Waypoint(
            id = UUID.randomUUID().toString(),
            x = clampedX,
            y = clampedY,
            actionTemplateId = actionId,
            incomingSegment = segment
        )
        _uiState.update { it.copy(waypoints = it.waypoints + newWaypoint) }
    }

    /** Fügt einen Bogen/Kreis am Ende der Kette ein. Position wird berechnet, nicht getippt. */
    fun insertBogen() {
        val state = _uiState.value
        val template = templates.value.find { it.id == state.selectedTemplateId } ?: return
        if (template.category != ElementCategory.LINIE || template.lineGeometry != LineGeometry.BOGEN) return

        val direction = template.defaultDirection ?: CurveDirection.RECHTS
        val lastWaypoint = state.waypoints.lastOrNull()

        // Ohne Vorgänger kann kein Bogen berechnet werden – der Tap dient dann nur
        // dazu, den Startpunkt der Kette zu setzen (ohne Segment).
        if (lastWaypoint == null) {
            val start = Waypoint(
                id = UUID.randomUUID().toString(),
                x = state.fieldWidth / 2,
                y = state.fieldLength / 2,
                actionTemplateId = null,
                incomingSegment = null
            )
            _uiState.update { it.copy(waypoints = listOf(start)) }
            return
        }

        val templatesById = templates.value.associateBy { it.id }
        val entryHeading = ParcourGeometry.headingBeforeWaypoint(
            waypoints = state.waypoints,
            uptoIndexExclusive = state.waypoints.size,
            initialHeadingDegrees = state.initialHeadingDegrees,
            templateLookup = { templatesById[it] }
        )

        val entryPoint = MeterPoint(lastWaypoint.x, lastWaypoint.y)
        val (exitPoint, _) = ParcourGeometry.computeArcExit(
            entry = entryPoint,
            headingDegrees = entryHeading,
            radius = state.bogenRadius,
            direction = direction,
            sweepDegrees = state.bogenSweep
        )

        val segment = Segment(
            templateId = template.id,
            radius = state.bogenRadius,
            direction = direction,
            sweepAngle = state.bogenSweep
        )

        val newWaypoint = Waypoint(
            id = UUID.randomUUID().toString(),
            x = exitPoint.x.coerceIn(0.0, state.fieldWidth),
            y = exitPoint.y.coerceIn(0.0, state.fieldLength),
            actionTemplateId = null,
            incomingSegment = segment
        )
        _uiState.update { it.copy(waypoints = it.waypoints + newWaypoint) }
    }

    fun undoLastWaypoint() {
        _uiState.update { it.copy(waypoints = it.waypoints.dropLast(1)) }
    }

    fun saveCurrentParcour(onSaved: (Long) -> Unit) {
        val state = _uiState.value
        if (state.title.isBlank() || state.waypoints.isEmpty()) return
        viewModelScope.launch {
            val data = ParcourData(
                initialHeadingDegrees = state.initialHeadingDegrees,
                waypoints = state.waypoints
            )
            val now = System.currentTimeMillis()
            val entity = ParcourEntity(
                id = state.editingParcourId ?: 0,
                title = state.title,
                description = state.description,
                fieldWidth = state.fieldWidth,
                fieldLength = state.fieldLength,
                dataJson = json.encodeToString(data),
                createdAt = now,
                updatedAt = now
            )
            val id = parcourRepository.save(entity)
            onSaved(id)
        }
    }

    fun loadParcour(id: Long) {
        viewModelScope.launch {
            val entity = parcourRepository.getParcourById(id) ?: return@launch
            val data = json.decodeFromString<ParcourData>(entity.dataJson)
            _uiState.value = ParcourEditorState(
                editingParcourId = entity.id,
                title = entity.title,
                description = entity.description,
                fieldWidth = entity.fieldWidth,
                fieldLength = entity.fieldLength,
                initialHeadingDegrees = data.initialHeadingDegrees,
                waypoints = data.waypoints
            )
        }
    }

    fun deleteParcour(parcour: ParcourEntity) {
        viewModelScope.launch { parcourRepository.delete(parcour) }
    }

    fun upsertTemplate(template: ElementTemplateEntity) {
        viewModelScope.launch { templateRepository.upsert(template) }
    }

    fun deleteTemplate(template: ElementTemplateEntity) {
        viewModelScope.launch { templateRepository.delete(template) }
    }
}
