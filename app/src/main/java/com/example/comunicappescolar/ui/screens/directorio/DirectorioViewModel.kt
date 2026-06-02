package com.example.comunicappescolar.ui.screens.directorio


import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.comunicappescolar.data.model.Usuario
import com.example.comunicappescolar.data.remote.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class DirectorioUiState {
    object Loading : DirectorioUiState()
    data class Success(val docentes: List<Usuario>) : DirectorioUiState()
    data class Error(val mensaje: String) : DirectorioUiState()
}

class DirectorioViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<DirectorioUiState>(DirectorioUiState.Loading)
    val uiState: StateFlow<DirectorioUiState> = _uiState.asStateFlow()

    var textoBusqueda   by mutableStateOf("")
    var filtroMateria   by mutableStateOf("Todo el personal")

    init { cargarDocentes() }

    fun cargarDocentes() {
        viewModelScope.launch {
            _uiState.value = DirectorioUiState.Loading
            try {
                // Traer todos los usuarios y filtrar solo docentes
                val todos    = RetrofitClient.apiService.getUsuarios()
                val docentes = todos.filter { it.rol == "docente" }
                _uiState.value = DirectorioUiState.Success(docentes)
            } catch (e: Exception) {
                _uiState.value = DirectorioUiState.Error("No se pudo cargar el directorio.")
            }
        }
    }

    // Materias únicas para los chips
    fun materias(docentes: List<Usuario>): List<String> {
        val lista = docentes.mapNotNull { it.grado }.distinct().sorted()
        return listOf("Todo el personal") + lista
    }

    // Filtrado por búsqueda + materia
    fun docentesFiltrados(docentes: List<Usuario>): List<Usuario> {
        var resultado = docentes
        if (filtroMateria != "Todo el personal") {
            resultado = resultado.filter {
                it.grado?.contains(filtroMateria, ignoreCase = true) == true
            }
        }
        if (textoBusqueda.isNotBlank()) {
            resultado = resultado.filter {
                it.nombre.contains(textoBusqueda, ignoreCase = true) ||
                        it.grado?.contains(textoBusqueda, ignoreCase = true) == true
            }
        }
        return resultado
    }

    // Colores de avatar por índice (cicla entre varios)
    fun colorAvatar(index: Int): Pair<androidx.compose.ui.graphics.Color, androidx.compose.ui.graphics.Color> {
        val colores = listOf(
            androidx.compose.ui.graphics.Color(0xFFD6E3FF) to androidx.compose.ui.graphics.Color(0xFF004D99),
            androidx.compose.ui.graphics.Color(0xFF54A0FE).copy(.3f) to androidx.compose.ui.graphics.Color(0xFF003567),
            androidx.compose.ui.graphics.Color(0xFFA3F69C) to androidx.compose.ui.graphics.Color(0xFF002204),
        )
        return colores[index % colores.size]
    }
}