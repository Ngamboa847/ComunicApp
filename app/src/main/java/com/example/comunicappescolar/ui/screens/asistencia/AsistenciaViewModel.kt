package com.example.comunicappescolar.ui.screens.asistencia


import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.comunicappescolar.data.model.Asistencia
import com.example.comunicappescolar.data.model.RegistrarAsistenciaRequest
import com.example.comunicappescolar.data.remote.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class AsistenciaUiState {
    object Loading : AsistenciaUiState()
    data class Success(val registros: List<Asistencia>) : AsistenciaUiState()
    data class Error(val mensaje: String) : AsistenciaUiState()
}

sealed class RegistrarAsistenciaUiState {
    object Idle    : RegistrarAsistenciaUiState()
    object Loading : RegistrarAsistenciaUiState()
    object Success : RegistrarAsistenciaUiState()
    data class Error(val mensaje: String) : RegistrarAsistenciaUiState()
}

class AsistenciaViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<AsistenciaUiState>(AsistenciaUiState.Loading)
    val uiState: StateFlow<AsistenciaUiState> = _uiState.asStateFlow()

    private val _registrarState = MutableStateFlow<RegistrarAsistenciaUiState>(RegistrarAsistenciaUiState.Idle)
    val registrarState: StateFlow<RegistrarAsistenciaUiState> = _registrarState.asStateFlow()

    // Campos del formulario de registro
    var estudianteId   by mutableStateOf("")
    var fechaRegistro  by mutableStateOf("")
    var estadoRegistro by mutableStateOf("presente")

    // Errores
    var estudianteError by mutableStateOf<String?>(null)
    var fechaError      by mutableStateOf<String?>(null)

    // Mes seleccionado para filtro
    var mesActivo by mutableStateOf("Septiembre 2023")

    init { cargarAsistencia() }

    fun cargarAsistencia() {
        viewModelScope.launch {
            _uiState.value = AsistenciaUiState.Loading
            try {
                val registros = RetrofitClient.apiService.getAsistencia()
                _uiState.value = AsistenciaUiState.Success(registros)
            } catch (e: Exception) {
                _uiState.value = AsistenciaUiState.Error("No se pudo cargar la asistencia.")
            }
        }
    }

    // Calcular porcentaje de asistencia
    fun porcentaje(registros: List<Asistencia>): Int {
        if (registros.isEmpty()) return 0
        val presentes = registros.count { it.estado == "presente" || it.estado == "tardanza" }
        return ((presentes.toFloat() / registros.size) * 100).toInt()
    }

    fun totalPresentes(registros: List<Asistencia>) =
        registros.count { it.estado == "presente" }

    fun totalAusentes(registros: List<Asistencia>) =
        registros.count { it.estado == "ausente" }

    fun totalTardanzas(registros: List<Asistencia>) =
        registros.count { it.estado == "tardanza" }

    private fun validar(): Boolean {
        estudianteError = if (estudianteId.isBlank() || estudianteId.toIntOrNull() == null)
            "ID inválido" else null
        fechaError = if (fechaRegistro.isBlank()) "La fecha es obligatoria" else null
        return estudianteError == null && fechaError == null
    }

    fun registrarAsistencia() {
        if (!validar()) return
        viewModelScope.launch {
            _registrarState.value = RegistrarAsistenciaUiState.Loading
            try {
                RetrofitClient.apiService.registrarAsistencia(
                    RegistrarAsistenciaRequest(
                        estudiante_id = estudianteId.toInt(),
                        fecha         = fechaRegistro.trim(),
                        estado        = estadoRegistro
                    )
                )
                estudianteId   = ""
                fechaRegistro  = ""
                estadoRegistro = "presente"
                _registrarState.value = RegistrarAsistenciaUiState.Success
                cargarAsistencia()
            } catch (e: Exception) {
                _registrarState.value = RegistrarAsistenciaUiState.Error("Error al registrar.")
            }
        }
    }

    fun resetRegistrarState() { _registrarState.value = RegistrarAsistenciaUiState.Idle }
}