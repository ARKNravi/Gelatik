package com.example.bckc.presentation.screens.forum.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bckc.data.api.ApiService
import com.example.bckc.data.model.response.ForumResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ForumViewModel @Inject constructor(
    private val apiService: ApiService
) : ViewModel() {

    private val _forumState = MutableStateFlow<ForumUiState>(ForumUiState.Loading)
    val forumState: StateFlow<ForumUiState> = _forumState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _selectedTab = MutableStateFlow(0)
    val selectedTab = _selectedTab.asStateFlow()

    init {
        loadForumData()
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSelectedTab(index: Int) {
        _selectedTab.value = index
    }

    private fun loadForumData() {
        viewModelScope.launch {
            try {
                val response = apiService.getSummaries()
                if (response.isSuccessful) {
                    response.body()?.let { forums ->
                        _forumState.value = ForumUiState.Success(forums)
                    } ?: run {
                        _forumState.value = ForumUiState.Error("No data available")
                    }
                } else {
                    _forumState.value = ForumUiState.Error("Failed to load forum data")
                }
            } catch (e: Exception) {
                _forumState.value = ForumUiState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }
}

sealed class ForumUiState {
    object Loading : ForumUiState()
    data class Success(val forums: List<ForumResponse>) : ForumUiState()
    data class Error(val message: String) : ForumUiState()
}
