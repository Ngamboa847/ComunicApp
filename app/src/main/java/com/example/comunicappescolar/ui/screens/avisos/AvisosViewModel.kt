package com.example.comunicappescolar.ui.screens.avisos

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.comunicappescolar.data.model.Aviso
import com.example.comunicappescolar.data.model.AvisoRequest
import com.example.comunicappescolar.data.remote.AuthInterceptor
import com.example.comunicappescolar.data.remote.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class AvisosUiState {
    object Loading : AvisosUiState()
    data class Success(val avisos: List<Aviso>) : AvisosUiState()
    data class Error(val mensaje: String) : AvisosUiState()
}

sealed class CrearAvisoUiState {
    object Idle    : CrearAvisoUiState()
    object Loading : CrearAvisoUiState()
    object Success : CrearAvisoUiState()
    data class Error(val mensaje: String) : CrearAvisoUiState()
}

class AvisosViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<AvisosUiState>(AvisosUiState.Loading)
    val uiState: StateFlow<AvisosUiState> = _uiState.asStateFlow()

    private val _crearState = MutableStateFlow<CrearAvisoUiState>(CrearAvisoUiState.Idle)
    val crearState: StateFlow<CrearAvisoUiState> = _crearState.asStateFlow()

    var filtroActivo by mutableStateOf("todos")
        private set

    // Campos del formulario de creación
    var nuevoTitulo      by mutableStateOf("")
    var nuevaDescripcion by mutableStateOf("")
    var nuevoContenido   by mutableStateOf("")
    var nuevaCategoria   by mutableStateOf("info")

    // Errores
    var tituloError      by mutableStateOf<String?>(null)
    var descripcionError by mutableStateOf<String?>(null)
    var contenidoError   by mutableStateOf<String?>(null)

    init { cargarAvisos() }

    fun cargarAvisos() {
        viewModelScope.launch {
            _uiState.value = AvisosUiState.Loading
            try {
                Log.d("AvisosVM", "Token: ${AuthInterceptor.token}")
                val avisos = RetrofitClient.apiService.getAvisos()
                _uiState.value = AvisosUiState.Success(avisos)
            } catch (e: Exception) {
                Log.e("AvisosVM", "Error: ${e.message}", e)
                _uiState.value = AvisosUiState.Error("No se pudieron cargar los avisos.")
            }
        }
    }

    fun cambiarFiltro(filtro: String) { filtroActivo = filtro }

    fun marcarLeido(id: Int) {
        val estado = _uiState.value
        if (estado is AvisosUiState.Success) {
            val actualizados = estado.avisos.map {
                if (it.id == id) it.copy(leido = 1) else it
            }
            _uiState.value = AvisosUiState.Success(actualizados)
            viewModelScope.launch {
                try { RetrofitClient.apiService.marcarAvisoLeido(id) } catch (_: Exception) {}
            }
        }
    }

    fun avisosFiltrados(avisos: List<Aviso>): List<Aviso> =
        if (filtroActivo == "todos") avisos
        else avisos.filter { it.categoria == filtroActivo }

    fun noLeidos(avisos: List<Aviso>) = avisos.count { it.leido == 0 }

    private fun validarNuevoAviso(): Boolean {
        tituloError      = if (nuevoTitulo.isBlank()) "El título es obligatorio" else null
        descripcionError = if (nuevaDescripcion.isBlank()) "La descripción es obligatoria" else null
        contenidoError   = if (nuevoContenido.isBlank()) "El contenido es obligatorio" else null
        return tituloError == null && descripcionError == null && contenidoError == null
    }

    fun crearAviso() {
        if (!validarNuevoAviso()) return
        viewModelScope.launch {
            _crearState.value = CrearAvisoUiState.Loading
            try {
                RetrofitClient.apiService.crearAviso(
                    AvisoRequest(
                        titulo      = nuevoTitulo.trim(),
                        descripcion = nuevaDescripcion.trim(),
                        contenido   = nuevoContenido.trim(),
                        categoria   = nuevaCategoria
                    )
                )
                // Limpiar campos
                nuevoTitulo      = ""
                nuevaDescripcion = ""
                nuevoContenido   = ""
                nuevaCategoria   = "info"
                _crearState.value = CrearAvisoUiState.Success
                cargarAvisos() // Recargar lista
            } catch (e: Exception) {
                _crearState.value = CrearAvisoUiState.Error("Error al publicar el aviso.")
            }
        }
    }

    fun resetCrearState() { _crearState.value = CrearAvisoUiState.Idle }
}