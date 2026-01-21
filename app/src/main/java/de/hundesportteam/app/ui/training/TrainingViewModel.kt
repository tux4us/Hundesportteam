package de.hundesportteam.app.ui.training

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.hundesportteam.app.data.local.entity.TrainingPageEntity
import de.hundesportteam.app.data.repository.TrainingRepository
import de.hundesportteam.app.util.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TrainingViewModel @Inject constructor(
    private val trainingRepository: TrainingRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<TrainingUiState>(TrainingUiState.Loading)
    val uiState: StateFlow<TrainingUiState> = _uiState.asStateFlow()

    init {
        loadTrainingPages()
    }

    fun loadTrainingPages(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            trainingRepository.getTrainingPages(forceRefresh).collect { result ->
                _uiState.value = when (result) {
                    is Result.Loading -> TrainingUiState.Loading
                    is Result.Success -> {
                        if (result.data.isEmpty()) {
                            TrainingUiState.Empty
                        } else {
                            TrainingUiState.Success(result.data)
                        }
                    }
                    is Result.Error -> TrainingUiState.Error(result.message ?: "Unknown error")
                }
            }
        }
    }

    fun refresh() {
        loadTrainingPages(forceRefresh = true)
    }

    suspend fun getPageById(id: Int) = trainingRepository.getTrainingPageById(id)
}

sealed class TrainingUiState {
    object Loading : TrainingUiState()
    object Empty : TrainingUiState()
    data class Success(val pages: List<TrainingPageEntity>) : TrainingUiState()
    data class Error(val message: String) : TrainingUiState()
}
