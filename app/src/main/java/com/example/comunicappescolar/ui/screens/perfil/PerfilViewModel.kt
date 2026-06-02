package com.example.comunicappescolar.ui.screens.perfil


import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.comunicappescolar.data.local.SessionDataStore
import com.example.comunicappescolar.data.model.PerfilRequest
import com.example.comunicappescolar.data.model.Usuario
import com.example.comunicappescolar.data.remote.AuthInterceptor
import com.example.comunicappescolar.data.remote.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class PerfilUiState {
    object Loading : PerfilUiState()
    data class Success(val usuario: Usuario) : PerfilUiState()
    data class Error(val mensaje: String) : PerfilUiState()
}

sealed class EditarPerfilUiState {
    object Idle    : EditarPerfilUiState()
    object Loading : EditarPerfilUiState()
    object Success : EditarPerfilUiState()
    data class Error(val mensaje: String) : EditarPerfilUiState()
}

class PerfilViewModel(private val session: SessionDataStore) : ViewModel() {

    private val _uiState = MutableStateFlow<PerfilUiState>(PerfilUiState.Loading)
    val uiState: StateFlow<PerfilUiState> = _uiState.asStateFlow()

    private val _editarState = MutableStateFlow<EditarPerfilUiState>(EditarPerfilUiState.Idle)
    val editarState: StateFlow<EditarPerfilUiState> = _editarState.asStateFlow()

    // Datos del formulario de edición
    var editNombre   by mutableStateOf("")
    var editTelefono by mutableStateOf("")
    var editGrado    by mutableStateOf("")
    var editColegio  by mutableStateOf("")

    // Errores
    var nombreError by mutableStateOf<String?>(null)

    // ID del usuario actual
    private var usuarioId = 0

    init { cargarPerfil() }

    fun cargarPerfil() {
        viewModelScope.launch {
            _uiState.value = PerfilUiState.Loading
            try {
                val usuario = RetrofitClient.apiService.getMe()
                usuarioId    = usuario.id
                editNombre   = usuario.nombre
                editTelefono = usuario.telefono   ?: ""
                editGrado    = usuario.grado      ?: ""
                editColegio  = usuario.colegio    ?: ""
                _uiState.value = PerfilUiState.Success(usuario)
            } catch (e: Exception) {
                // Fallback: leer desde DataStore
                session.nombre.collect { nombre ->
                    if (!nombre.isNullOrBlank()) {
                        _uiState.value = PerfilUiState.Success(
                            Usuario(
                                id        = 0,
                                nombre    = nombre,
                                email     = "",
                                rol       = "",
                                telefono  = null,
                                grado     = null,
                                colegio   = null,
                                avatar_url = null
                            )
                        )
                    } else {
                        _uiState.value = PerfilUiState.Error("No se pudo cargar el perfil.")
                    }
                }
            }
        }
    }

    fun iniciarEdicion(usuario: Usuario) {
        editNombre   = usuario.nombre
        editTelefono = usuario.telefono ?: ""
        editGrado    = usuario.grado    ?: ""
        editColegio  = usuario.colegio  ?: ""
    }

    private fun validar(): Boolean {
        nombreError = if (editNombre.isBlank()) "El nombre es obligatorio" else null
        return nombreError == null
    }

    fun guardarCambios() {
        if (!validar()) return
        viewModelScope.launch {
            _editarState.value = EditarPerfilUiState.Loading
            try {
                val actualizado = RetrofitClient.apiService.actualizarPerfil(
                    usuarioId,
                    PerfilRequest(nombre = editNombre.trim(), telefono = editTelefono.trim())
                )
                // Actualizar sesión local
                session.guardarSesion(AuthInterceptor.token ?: "", actualizado)
                _uiState.value = PerfilUiState.Success(actualizado)
                _editarState.value = EditarPerfilUiState.Success
            } catch (e: Exception) {
                _editarState.value = EditarPerfilUiState.Error("Error al guardar los cambios.")
            }
        }
    }

    fun cerrarSesion() {
        viewModelScope.launch {
            session.cerrarSesion()
            AuthInterceptor.token = null
        }
    }

    fun resetEditarState() { _editarState.value = EditarPerfilUiState.Idle }
}

class PerfilViewModelFactory(
    private val session: SessionDataStore
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        PerfilViewModel(session) as T
}