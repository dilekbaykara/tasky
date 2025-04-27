package com.dilekbaykara.tasky

data class MainUiState(
    val currentName: String = "",
    val currentNameErrors: MutableList<String> = mutableListOf(),
)