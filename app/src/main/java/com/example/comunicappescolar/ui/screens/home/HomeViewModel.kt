package com.example.comunicappescolar.ui.screens.home


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.comunicappescolar.data.local.SessionDataStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ActividadReciente(
    val icono:       androidx.compose.ui.graphics.vector.ImageVector,
    val colorFondo:  androidx.compose.ui.graphics.Color,
    val colorIcono:  androidx.compose.ui.graphics.Color,
    val titulo:      String,
    val descripcion: String,
    val tiempo:      String
)

class HomeViewModel(private val session: SessionDataStore) : ViewModel() {

    private val _nombre = MutableStateFlow("")
    val nombre: StateFlow<String> = _nombre.asStateFlow()

    private val _avatar = MutableStateFlow<String?>(null)
    val avatar: StateFlow<String?> = _avatar.asStateFlow()

    private val _rol = MutableStateFlow("")
    val rol: StateFlow<String> = _rol.asStateFlow()

    init {
        viewModelScope.launch {
            session.nombre.collect { _nombre.value = it ?: "" }
        }
        viewModelScope.launch {
            session.rol.collect { _rol.value = it ?: "" }
        }
    }
}

class HomeViewModelFactory(
    private val session: SessionDataStore
) : androidx.lifecycle.ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HomeViewModel(session) as T
    }
}