package com.example.comunicappescolar.ui.screens.tareas


import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.comunicappescolar.data.model.EstadoRequest
import com.example.comunicappescolar.data.model.Tarea
import com.example.comunicappescolar.data.model.TareaRequest
import com.example.comunicappescolar.data.remote.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class TareasUiState {
    object Loading : TareasUiState()
    data class Success(val tareas: List<Tarea>) : TareasUiState()
    data class Error(val mensaje: String) : TareasUiState()
}

sealed class CrearTareaUiState {
    object Idle    : CrearTareaUiState()
    object Loading : CrearTareaUiState()
    object Success : CrearTareaUiState()
    data class Error(val mensaje: String) : CrearTareaUiState()
}

class TareasViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<TareasUiState>(TareasUiState.Loading)
    val uiState: StateFlow<TareasUiState> = _uiState.asStateFlow()

    private val _crearState = MutableStateFlow<CrearTareaUiState>(CrearTareaUiState.Idle)
    val crearState: StateFlow<CrearTareaUiState> = _crearState.asStateFlow()

    // Filtro activo
    var filtroActivo by mutableStateOf("todas")
        private set

    // Campos del formulario de creación
    var nuevoTitulo      by mutableStateOf("")
    var nuevaDescripcion by mutableStateOf("")
    var nuevaMateria     by mutableStateOf("")
    var nuevoGrado       by mutableStateOf("")
    var nuevaFecha       by mutableStateOf("")

    // Errores
    var tituloError   by mutableStateOf<String?>(null)
    var materiaError  by mutableStateOf<String?>(null)
    var fechaError    by mutableStateOf<String?>(null)

    init { cargarTareas() }

    fun cargarTareas() {
        viewModelScope.launch {
            _uiState.value = TareasUiState.Loading
            try {
                val tareas = RetrofitClient.apiService.getTareas()
                _uiState.value = TareasUiState.Success(tareas)
            } catch (e: Exception) {
                _uiState.value = TareasUiState.Error("No se pudieron cargar las tareas.")
            }
        }
    }

    fun cambiarFiltro(filtro: String) { filtroActivo = filtro }

    fun tareasFiltradas(tareas: List<Tarea>): List<Tarea> = when (filtroActivo) {
        "pendientes" -> tareas.filter { it.estado_usuario == "pendiente" }
        "entregadas" -> tareas.filter { it.estado_usuario == "entregada" }
        else         -> tareas
    }

    // Marcar revisión del padre (checkbox)
    fun marcarRevision(tareaId: Int, revisada: Boolean) {
        val estado = _uiState.value
        if (estado is TareasUiState.Success) {
            val nuevo = if (revisada) "vista" else "pendiente"
            val actualizadas = estado.tareas.map {
                if (it.id == tareaId) it.copy(estado_usuario = nuevo) else it
            }
            _uiState.value = TareasUiState.Success(actualizadas)
            viewModelScope.launch {
                try {
                    RetrofitClient.apiService.actualizarEstado(
                        tareaId, EstadoRequest(nuevo))
                } catch (_: Exception) {}
            }
        }
    }

    private fun validar(): Boolean {
        tituloError  = if (nuevoTitulo.isBlank()) "El título es obligatorio" else null
        materiaError = if (nuevaMateria.isBlank()) "La materia es obligatoria" else null
        fechaError   = if (nuevaFecha.isBlank()) "La fecha límite es obligatoria" else null
        return tituloError == null && materiaError == null && fechaError == null
    }

    fun crearTarea() {
        if (!validar()) return
        viewModelScope.launch {
            _crearState.value = CrearTareaUiState.Loading
            try {
                RetrofitClient.apiService.crearTarea(
                    TareaRequest(
                        titulo       = nuevoTitulo.trim(),
                        descripcion  = nuevaDescripcion.trim(),
                        materia      = nuevaMateria.trim(),
                        grado        = nuevoGrado.trim(),
                        fecha_limite = nuevaFecha.trim()
                    )
                )
                nuevoTitulo      = ""
                nuevaDescripcion = ""
                nuevaMateria     = ""
                nuevoGrado       = ""
                nuevaFecha       = ""
                _crearState.value = CrearTareaUiState.Success
                cargarTareas()
            } catch (e: Exception) {
                _crearState.value = CrearTareaUiState.Error("Error al crear la tarea.")
            }
        }
    }

    fun resetCrearState() { _crearState.value = CrearTareaUiState.Idle }
}