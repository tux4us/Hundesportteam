package de.hundesportteam.app.ui.parcour

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import de.hundesportteam.app.data.local.entity.ParcourEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParcourListScreen(
    onParcourClick: (Long) -> Unit,
    onCreateNewClick: () -> Unit,
    viewModel: ParcourViewModel = hiltViewModel()
) {
    val parcours by viewModel.savedParcours.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Trainingsparcoure") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = onCreateNewClick) {
                Icon(Icons.Default.Add, contentDescription = "Neuer Parcour")
            }
        }
    ) { padding ->
        if (parcours.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("Noch keine Parcoure erstellt. Tippe auf + um zu starten.")
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(parcours, key = { it.id }) { parcour ->
                    ParcourListItem(
                        parcour = parcour,
                        onClick = { onParcourClick(parcour.id) },
                        onDelete = { viewModel.deleteParcour(parcour) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ParcourListItem(
    parcour: ParcourEntity,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val dateFormat = remember(parcour.id) { SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.GERMANY) }
    Card(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(parcour.title, style = MaterialTheme.typography.titleMedium)
                if (parcour.description.isNotBlank()) {
                    Text(
                        parcour.description,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 2
                    )
                }
                Text(
                    "${parcour.fieldWidth.toInt()}×${parcour.fieldLength.toInt()} m · ${dateFormat.format(Date(parcour.updatedAt))}",
                    style = MaterialTheme.typography.labelSmall
                )
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Löschen")
            }
        }
    }
}
