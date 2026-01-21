package de.hundesportteam.app.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.hundesportteam.app.data.repository.BlogRepository
import de.hundesportteam.app.data.repository.PageRepository
import de.hundesportteam.app.data.repository.TrainingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface DetailUiState {
    object Loading : DetailUiState
    data class Success(val title: String, val content: String, val imageUrl: String?) : DetailUiState
    data class Error(val message: String) : DetailUiState
}

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val blogRepository: BlogRepository,
    private val pageRepository: PageRepository,
    private val trainingRepository: TrainingRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<DetailUiState>(DetailUiState.Loading)
    val uiState: StateFlow<DetailUiState> = _uiState

    fun loadContent(type: String, id: Int, title: String) {
        viewModelScope.launch {
            _uiState.value = DetailUiState.Loading
            try {
                when (type) {
                    "blog" -> {
                        val post = blogRepository.getBlogPostById(id)
                        if (post != null) {
                            _uiState.value = DetailUiState.Success(post.title, post.content, post.imageUrl)
                        } else {
                            _uiState.value = DetailUiState.Error("Blogbeitrag nicht gefunden")
                        }
                    }
                    "page" -> {
                        val page = pageRepository.getPage(id)
                        if (page != null) {
                            _uiState.value = DetailUiState.Success(page.title, page.content, page.imageUrl)
                        } else {
                            _uiState.value = DetailUiState.Error("Seite nicht gefunden")
                        }
                    }
                    "training" -> {
                        val page = trainingRepository.getTrainingPage(id)
                        if (page != null) {
                            _uiState.value = DetailUiState.Success(page.title, page.content, page.imageUrl)
                        } else {
                            _uiState.value = DetailUiState.Error("Trainings-Seite nicht gefunden")
                        }
                    }
                    else -> {
                        _uiState.value = DetailUiState.Error("Unbekannter Inhaltstyp")
                    }
                }
            } catch (e: Exception) {
                _uiState.value = DetailUiState.Error(e.message ?: "Ein unbekannter Fehler ist aufgetreten")
            }
        }
    }
}
