package com.example.comunicappescolar.ui.screens.registro


sealed class RegistroUiState {
    object Idle    : RegistroUiState()
    object Loading : RegistroUiState()
    object Success : RegistroUiState()
    data class Error(val mensaje: String) : RegistroUiState()
}