package de.hundesportteam.app.ui.pages

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.hundesportteam.app.data.local.entity.PageEntity
import de.hundesportteam.app.data.repository.PageRepository
import de.hundesportteam.app.util.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PageViewModel @Inject constructor(
    private val pageRepository: PageRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<PageUiState>(PageUiState.Loading)
    val uiState: StateFlow<PageUiState> = _uiState.asStateFlow()

    init {
        loadPages()
    }

    fun loadPages(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            pageRepository.getPages(forceRefresh).let { pages ->
                val filteredPages = pages.filter { it.slug != "trainingsordnung" && !it.link.contains("/trainingsordnung/") }
                _uiState.value = if (filteredPages.isNotEmpty()) {
                    PageUiState.Success(filteredPages)
                } else {
                    PageUiState.Empty
                }
            }
        }
    }

    fun refresh() {
        loadPages(forceRefresh = true)
    }

    suspend fun getPageById(id: Int) = pageRepository.getPage(id)
}

sealed class PageUiState {
    object Loading : PageUiState()
    object Empty : PageUiState()
    data class Success(val pages: List<PageEntity>) : PageUiState()
    data class Error(val message: String) : PageUiState()
}