package com.example.comunicappescolar.ui.screens.mensajes


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.comunicappescolar.data.local.SessionDataStore
import com.example.comunicappescolar.data.model.Conversacion
import com.example.comunicappescolar.data.model.Usuario
import com.example.comunicappescolar.ui.navigation.AppRoutes
import com.example.comunicappescolar.ui.screens.home.BottomNavBar

private val Blue900   = Color(0xFF004D99)
private val Blue800   = Color(0xFF1565C0)
private val BlueFixed = Color(0xFFD6E3FF)
private val BlueCont  = Color(0xFF1565C0)
private val Gray500   = Color(0xFF727783)
private val Gray200   = Color(0xFFC2C6D4)
private val Surface   = Color(0xFFF9F9FF)
private val White     = Color(0xFFFFFFFF)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MensajesScreen(navController: NavController) {
    val context   = LocalContext.current
    val session   = remember { SessionDataStore(context) }
    val viewModel: MensajesViewModel = viewModel()
    val convState by viewModel.convState.collectAsState()
    val usuarios  by viewModel.usuarios.collectAsState()
    val usuarioId by session.token.collectAsState(initial = null)

    var mostrarNuevoMensaje by remember { mutableStateOf(false) }

    // Sheet para nuevo mensaje
    if (mostrarNuevoMensaje) {
        NuevoMensajeSheet(
            usuarios  = usuarios,
            onContact = { usuario ->
                mostrarNuevoMensaje = false
                navController.navigate(AppRoutes.Chat.createRoute(usuario.id, usuario.nombre))
            },
            onDismiss = { mostrarNuevoMensaje = false }
        )
    }

    Scaffold(
        topBar = {
            Surface(shadowElevation = 2.dp, color = White) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                        .padding(horizontal = 8.dp),
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.Default.ArrowBack, null, tint = Blue900)
                        }
                        Text("Mensajes", fontSize = 22.sp,
                            fontWeight = FontWeight.Bold, color = Blue900)
                    }
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .border(2.dp, BlueFixed, CircleShape)
                            .background(BlueFixed),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Person, null,
                            tint = Blue900, modifier = Modifier.size(22.dp))
                    }
                    Spacer(Modifier.width(8.dp))
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick        = {
                    viewModel.cargarUsuarios()
                    mostrarNuevoMensaje = true
                },
                containerColor = Blue900,
                contentColor   = White,
                modifier       = Modifier.padding(bottom = 72.dp)
            ) {
                Icon(Icons.Default.Edit, contentDescription = "Nuevo mensaje")
            }
        },
        bottomBar      = { BottomNavBar(navController) },
        containerColor = Surface
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {

            Spacer(Modifier.height(12.dp))

            // ── Barra de búsqueda ────────────────────────────
            OutlinedTextField(
                value         = viewModel.textoBusqueda,
                onValueChange = { viewModel.textoBusqueda = it },
                placeholder   = { Text("Buscar conversaciones...", color = Gray200) },
                leadingIcon   = { Icon(Icons.Default.Search, null, tint = Gray500) },
                singleLine    = true,
                shape         = RoundedCornerShape(50),
                modifier      = Modifier.fillMaxWidth(),
                colors        = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor   = Blue800,
                    unfocusedBorderColor = Color.Transparent,
                    focusedContainerColor   = White,
                    unfocusedContainerColor = White
                )
            )

            Spacer(Modifier.height(16.dp))

            // ── Lista de conversaciones ──────────────────────
            when (val estado = convState) {
                is ConversacionesUiState.Loading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Blue900)
                    }
                }
                is ConversacionesUiState.Error -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.WifiOff, null,
                                tint = Gray500, modifier = Modifier.size(48.dp))
                            Spacer(Modifier.height(8.dp))
                            Text(estado.mensaje, color = Gray500)
                            Spacer(Modifier.height(12.dp))
                            Button(
                                onClick = viewModel::cargarConversaciones,
                                colors  = ButtonDefaults.buttonColors(containerColor = Blue900)
                            ) { Text("Reintentar") }
                        }
                    }
                }
                is ConversacionesUiState.Success -> {
                    val lista = viewModel.conversacionesFiltradas(estado.conversaciones)
                    if (lista.isEmpty()) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.ChatBubbleOutline, null,
                                    tint = Gray200, modifier = Modifier.size(56.dp))
                                Spacer(Modifier.height(8.dp))
                                Text("No hay conversaciones aún", color = Gray500)
                                Spacer(Modifier.height(4.dp))
                                Text("Toca el botón para iniciar una",
                                    fontSize = 12.sp, color = Gray200)
                            }
                        }
                    } else {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            items(lista, key = { it.contacto_id }) { conv ->
                                TarjetaConversacion(
                                    conv    = conv,
                                    onClick = {
                                        navController.navigate(AppRoutes.Chat.createRoute(conv.contacto_id, conv.contacto_nombre))
                                    }
                                )
                            }
                            item { Spacer(Modifier.height(80.dp)) }
                        }
                    }
                }
            }
        }
    }
}

