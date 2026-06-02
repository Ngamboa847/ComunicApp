package com.example.comunicappescolar.ui.screens.calendario


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
import com.example.comunicappescolar.data.model.Evento
import com.example.comunicappescolar.ui.screens.home.BottomNavBar
import java.time.LocalDate

// ── Colores ──────────────────────────────────────────────────
private val Blue900   = Color(0xFF004D99)
private val Blue800   = Color(0xFF1565C0)
private val BlueFixed = Color(0xFFD6E3FF)
private val BlueOnFix = Color(0xFF001B3D)
private val SecColor  = Color(0xFF005FAF)
private val SecCont   = Color(0xFF54A0FE)
private val GreenDark = Color(0xFF005C15)
private val GreenMid  = Color(0xFF25752B)
private val GreenFix  = Color(0xFFA3F69C)
private val Gray500   = Color(0xFF727783)
private val Gray200   = Color(0xFFE1E2EA)
private val Surface   = Color(0xFFF9F9FF)
private val SurfCont  = Color(0xFFECEDF6)
private val White     = Color(0xFFFFFFFF)
private val ErrorRed  = Color(0xFFBA1A1A)
private val ErrorCont = Color(0xFFFFDAD6)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarioScreen(navController: NavController) {
    val context   = LocalContext.current
    val session   = remember { SessionDataStore(context) }
    val viewModel: CalendarioViewModel = viewModel()
    val uiState   by viewModel.uiState.collectAsState()
    val rol       by session.rol.collectAsState(initial = "")

    var mostrarCrear by remember { mutableStateOf(false) }

    if (mostrarCrear) {
        CrearEventoSheet(
            viewModel = viewModel,
            onDismiss = { mostrarCrear = false }
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
                        Text("ComunicApp Escolar", fontSize = 18.sp,
                            fontWeight = FontWeight.Bold, color = Blue900)
                    }
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
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
            if (rol == "docente") {
                FloatingActionButton(
                    onClick        = { mostrarCrear = true },
                    containerColor = BlueFixed,
                    contentColor   = Blue900,
                    shape          = RoundedCornerShape(16.dp),
                    modifier       = Modifier.padding(bottom = 72.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(Icons.Default.Add, null, modifier = Modifier.size(22.dp))
                        Text("Crear Evento", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    }
                }
            }
        },
        bottomBar      = { BottomNavBar(navController) },
        containerColor = Surface
    ) { padding ->

        LazyColumn(
            modifier       = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // ── Encabezado mes + botón ───────────────────────
            item {
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    Text(viewModel.mesAnio, fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold, color = Color(0xFF191C21))
                    TextButton(onClick = { }) {
                        Text("Ver Calendario", fontSize = 12.sp, color = Blue900)
                        Spacer(Modifier.width(4.dp))
                        Icon(Icons.Default.CalendarMonth, null,
                            tint = Blue900, modifier = Modifier.size(16.dp))
                    }
                }
            }

            // ── Selector de días ─────────────────────────────
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding        = PaddingValues(vertical = 4.dp)
                ) {
                    items(viewModel.diasSemana) { dia ->
                        val esSeleccionado = dia.fecha == viewModel.diaSeleccionado
                        val esHoy          = dia.fecha == LocalDate.now()

                        Column(
                            modifier = Modifier
                                .width(64.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .background(
                                    if (esSeleccionado) Blue900 else SurfCont
                                )
                                .clickable { viewModel.seleccionarDia(dia.fecha) }
                                .padding(vertical = 14.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text       = dia.diaNombre.take(3)
                                    .replaceFirstChar { it.uppercase() },
                                fontSize   = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color      = if (esSeleccionado) BlueFixed else Gray500
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text       = dia.diaNum.toString(),
                                fontSize   = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color      = if (esSeleccionado) White else Color(0xFF191C21)
                            )
                            if (esSeleccionado || esHoy) {
                                Spacer(Modifier.height(4.dp))
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (esSeleccionado) White else Blue900
                                        )
                                )
                            }
                        }
                    }
                }
            }

            // ── Título agenda ────────────────────────────────
            item {
                HorizontalDivider(color = Gray200)
                Spacer(Modifier.height(4.dp))
                when (val estado = uiState) {
                    is CalendarioUiState.Success -> {
                        val filtrados = viewModel.eventosFiltrados(estado.eventos)
                        Row(
                            modifier              = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment     = Alignment.CenterVertically
                        ) {
                            Text("Próximas Tareas y Eventos", fontSize = 14.sp,
                                color = Gray500, fontWeight = FontWeight.Medium)
                            Text("${filtrados.size} ítems hoy",
                                fontSize = 11.sp, color = Gray500)
                        }
                    }
                    else -> {}
                }
            }

            // ── Contenido ────────────────────────────────────
            when (val estado = uiState) {
                is CalendarioUiState.Loading -> {
                    item {
                        Box(Modifier.fillMaxWidth().height(200.dp),
                            contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = Blue900)
                        }
                    }
                }
                is CalendarioUiState.Error -> {
                    item {
                        Box(Modifier.fillMaxWidth().height(200.dp),
                            contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.WifiOff, null,
                                    tint = Gray500, modifier = Modifier.size(40.dp))
                                Spacer(Modifier.height(8.dp))
                                Text(estado.mensaje, color = Gray500, fontSize = 13.sp)
                                Spacer(Modifier.height(8.dp))
                                Button(
                                    onClick = viewModel::cargarEventos,
                                    colors  = ButtonDefaults.buttonColors(containerColor = Blue900)
                                ) { Text("Reintentar") }
                            }
                        }
                    }
                }
                is CalendarioUiState.Success -> {
                    val filtrados = viewModel.eventosFiltrados(estado.eventos)
                    if (filtrados.isEmpty()) {
                        item {
                            Box(Modifier.fillMaxWidth().height(200.dp),
                                contentAlignment = Alignment.Center) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(Icons.Default.EventBusy, null,
                                        tint = Gray200, modifier = Modifier.size(48.dp))
                                    Spacer(Modifier.height(8.dp))
                                    Text("Sin eventos para este día", color = Gray500)
                                    if (rol == "docente") {
                                        Spacer(Modifier.height(4.dp))
                                        Text("Toca + para crear uno",
                                            fontSize = 12.sp, color = Gray200)
                                    }
                                }
                            }
                        }
                    } else {
                        items(filtrados, key = { it.id }) { evento ->
                            TarjetaEvento(evento = evento)
                        }
                    }
                }
            }

            item { Spacer(Modifier.height(100.dp)) }
        }
    }
}


