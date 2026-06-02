package com.example.comunicappescolar.ui.screens.mensajes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.comunicappescolar.data.local.SessionDataStore
import com.example.comunicappescolar.data.model.Mensaje

// ── Colores ──────────────────────────────────────────────────
private val Blue900   = Color(0xFF004D99)
private val Blue800   = Color(0xFF1565C0)
private val BlueFixed = Color(0xFFD6E3FF)
private val Gray500   = Color(0xFF727783)
private val Gray200   = Color(0xFFC2C6D4)
private val White     = Color.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    navController: NavController,
    contactoId:    Int,
    contactoNombre: String = "Contacto",
    viewModel:     MensajesViewModel = viewModel()
) {
    val context   = LocalContext.current
    val session   = remember { SessionDataStore(context) }
    val chatState by viewModel.chatState.collectAsState()
    val listState = rememberLazyListState()

    val miIdStr by session.id.collectAsState(initial = "0")
    val miId    = miIdStr?.toIntOrNull() ?: 0

    // Nombre del contacto desde la lista de conversaciones
    val contactoNombre = (viewModel.convState.value as? ConversacionesUiState.Success)
        ?.conversaciones?.find { it.contacto_id == contactoId }
        ?.contacto_nombre ?: "Contacto"

    LaunchedEffect(contactoId) {
        viewModel.cargarChat(contactoId)
    }

    // Scroll al último mensaje cuando llegan nuevos
    LaunchedEffect(chatState) {
        if (chatState is ChatUiState.Success) {
            val size = (chatState as ChatUiState.Success).mensajes.size
            if (size > 0) listState.animateScrollToItem(size - 1)
        }
    }

    Scaffold(
        topBar = {
            Surface(shadowElevation = 2.dp, color = White) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                        .padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Blue900)
                    }
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(BlueFixed),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(contactoNombre.take(2).uppercase(),
                            fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Blue900)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(contactoNombre, fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold, color = Color(0xFF191C21))
                        Text("En línea", fontSize = 11.sp, color = Color(0xFF25752B))
                    }
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.MoreVert, null, tint = Gray500)
                    }
                }
            }
        },
        containerColor = Color(0xFFECEDF6)
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {

            // ── Lista de mensajes ────────────────────────────
            Box(modifier = Modifier.weight(1f)) {
                when (val estado = chatState) {
                    is ChatUiState.Loading -> {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = Blue900)
                        }
                    }
                    is ChatUiState.Error -> {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(estado.mensaje, color = Gray500)
                        }
                    }
                    is ChatUiState.Success -> {
                        if (estado.mensajes.isEmpty()) {
                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(Icons.AutoMirrored.Filled.Chat, null,
                                        tint = Gray200, modifier = Modifier.size(48.dp))
                                    Spacer(Modifier.height(8.dp))
                                    Text("Sé el primero en escribir", color = Gray500)
                                }
                            }
                        } else {
                            LazyColumn(
                                state           = listState,
                                modifier        = Modifier.fillMaxSize(),
                                contentPadding  = PaddingValues(16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(estado.mensajes, key = { it.id }) { mensaje ->
                                    val esMio = mensaje.remitente_id == miId
                                    BurbujaMensaje(mensaje = mensaje, esMio = esMio)
                                }
                            }
                        }
                    }
                }
            }

            // ── Input de mensaje ─────────────────────────────
            Surface(
                shadowElevation = 8.dp,
                color           = White
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value         = viewModel.textoMensaje,
                        onValueChange = { viewModel.textoMensaje = it },
                        placeholder   = { Text("Escribe un mensaje...", color = Gray200, fontSize = 14.sp) },
                        maxLines      = 4,
                        shape         = RoundedCornerShape(24.dp),
                        modifier      = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction    = ImeAction.Send
                        ),
                        keyboardActions = KeyboardActions(
                            onSend = { viewModel.enviarMensaje(contactoId, miId) }
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor   = Blue800,
                            unfocusedBorderColor = Gray200,
                            focusedContainerColor   = Color(0xFFF9F9FF),
                            unfocusedContainerColor = Color(0xFFF9F9FF)
                        )
                    )
                    // Botón enviar
                    FloatingActionButton(
                        onClick        = { viewModel.enviarMensaje(contactoId, miId) },
                        containerColor = Blue900,
                        contentColor   = White,
                        modifier       = Modifier.size(48.dp)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Send, null, modifier = Modifier.size(20.dp))
                    }
                }
            }
        }
    }
}

// ── Burbuja de mensaje ────────────────────────────────────────
@Composable
fun BurbujaMensaje(mensaje: Mensaje, esMio: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (esMio) Arrangement.End else Arrangement.Start
    ) {
        if (!esMio) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(BlueFixed)
                    .align(Alignment.Bottom),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = (mensaje.remitente_nombre ?: "?").take(1).uppercase(),
                    fontSize   = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color      = Blue900
                )
            }
            Spacer(Modifier.width(6.dp))
        }

        Column(
            horizontalAlignment = if (esMio) Alignment.End else Alignment.Start,
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Box(
                modifier = Modifier
                    .clip(
                        RoundedCornerShape(
                            topStart    = 20.dp,
                            topEnd      = 20.dp,
                            bottomStart = if (esMio) 20.dp else 4.dp,
                            bottomEnd   = if (esMio) 4.dp else 20.dp
                        )
                    )
                    .background(if (esMio) Blue900 else White)
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Text(
                    text      = mensaje.contenido,
                    fontSize  = 14.sp,
                    color     = if (esMio) White else Color(0xFF191C21),
                    lineHeight = 20.sp
                )
            }
            Spacer(Modifier.height(2.dp))
            Text(
                text     = mensaje.created_at,
                fontSize = 10.sp,
                color    = Gray500
            )
        }
    }
}
