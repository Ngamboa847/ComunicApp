package com.example.comunicappescolar.ui.screens.tareas


import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.comunicappescolar.data.local.SessionDataStore
import com.example.comunicappescolar.data.model.Tarea
import com.example.comunicappescolar.ui.screens.home.BottomNavBar

// ── Colores ──────────────────────────────────────────────────
private val Blue900    = Color(0xFF004D99)
private val Blue800    = Color(0xFF1565C0)
private val BlueFixed  = Color(0xFFD6E3FF)
private val Gray500    = Color(0xFF727783)
private val Gray200    = Color(0xFFC2C6D4)
private val Surface    = Color(0xFFF9F9FF)
private val White      = Color(0xFFFFFFFF)
private val ErrorRed   = Color(0xFFBA1A1A)
private val ErrorCont  = Color(0xFFFFDAD6)
private val GreenDark  = Color(0xFF25752B)
private val GreenLight = Color(0xFFA3F69C)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TareasScreen(navController: NavController) {
    val context   = LocalContext.current
    val session   = remember { SessionDataStore(context) }
    val viewModel: TareasViewModel = viewModel()
    val uiState   by viewModel.uiState.collectAsState()
    val rol       by session.rol.collectAsState(initial = "")

    var mostrarCrear by remember { mutableStateOf(false) }

    if (mostrarCrear) {
        CrearTareaSheet(
            viewModel = viewModel,
            onDismiss = { mostrarCrear = false }
        )
    }

    val filtros = listOf(
        "todas"      to "Todas",
        "pendientes" to "Pendientes",
        "entregadas" to "Entregadas"
    )

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
                        Text("ComunicApp Escolar", fontSize = 18.sp,
                            fontWeight = FontWeight.Bold, color = Blue900)
                    }
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(BlueFixed),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Person, null,
                            tint = Blue900, modifier = Modifier.size(20.dp))
                    }
                    Spacer(Modifier.width(8.dp))
                }
            }
        },
        floatingActionButton = {
            if (rol == "docente") {
                FloatingActionButton(
                    onClick        = { mostrarCrear = true },
                    containerColor = Blue900,
                    contentColor   = White,
                    modifier       = Modifier.padding(bottom = 72.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Nueva tarea")
                }
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
            Spacer(Modifier.height(16.dp))

            // ── Título y subtítulo ───────────────────────────
            Text("Tareas", fontSize = 28.sp,
                fontWeight = FontWeight.Bold, color = Color(0xFF191C21))
            Text("Gestiona las actividades académicas y plazos.",
                fontSize = 14.sp, color = Gray500)

            Spacer(Modifier.height(16.dp))

            // ── Chips de filtro ──────────────────────────────
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                filtros.forEach { (clave, etiqueta) ->
                    val sel = viewModel.filtroActivo == clave
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(if (sel) Blue900 else White)
                            .clickable { viewModel.cambiarFiltro(clave) }
                            .then(
                                if (!sel) Modifier.then(
                                    Modifier.background(
                                        color = Color.Transparent,
                                        shape = RoundedCornerShape(50)
                                    )
                                ) else Modifier
                            )
                            .padding(horizontal = 20.dp, vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(etiqueta, fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (sel) White else Gray500)
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // ── Contenido ────────────────────────────────────
            when (val estado = uiState) {
                is TareasUiState.Loading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Blue900)
                    }
                }
                is TareasUiState.Error -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.WifiOff, null,
                                tint = Gray500, modifier = Modifier.size(48.dp))
                            Spacer(Modifier.height(8.dp))
                            Text(estado.mensaje, color = Gray500)
                            Spacer(Modifier.height(12.dp))
                            Button(
                                onClick = viewModel::cargarTareas,
                                colors  = ButtonDefaults.buttonColors(containerColor = Blue900)
                            ) { Text("Reintentar") }
                        }
                    }
                }
                is TareasUiState.Success -> {
                    val lista = viewModel.tareasFiltradas(estado.tareas)
                    if (lista.isEmpty()) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.AssignmentTurnedIn, null,
                                    tint = Gray200, modifier = Modifier.size(56.dp))
                                Spacer(Modifier.height(8.dp))
                                Text("No hay tareas en esta categoría", color = Gray500)
                            }
                        }
                    } else {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            items(lista, key = { it.id }) { tarea ->
                                TarjetaTarea(
                                    tarea       = tarea,
                                    onRevisar   = { revisada ->
                                        viewModel.marcarRevision(tarea.id, revisada)
                                    }
                                )
                            }
                            item { Spacer(Modifier.height(100.dp)) }
                        }
                    }
                }
            }
        }
    }
}