// ── Tarjeta de conversación ───────────────────────────────────
@Composable
fun TarjetaConversacion(conv: Conversacion, onClick: () -> Unit) {
    val tieneNoLeidos = conv.no_leidos > 0

    Card(
        modifier  = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape     = RoundedCornerShape(16.dp),
        colors    = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (tieneNoLeidos) 4.dp else 1.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Borde izquierdo si tiene no leídos
            if (tieneNoLeidos) {
                Box(
                    modifier = Modifier
                        .width(4.dp)
                        .height(80.dp)
                        .background(
                            color = Color(0xFF1565C0),
                            shape = RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp)
                        )
                )
            } else {
                Spacer(Modifier.width(4.dp))
            }

            Row(
                modifier = Modifier
                    .weight(1f)
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Avatar con indicador online
                Box(modifier = Modifier.size(56.dp)) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(BlueFixed),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text       = conv.contacto_nombre.take(2).uppercase(),
                            fontSize   = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color      = Blue900
                        )
                    }
                    if (tieneNoLeidos) {
                        Box(
                            modifier = Modifier
                                .size(14.dp)
                                .align(Alignment.BottomEnd)
                                .clip(CircleShape)
                                .background(Color.White)
                                .padding(2.dp)
                                .clip(CircleShape)
                                .background(Blue800)
                        )
                    }
                }

                // Info de la conversación
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment     = Alignment.CenterVertically
                    ) {
                        Text(
                            text       = conv.contacto_nombre,
                            fontSize   = 15.sp,
                            fontWeight = if (tieneNoLeidos) FontWeight.Bold else FontWeight.Medium,
                            color      = Color(0xFF191C21),
                            maxLines   = 1,
                            overflow   = TextOverflow.Ellipsis,
                            modifier   = Modifier.weight(1f)
                        )
                        Text(
                            text     = "Hoy",
                            fontSize = 11.sp,
                            color    = if (tieneNoLeidos) Blue900 else Gray500
                        )
                    }
                    Spacer(Modifier.height(2.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment     = Alignment.CenterVertically
                    ) {
                        Text(
                            text       = conv.ultimo_mensaje ?: "Sin mensajes aún",
                            fontSize   = 13.sp,
                            fontWeight = if (tieneNoLeidos) FontWeight.SemiBold else FontWeight.Normal,
                            color      = if (tieneNoLeidos) Color(0xFF191C21) else Gray500,
                            maxLines   = 1,
                            overflow   = TextOverflow.Ellipsis,
                            modifier   = Modifier.weight(1f)
                        )
                        if (tieneNoLeidos) {
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .clip(CircleShape)
                                    .background(Blue900),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text     = conv.no_leidos.toString(),
                                    fontSize = 10.sp,
                                    color    = White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}