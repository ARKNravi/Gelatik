package com.example.bckc.presentation.screens.jbi.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bckc.data.api.ApiService
import com.example.bckc.data.model.response.TranslationOrderResponse
import com.example.bckc.data.model.response.TranslatorResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class JBIViewModel @Inject constructor(
    private val apiService: ApiService
) : ViewModel() {

    private val _selectedTab = mutableStateOf(0)
    val selectedTab: State<Int> = _selectedTab

    private val _searchQuery = mutableStateOf("")
    val searchQuery: State<String> = _searchQuery

    private val _translators = MutableStateFlow<List<TranslatorResponse>>(emptyList())
    val translators: StateFlow<List<TranslatorResponse>> = _translators.asStateFlow()

    private val _translationOrders = MutableStateFlow<List<TranslationOrderResponse>>(emptyList())
    val translationOrders: StateFlow<List<TranslationOrderResponse>> = _translationOrders.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        fetchTranslators()
        fetchTranslationOrders()
    }

    fun onTabSelected(index: Int) {
        _selectedTab.value = index
        if (index == 1 && translationOrders.value.isEmpty()) {
            fetchTranslationOrders()
        }
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun getFilteredTranslators(): List<TranslatorResponse> {
        val query = searchQuery.value.lowercase()
        return if (query.isEmpty()) {
            translators.value
        } else {
            translators.value.filter { translator ->
                translator.alamat.lowercase().contains(query) ||
                translator.name.lowercase().contains(query)
            }
        }
    }

    private fun fetchTranslators() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val response = apiService.getTranslators()
                if (response.isSuccessful) {
                    response.body()?.let { translatorList ->
                        _translators.value = translatorList.items
                    }
                } else {
                    _error.value = "Failed to fetch translators"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "An error occurred"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun fetchTranslationOrders() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val response = apiService.getMyTranslationOrders()
                if (response.isSuccessful) {
                    response.body()?.let { orderList ->
                        _translationOrders.value = orderList.items
                    }
                } else {
                    _error.value = "Failed to fetch translation orders"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "An error occurred"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun onContactTranslator(orderId: Int) {
        // TODO: Implement contact functionality
    }

    fun onCompleteOrder(orderId: Int) {
        // TODO: Implement order completion
    }
}