package de.hundesportteam.app.ui.blog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.hundesportteam.app.data.local.entity.BlogPostEntity
import de.hundesportteam.app.data.repository.BlogRepository
import de.hundesportteam.app.util.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BlogViewModel @Inject constructor(
    private val blogRepository: BlogRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<BlogUiState>(BlogUiState.Loading)
    val uiState: StateFlow<BlogUiState> = _uiState.asStateFlow()

    init {
        loadBlogPosts()
    }

    fun loadBlogPosts(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            blogRepository.getBlogPosts(forceRefresh).collect { result ->
                _uiState.value = when (result) {
                    is Result.Loading -> BlogUiState.Loading
                    is Result.Success -> {
                        if (result.data.isEmpty()) {
                            BlogUiState.Empty
                        } else {
                            BlogUiState.Success(result.data)
                        }
                    }
                    is Result.Error -> BlogUiState.Error(result.message)
                }
            }
        }
    }

    fun refresh() {
        loadBlogPosts(forceRefresh = true)
    }

    suspend fun getPostById(id: Int) = blogRepository.getBlogPostById(id)
}

sealed class BlogUiState {
    object Loading : BlogUiState()
    object Empty : BlogUiState()
    data class Success(val posts: List<BlogPostEntity>) : BlogUiState()
    data class Error(val message: String) : BlogUiState()
}
