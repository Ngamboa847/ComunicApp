package com.example.comunicappescolar.ui.screens.mensajes


import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.comunicappescolar.data.model.Conversacion
import com.example.comunicappescolar.data.model.Mensaje
import com.example.comunicappescolar.data.model.MensajeRequest
import com.example.comunicappescolar.data.model.Usuario
import com.example.comunicappescolar.data.remote.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class ConversacionesUiState {
    object Loading : ConversacionesUiState()
    data class Success(val conversaciones: List<Conversacion>) : ConversacionesUiState()
    data class Error(val mensaje: String) : ConversacionesUiState()
}

sealed class ChatUiState {
    object Loading : ChatUiState()
    data class Success(val mensajes: List<Mensaje>) : ChatUiState()
    data class Error(val mensaje: String) : ChatUiState()
}

class MensajesViewModel : ViewModel() {

    private val _convState = MutableStateFlow<ConversacionesUiState>(ConversacionesUiState.Loading)
    val convState: StateFlow<ConversacionesUiState> = _convState.asStateFlow()

    private val _chatState = MutableStateFlow<ChatUiState>(ChatUiState.Loading)
    val chatState: StateFlow<ChatUiState> = _chatState.asStateFlow()

    private val _usuarios = MutableStateFlow<List<Usuario>>(emptyList())
    val usuarios: StateFlow<List<Usuario>> = _usuarios.asStateFlow()

    // Campo del mensaje nuevo
    var textoMensaje by mutableStateOf("")

    // Búsqueda en lista de conversaciones
    var textoBusqueda by mutableStateOf("")

    init { cargarConversaciones() }

    fun cargarConversaciones() {
        viewModelScope.launch {
            _convState.value = ConversacionesUiState.Loading
            try {
                val conv = RetrofitClient.apiService.getConversaciones()
                _convState.value = ConversacionesUiState.Success(conv)
            } catch (e: Exception) {
                _convState.value = ConversacionesUiState.Error("No se pudieron cargar los mensajes.")
            }
        }
    }

    fun cargarChat(contactoId: Int) {
        viewModelScope.launch {
            _chatState.value = ChatUiState.Loading
            try {
                Log.d("ChatVM", "Cargando chat con contactoId: $contactoId")
                val msgs = RetrofitClient.apiService.getChat(contactoId)
                Log.d("ChatVM", "Mensajes recibidos: ${msgs.size}")
                _chatState.value = ChatUiState.Success(msgs)
            } catch (e: Exception) {
                Log.e("ChatVM", "Error: ${e.message}", e)
                _chatState.value = ChatUiState.Error("No se pudo cargar el chat.")
            }
        }
    }

    fun enviarMensaje(receptorId: Int, usuarioId: Int) {
        if (textoMensaje.isBlank()) return
        val texto = textoMensaje.trim()
        textoMensaje = ""

        // Agregar mensaje optimista a la UI
        val estado = _chatState.value
        if (estado is ChatUiState.Success) {
            val nuevo = Mensaje(
                id              = System.currentTimeMillis().toInt(),
                remitente_id    = usuarioId,
                receptor_id     = receptorId,
                contenido       = texto,
                leido           = false,  // ← de 0 a false
                created_at      = "Ahora",
                remitente_nombre = "Yo"
            )
            _chatState.value = ChatUiState.Success(estado.mensajes + nuevo)
        }

        viewModelScope.launch {
            try {
                RetrofitClient.apiService.enviarMensaje(MensajeRequest(receptorId, texto))
            } catch (_: Exception) {}
        }
    }

    fun cargarUsuarios() {
        viewModelScope.launch {
            try {
                _usuarios.value = RetrofitClient.apiService.getUsuarios()
            } catch (_: Exception) {}
        }
    }

    fun conversacionesFiltradas(lista: List<Conversacion>): List<Conversacion> =
        if (textoBusqueda.isBlank()) lista
        else lista.filter {
            it.contacto_nombre.contains(textoBusqueda, ignoreCase = true)
        }
}