package com.example.comunicappescolar.ui.screens.login


import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.comunicappescolar.data.local.SessionDataStore
import com.example.comunicappescolar.data.remote.AuthInterceptor
import com.example.comunicappescolar.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    private val repository: AuthRepository = AuthRepository(),
    private val session: SessionDataStore
) : ViewModel() {

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    // Campos del formulario
    var email           by mutableStateOf("")
    var password        by mutableStateOf("")
    var rolSeleccionado by mutableStateOf("padre")  // "padre" | "docente"
    var passwordVisible by mutableStateOf(false)

    // Errores de validación
    var emailError    by mutableStateOf<String?>(null)
    var passwordError by mutableStateOf<String?>(null)

    private fun validar(): Boolean {
        emailError = when {
            email.isBlank() -> "El correo es obligatorio"
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> "Correo no válido"
            else -> null
        }
        passwordError = when {
            password.isBlank()  -> "La contraseña es obligatoria"
            password.length < 6 -> "Mínimo 6 caracteres"
            else -> null
        }
        return emailError == null && passwordError == null
    }

    fun login() {
        if (!validar()) return
        viewModelScope.launch {
            _uiState.value = LoginUiState.Loading
            try {
                val response = repository.login(email, password)
                // Guardar token en el interceptor para peticiones futuras
                AuthInterceptor.token = response.token
                // Guardar sesión completa en DataStore
                session.guardarSesion(response.token, response.usuario)
                _uiState.value = LoginUiState.Success
            } catch (e: Exception) {
                _uiState.value = LoginUiState.Error("Credenciales incorrectas. Intenta de nuevo.")
            }
        }
    }

    fun resetState() { _uiState.value = LoginUiState.Idle }
}


class LoginViewModelFactory(
    private val session: SessionDataStore
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return LoginViewModel(session = session) as T
    }
}