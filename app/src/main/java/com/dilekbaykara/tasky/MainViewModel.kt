package com.dilekbaykara.tasky

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainViewModel: ViewModel() {
    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    fun updateName(name: String) {
        val errors = mutableListOf<String>()
        if (name.length < 4) {
            errors.add("The name must be at least 4 characters.")
        }
        _uiState.value = _uiState.value.copy(
            currentName = name,
            currentNameErrors = errors
        )
    }
}