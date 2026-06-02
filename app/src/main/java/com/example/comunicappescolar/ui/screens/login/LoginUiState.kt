package com.example.comunicappescolar.ui.screens.login


sealed class LoginUiState {
    object Idle    : LoginUiState()
    object Loading : LoginUiState()
    object Success : LoginUiState()
    data class Error(val mensaje: String) : LoginUiState()
}