// ── Tarjeta de evento ─────────────────────────────────────────
@Composable
fun TarjetaEvento(evento: Evento) {
    val (colorBorde, colorPunto, labelTipo) = when (evento.tipo) {
        "deportivo"  -> listOf(GreenMid,  GreenMid,  "DEPORTIVO")
        "reunion"    -> listOf(SecColor,  SecCont,   "REUNIÓN")
        "cultural"   -> listOf(Color(0xFF7C3AED), Color(0xFFA855F7), "CULTURAL")
        else         -> listOf(Blue900,   Blue800,   "ACADÉMICO")
    }

    val colorBadgeFondo = when (evento.tipo) {
        "deportivo" -> GreenFix
        "reunion"   -> BlueFixed
        "cultural"  -> Color(0xFFEDE9FE)
        else        -> BlueFixed
    }
    val colorBadgeTexto = when (evento.tipo) {
        "deportivo" -> Color(0xFF002204)
        "reunion"   -> BlueOnFix
        "cultural"  -> Color(0xFF4C1D95)
        else        -> BlueOnFix
    }

    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(16.dp),
        colors    = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            // Borde lateral de color
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .background(
                        color = colorBorde as Color,
                        shape = RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp)
                    )
            )

            Column(modifier = Modifier.weight(1f).padding(14.dp)) {
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        // Punto de color + tipo
                        Row(
                            verticalAlignment     = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(colorPunto as Color)
                            )
                            Text(
                                text          = labelTipo as String,
                                fontSize      = 10.sp,
                                fontWeight    = FontWeight.Bold,
                                color         = colorBorde,
                                letterSpacing = 1.sp
                            )
                        }

                        Spacer(Modifier.height(6.dp))

                        // Título
                        Text(
                            text       = evento.titulo,
                            fontSize   = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color      = Color(0xFF191C21),
                            maxLines   = 2,
                            overflow   = TextOverflow.Ellipsis
                        )

                        Spacer(Modifier.height(10.dp))

                        // Hora
                        if (!evento.hora_inicio.isNullOrBlank()) {
                            Row(
                                verticalAlignment     = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(Icons.Default.Schedule, null,
                                    tint = Gray500, modifier = Modifier.size(16.dp))
                                Text(
                                    text     = "${evento.hora_inicio} - ${evento.hora_fin ?: ""}",
                                    fontSize = 13.sp,
                                    color    = Gray500
                                )
                            }
                            Spacer(Modifier.height(4.dp))
                        }

                        // Lugar
                        if (!evento.lugar.isNullOrBlank()) {
                            Row(
                                verticalAlignment     = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = if (evento.lugar.contains("Zoom", ignoreCase = true))
                                        Icons.Default.Videocam else Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint     = Gray500,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(evento.lugar, fontSize = 13.sp, color = Gray500)
                            }
                        }
                    }

                    // Badge de grado
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(colorBadgeFondo)
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text       = evento.grado ?: "Todos",
                            fontSize   = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color      = colorBadgeTexto
                        )
                    }
                }
            }
        }
    }
}