package com.example.comunicappescolar.ui.screens.registro


import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.comunicappescolar.data.local.SessionDataStore
import com.example.comunicappescolar.data.model.RegisterRequest
import com.example.comunicappescolar.data.remote.AuthInterceptor
import com.example.comunicappescolar.data.remote.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RegistroViewModel(
    private val session: SessionDataStore
) : ViewModel() {

    private val _uiState = MutableStateFlow<RegistroUiState>(RegistroUiState.Idle)
    val uiState: StateFlow<RegistroUiState> = _uiState.asStateFlow()

    // Campos del formulario
    var nombre          by mutableStateOf("")
    var email           by mutableStateOf("")
    var colegio         by mutableStateOf("")
    var password        by mutableStateOf("")
    var confirmar       by mutableStateOf("")
    var rolSeleccionado by mutableStateOf("padre")
    var aceptaTerminos  by mutableStateOf(false)

    // Errores
    var nombreError    by mutableStateOf<String?>(null)
    var emailError     by mutableStateOf<String?>(null)
    var colegioError   by mutableStateOf<String?>(null)
    var passwordError  by mutableStateOf<String?>(null)
    var confirmarError by mutableStateOf<String?>(null)
    var terminosError  by mutableStateOf<String?>(null)

    private fun validar(): Boolean {
        nombreError = if (nombre.isBlank()) "El nombre es obligatorio" else null
        emailError  = when {
            email.isBlank() -> "El correo es obligatorio"
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> "Correo no válido"
            else -> null
        }
        colegioError   = if (colegio.isBlank()) "El colegio es obligatorio" else null
        passwordError  = when {
            password.isBlank()  -> "La contraseña es obligatoria"
            password.length < 6 -> "Mínimo 6 caracteres"
            else -> null
        }
        confirmarError = when {
            confirmar.isBlank()      -> "Confirma tu contraseña"
            confirmar != password    -> "Las contraseñas no coinciden"
            else -> null
        }
        terminosError = if (!aceptaTerminos) "Debes aceptar los términos" else null

        return listOf(nombreError, emailError, colegioError,
            passwordError, confirmarError, terminosError).all { it == null }
    }

    fun registrar() {
        if (!validar()) return
        viewModelScope.launch {
            _uiState.value = RegistroUiState.Loading
            try {
                val request = RegisterRequest(
                    nombre   = nombre.trim(),
                    email    = email.trim(),
                    password = password,
                    rol      = rolSeleccionado
                )
                val response = RetrofitClient.apiService.register(request)
                // Guardar sesión igual que en el login
                AuthInterceptor.token = response.token
                session.guardarSesion(response.token, response.usuario)
                _uiState.value = RegistroUiState.Success
            } catch (e: Exception) {
                _uiState.value = RegistroUiState.Error(
                    "Error al registrarse. El correo puede estar en uso."
                )
            }
        }
    }

    fun resetState() { _uiState.value = RegistroUiState.Idle }
}

class RegistroViewModelFactory(
    private val session: SessionDataStore
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return RegistroViewModel(session = session) as T
    }
}