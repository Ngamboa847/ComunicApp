package com.example.comunicappescolar.ui.screens.calificaciones


import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.comunicappescolar.data.model.Calificacion
import com.example.comunicappescolar.data.remote.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class CalificacionesUiState {
    data object Loading : CalificacionesUiState()
    data class Success(val calificaciones: List<Calificacion>) : CalificacionesUiState()
    data class Error(val mensaje: String) : CalificacionesUiState()
}

sealed class RegistrarNotaUiState {
    data object Idle    : RegistrarNotaUiState()
    data object Loading : RegistrarNotaUiState()
    data object Success : RegistrarNotaUiState()
    data class Error(val mensaje: String) : RegistrarNotaUiState()
}

// Modelo para registrar nota (solo docente)
data class RegistrarNotaRequest(
    val estudiante_id: Int,
    val materia:       String,
    val nota:          Double,
    val periodo:       Int
)

class CalificacionesViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<CalificacionesUiState>(CalificacionesUiState.Loading)
    val uiState: StateFlow<CalificacionesUiState> = _uiState.asStateFlow()

    private val _registrarState = MutableStateFlow<RegistrarNotaUiState>(RegistrarNotaUiState.Idle)
    val registrarState: StateFlow<RegistrarNotaUiState> = _registrarState.asStateFlow()

    var periodoActivo by mutableIntStateOf(1)
        private set

    // Campos formulario de registro de nota
    var estudianteId   by mutableStateOf("")
    var nuevaMateria   by mutableStateOf("")
    var nuevaNota      by mutableStateOf("")
    var nuevoPeriodo   by mutableStateOf("1")

    // Errores
    var estudianteError by mutableStateOf<String?>(null)
    var materiaError    by mutableStateOf<String?>(null)
    var notaError       by mutableStateOf<String?>(null)

    init { cargarCalificaciones() }

    fun cargarCalificaciones() {
        viewModelScope.launch {
            _uiState.value = CalificacionesUiState.Loading
            try {
                val cals = RetrofitClient.apiService.getCalificaciones(periodoActivo)
                _uiState.value = CalificacionesUiState.Success(cals)
            } catch (e: Exception) {
                _uiState.value = CalificacionesUiState.Error("No se pudieron cargar las calificaciones.")
            }
        }
    }

    fun cambiarPeriodo(periodo: Int) {
        periodoActivo = periodo
        cargarCalificaciones()
    }

    // Calcular promedio general
    fun promedio(cals: List<Calificacion>): Double =
        if (cals.isEmpty()) 0.0
        else cals.map { it.nota }.average()

    // Determinar nivel de rendimiento
    fun nivelRendimiento(promedio: Double): String = when {
        promedio >= 4.5 -> "Excelente Nivel"
        promedio >= 4.0 -> "Muy Buen Nivel"
        promedio >= 3.5 -> "Buen Nivel"
        promedio >= 3.0 -> "Nivel Promedio"
        else            -> "Nivel Crítico"
    }

    // Color y badge por nota
    fun badgeInfo(nota: Double): Pair<androidx.compose.ui.graphics.Color, String> {
        val blue800    = androidx.compose.ui.graphics.Color(0xFF1565C0)
        val greenDark  = androidx.compose.ui.graphics.Color(0xFF25752B)
        val gray500    = androidx.compose.ui.graphics.Color(0xFF727783)
        val errorRed   = androidx.compose.ui.graphics.Color(0xFFBA1A1A)

        return when {
            nota >= 4.8 -> greenDark to "Sobresaliente"
            nota >= 4.5 -> greenDark to "Excelente"
            nota >= 4.0 -> blue800   to "Bueno"
            nota >= 3.5 -> blue800   to "Promedio"
            nota >= 3.0 -> gray500   to "Regular"
            else        -> errorRed  to "Crítico"
        }
    }

    private fun validar(): Boolean {
        estudianteError = if (estudianteId.isBlank() || estudianteId.toIntOrNull() == null)
            "ID de estudiante inválido" else null
        materiaError = if (nuevaMateria.isBlank()) "La materia es obligatoria" else null
        notaError = when {
            nuevaNota.isBlank()                        -> "La nota es obligatoria"
            nuevaNota.toDoubleOrNull() == null         -> "Nota inválida"
            nuevaNota.toDouble() !in 0.0..5.0          -> "La nota debe estar entre 0.0 y 5.0"
            else                                       -> null
        }
        return estudianteError == null && materiaError == null && notaError == null
    }

    fun registrarNota() {
        if (!validar()) return
        viewModelScope.launch {
            _registrarState.value = RegistrarNotaUiState.Loading
            try {
                val request = RegistrarNotaRequest(
                    estudiante_id = estudianteId.toInt(),
                    materia = nuevaMateria.trim(),
                    nota = nuevaNota.toDouble(),
                    periodo = nuevoPeriodo.toIntOrNull() ?: 1
                )
                RetrofitClient.apiService.registrarCalificacion(request)

                estudianteId = ""
                nuevaMateria = ""
                nuevaNota    = ""
                _registrarState.value = RegistrarNotaUiState.Success
                cargarCalificaciones()
            } catch (e: Exception) {
                _registrarState.value = RegistrarNotaUiState.Error("Error al registrar la nota.")
            }
        }
    }

    fun resetRegistrarState() { _registrarState.value = RegistrarNotaUiState.Idle }
}