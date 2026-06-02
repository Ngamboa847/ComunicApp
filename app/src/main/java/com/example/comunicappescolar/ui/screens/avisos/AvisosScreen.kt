package com.example.comunicappescolar.ui.screens.avisos

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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.comunicappescolar.data.local.SessionDataStore
import com.example.comunicappescolar.data.model.Aviso
import com.example.comunicappescolar.ui.navigation.AppRoutes
import com.example.comunicappescolar.ui.screens.home.BottomNavBar

private val Blue900   = Color(0xFF004D99)
private val Blue800   = Color(0xFF1565C0)
private val BlueFixed = Color(0xFFD6E3FF)
private val Gray500   = Color(0xFF727783)
private val Gray200   = Color(0xFFC2C6D4)
private val Surface   = Color(0xFFF9F9FF)
private val ErrorRed  = Color(0xFFBA1A1A)
private val ErrorCont = Color(0xFFFFDAD6)
private val SecCont   = Color(0xFF54A0FE)
private val TertFixed = Color(0xFFA3F69C)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AvisosScreen(navController: NavController) {
    val context   = LocalContext.current
    val session   = remember { SessionDataStore(context) }
    val viewModel: AvisosViewModel = viewModel()
    val uiState   by viewModel.uiState.collectAsState()
    val rol       by session.rol.collectAsState(initial = "")

    // Mostrar sheet de creación
    var mostrarCrear by remember { mutableStateOf(false) }

    if (mostrarCrear) {
        CrearAvisoSheet(
            viewModel  = viewModel,
            onDismiss  = { mostrarCrear = false }
        )
    }

    val filtros = listOf(
        "todos"   to "Todos",
        "urgente" to "Urgentes",
        "info"    to "Informativos",
        "evento"  to "Eventos"
    )

    Scaffold(
        topBar = {
            Surface(shadowElevation = 2.dp, color = Color.White) {
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
                        Column {
                            Text("Avisos", fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold, color = Blue900)
                            if (uiState is AvisosUiState.Success) {
                                val noLeidos = viewModel.noLeidos(
                                    (uiState as AvisosUiState.Success).avisos)
                                if (noLeidos > 0)
                                    Text("$noLeidos avisos sin leer",
                                        fontSize = 11.sp, color = Gray500)
                            }
                        }
                    }
                    Box(
                        modifier = Modifier
                            .size(36.dp)
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
        // FAB solo para docentes
        floatingActionButton = {
            if (rol == "docente") {
                ExtendedFloatingActionButton(
                    onClick           = { mostrarCrear = true },
                    containerColor    = Blue900,
                    contentColor      = Color.White,
                    icon              = { Icon(Icons.Default.Add, null) },
                    text              = { Text("Nuevo aviso", fontWeight = FontWeight.SemiBold) }
                )
            }
        },
        bottomBar      = { BottomNavBar(navController) },
        containerColor = Surface
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Chips de filtro
            Surface(color = Surface.copy(alpha = 0.9f)) {
                LazyRow(
                    contentPadding        = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filtros) { (clave, etiqueta) ->
                        val sel = viewModel.filtroActivo == clave
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(if (sel) Blue900 else Color.White)
                                .clickable { viewModel.cambiarFiltro(clave) }
                                .padding(horizontal = 20.dp, vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(etiqueta, fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (sel) Color.White else Gray500)
                        }
                    }
                }
            }

            when (val estado = uiState) {
                is AvisosUiState.Loading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Blue900)
                    }
                }
                is AvisosUiState.Error -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.WifiOff, null,
                                tint = Gray500, modifier = Modifier.size(48.dp))
                            Spacer(Modifier.height(8.dp))
                            Text(estado.mensaje, color = Gray500)
                            Spacer(Modifier.height(12.dp))
                            Button(
                                onClick = viewModel::cargarAvisos,
                                colors  = ButtonDefaults.buttonColors(containerColor = Blue900)
                            ) { Text("Reintentar") }
                        }
                    }
                }
                is AvisosUiState.Success -> {
                    val lista = viewModel.avisosFiltrados(estado.avisos)
                    if (lista.isEmpty()) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.Inbox, null,
                                    tint = Gray200, modifier = Modifier.size(56.dp))
                                Spacer(Modifier.height(8.dp))
                                Text("Sin avisos en esta categoría", color = Gray500)
                            }
                        }
                    } else {
                        LazyColumn(
                            contentPadding        = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                            verticalArrangement   = Arrangement.spacedBy(12.dp)
                        ) {
                            items(lista, key = { it.id }) { aviso ->
                                TarjetaAviso(
                                    aviso   = aviso,
                                    onClick = {
                                        viewModel.marcarLeido(aviso.id)
                                        navController.navigate(
                                            AppRoutes.DetalleAviso.createRoute(aviso.id))
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