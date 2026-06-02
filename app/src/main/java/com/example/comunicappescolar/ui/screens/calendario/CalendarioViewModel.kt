package com.example.comunicappescolar.ui.screens.calendario


import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.comunicappescolar.data.model.CrearEventoRequest
import com.example.comunicappescolar.data.model.Evento
import com.example.comunicappescolar.data.remote.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

sealed class CalendarioUiState {
    object Loading : CalendarioUiState()
    data class Success(val eventos: List<Evento>) : CalendarioUiState()
    data class Error(val mensaje: String) : CalendarioUiState()
}

sealed class CrearEventoUiState {
    object Idle    : CrearEventoUiState()
    object Loading : CrearEventoUiState()
    object Success : CrearEventoUiState()
    data class Error(val mensaje: String) : CrearEventoUiState()
}

data class DiaCalendario(
    val fecha:     LocalDate,
    val diaNombre: String,
    val diaNum:    Int,
    val tieneEvento: Boolean = false
)

class CalendarioViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<CalendarioUiState>(CalendarioUiState.Loading)
    val uiState: StateFlow<CalendarioUiState> = _uiState.asStateFlow()

    private val _crearState = MutableStateFlow<CrearEventoUiState>(CrearEventoUiState.Idle)
    val crearState: StateFlow<CrearEventoUiState> = _crearState.asStateFlow()

    var diaSeleccionado by mutableStateOf(LocalDate.now())
        private set

    // Campos del formulario
    var nuevoTitulo      by mutableStateOf("")
    var nuevaDescripcion by mutableStateOf("")
    var nuevaFecha       by mutableStateOf("")
    var nuevaHoraInicio  by mutableStateOf("")
    var nuevaHoraFin     by mutableStateOf("")
    var nuevoLugar       by mutableStateOf("")
    var nuevoTipo        by mutableStateOf("academico")
    var nuevoGrado       by mutableStateOf("Todos los Grados")

    // Errores
    var tituloError by mutableStateOf<String?>(null)
    var fechaError  by mutableStateOf<String?>(null)

    // Semana actual
    val diasSemana: List<DiaCalendario>
        get() {
            val hoy    = LocalDate.now()
            val lunes  = hoy.minusDays(hoy.dayOfWeek.value.toLong() - 1)
            return (0..6).map { i ->
                val fecha = lunes.plusDays(i.toLong())
                DiaCalendario(
                    fecha     = fecha,
                    diaNombre = fecha.dayOfWeek
                        .getDisplayName(TextStyle.SHORT, Locale("es")),
                    diaNum    = fecha.dayOfMonth
                )
            }
        }

    val mesAnio: String
        get() {
            val formatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale("es"))
            return diaSeleccionado.format(formatter)
                .replaceFirstChar { it.uppercase() }
        }

    init { cargarEventos() }

    fun cargarEventos() {
        viewModelScope.launch {
            _uiState.value = CalendarioUiState.Loading
            try {
                val eventos = RetrofitClient.apiService.getEventos()
                _uiState.value = CalendarioUiState.Success(eventos)
            } catch (e: Exception) {
                _uiState.value = CalendarioUiState.Error("No se pudieron cargar los eventos.")
            }
        }
    }

    fun seleccionarDia(dia: LocalDate) { diaSeleccionado = dia }

    fun eventosFiltrados(eventos: List<Evento>): List<Evento> =
        eventos.filter { it.fecha.startsWith(diaSeleccionado.toString()) }

    private fun validar(): Boolean {
        tituloError = if (nuevoTitulo.isBlank()) "El título es obligatorio" else null
        fechaError  = if (nuevaFecha.isBlank()) "La fecha es obligatoria" else null
        return tituloError == null && fechaError == null
    }

    fun crearEvento() {
        if (!validar()) return
        viewModelScope.launch {
            _crearState.value = CrearEventoUiState.Loading
            try {
                RetrofitClient.apiService.crearEvento(
                    CrearEventoRequest(
                        titulo      = nuevoTitulo.trim(),
                        descripcion = nuevaDescripcion.trim(),
                        fecha       = nuevaFecha.trim(),
                        hora_inicio = nuevaHoraInicio.trim(),
                        hora_fin    = nuevaHoraFin.trim(),
                        lugar       = nuevoLugar.trim(),
                        tipo        = nuevoTipo,
                        grado       = nuevoGrado.trim()
                    )
                )
                limpiarFormulario()
                _crearState.value = CrearEventoUiState.Success
                cargarEventos()
            } catch (e: Exception) {
                _crearState.value = CrearEventoUiState.Error("Error al crear el evento.")
            }
        }
    }

    private fun limpiarFormulario() {
        nuevoTitulo      = ""
        nuevaDescripcion = ""
        nuevaFecha       = ""
        nuevaHoraInicio  = ""
        nuevaHoraFin     = ""
        nuevoLugar       = ""
        nuevoTipo        = "academico"
        nuevoGrado       = "Todos los Grados"
    }

    fun resetCrearState() { _crearState.value = CrearEventoUiState.Idle }
}