package com.example.comunicappescolar.ui.screens.asistencia


import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.comunicappescolar.data.local.SessionDataStore
import com.example.comunicappescolar.data.model.Asistencia
import com.example.comunicappescolar.ui.screens.home.BottomNavBar

// ── Colores ──────────────────────────────────────────────────
private val Blue900   = Color(0xFF004D99)
private val BlueFixed = Color(0xFFD6E3FF)
private val Gray500   = Color(0xFF727783)
private val Gray200   = Color(0xFFE1E2EA)
private val Surface   = Color(0xFFF9F9FF)
private val White     = Color(0xFFFFFFFF)
private val GreenDark = Color(0xFF005C15)
private val GreenMid  = Color(0xFF25752B)
private val GreenLight= Color(0xFFA3F69C)
private val ErrorRed  = Color(0xFFBA1A1A)
private val ErrorCont = Color(0xFFFFDAD6)
private val SecondCol = Color(0xFF005FAF)
private val SecCont   = Color(0xFFD4E3FF)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AsistenciaScreen(navController: NavController) {
    val context   = LocalContext.current
    val session   = remember { SessionDataStore(context) }
    val viewModel: AsistenciaViewModel = viewModel()
    val uiState   by viewModel.uiState.collectAsState()
    val rol       by session.rol.collectAsState(initial = "")

    var mostrarRegistrar by remember { mutableStateOf(false) }

    if (mostrarRegistrar) {
        RegistrarAsistenciaSheet(
            viewModel = viewModel,
            onDismiss = { mostrarRegistrar = false }
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
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Blue900)
                        }
                        Text("Asistencia", fontSize = 22.sp,
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
                    onClick        = { mostrarRegistrar = true },
                    containerColor = Blue900,
                    contentColor   = White,
                    modifier       = Modifier.padding(bottom = 72.dp)
                ) {
                    Icon(Icons.Default.Add, "Registrar asistencia")
                }
            }
        },
        bottomBar      = { BottomNavBar(navController) },
        containerColor = Surface
    ) { padding ->

        when (val estado = uiState) {
            is AsistenciaUiState.Loading -> {
                Box(Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Blue900)
                }
            }
            is AsistenciaUiState.Error -> {
                Box(Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.WifiOff, null,
                            tint = Gray500, modifier = Modifier.size(48.dp))
                        Spacer(Modifier.height(8.dp))
                        Text(estado.mensaje, color = Gray500)
                        Spacer(Modifier.height(12.dp))
                        Button(
                            onClick = viewModel::cargarAsistencia,
                            colors  = ButtonDefaults.buttonColors(containerColor = Blue900)
                        ) { Text("Reintentar") }
                    }
                }
            }
            is AsistenciaUiState.Success -> {
                val registros  = estado.registros
                val porcentaje = viewModel.porcentaje(registros)
                val presentes  = viewModel.totalPresentes(registros)
                val ausentes   = viewModel.totalAusentes(registros)
                val tardanzas  = viewModel.totalTardanzas(registros)

                LazyColumn(
                    modifier       = Modifier.fillMaxSize().padding(padding),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {

                    // ── Hero: círculo de progreso ────────────
                    item {
                        Card(
                            modifier  = Modifier.fillMaxWidth(),
                            shape     = RoundedCornerShape(20.dp),
                            colors    = CardDefaults.cardColors(containerColor = White),
                            elevation = CardDefaults.cardElevation(1.dp)
                        ) {
                            Column(
                                modifier            = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                CirculoAsistencia(porcentaje = porcentaje)
                                Spacer(Modifier.height(16.dp))
                                Text(
                                    text       = if (porcentaje >= 95) "¡Gran progreso!"
                                    else if (porcentaje >= 80) "Buen desempeño"
                                    else "Requiere atención",
                                    fontSize   = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color      = Color(0xFF191C21)
                                )
                                Text(
                                    text      = if (porcentaje >= 95)
                                        "La asistencia está por encima del promedio."
                                    else "Es importante mejorar la asistencia.",
                                    fontSize  = 13.sp,
                                    color     = Gray500,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }

                    // ── Grid resumen ─────────────────────────
                    item {
                        Row(
                            modifier              = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            TarjetaResumenAsistencia(
                                icono    = Icons.Default.CheckCircle,
                                color    = GreenMid,
                                label    = "PRESENTE",
                                valor    = presentes.toString(),
                                subtexto = "días este año",
                                modifier = Modifier.weight(1f)
                            )
                            TarjetaResumenAsistencia(
                                icono    = Icons.Default.Cancel,
                                color    = ErrorRed,
                                label    = "AUSENTE",
                                valor    = ausentes.toString(),
                                subtexto = "necesita justificación",
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    // Tardanzas si hay
                    if (tardanzas > 0) {
                        item {
                            TarjetaResumenAsistencia(
                                icono    = Icons.Default.Schedule,
                                color    = SecondCol,
                                label    = "TARDANZAS",
                                valor    = tardanzas.toString(),
                                subtexto = "llegadas tarde",
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    // ── Filtro de mes ────────────────────────
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape    = RoundedCornerShape(50),
                            colors   = CardDefaults.cardColors(containerColor = Gray200)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment     = Alignment.CenterVertically
                            ) {
                                Text(viewModel.mesActivo, fontSize = 15.sp,
                                    fontWeight = FontWeight.Medium, color = Color(0xFF191C21))
                                Icon(Icons.Default.CalendarMonth, null,
                                    tint = Blue900, modifier = Modifier.size(20.dp))
                            }
                        }
                    }

                    // ── Título lista ─────────────────────────
                    item {
                        Text("Registro Diario", fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold, color = Color(0xFF191C21))
                    }

                    // ── Lista de registros ───────────────────
                    if (registros.isEmpty()) {
                        item {
                            Box(
                                modifier         = Modifier.fillMaxWidth().height(200.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(Icons.Default.CalendarToday, null,
                                        tint = Gray200, modifier = Modifier.size(48.dp))
                                    Spacer(Modifier.height(8.dp))
                                    Text("Sin registros de asistencia", color = Gray500)
                                }
                            }
                        }
                    } else {
                        items(registros, key = { it.id }) { registro ->
                            FilaAsistencia(registro = registro)
                        }
                    }

                    item { Spacer(Modifier.height(80.dp)) }
                }
            }
        }
    }
}


// ── Círculo de progreso animado ───────────────────────────────
@Composable
fun CirculoAsistencia(porcentaje: Int) {
    val progreso = (porcentaje / 100f).coerceIn(0f, 1f)
    val animado  by animateFloatAsState(
        targetValue   = progreso,
        animationSpec = tween(1400, easing = FastOutSlowInEasing),
        label         = "circuloAsistencia"
    )

    val colorArco = when {
        porcentaje >= 95 -> GreenDark
        porcentaje >= 80 -> Blue900
        else             -> ErrorRed
    }

    Box(
        modifier         = Modifier.size(150.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val stroke = Stroke(width = 14.dp.toPx(), cap = StrokeCap.Round)
            val inset  = stroke.width / 2
            val rect   = androidx.compose.ui.geometry.Rect(
                offset = Offset(inset, inset),
                size   = Size(size.width - stroke.width, size.height - stroke.width)
            )
            drawArc(
                color      = Color.LightGray.copy(alpha = 0.3f),
                startAngle = -90f, sweepAngle = 360f, useCenter = false,
                topLeft    = rect.topLeft, size = rect.size, style = stroke
            )
            drawArc(
                color      = colorArco,
                startAngle = -90f, sweepAngle = 360f * animado, useCenter = false,
                topLeft    = rect.topLeft, size = rect.size, style = stroke
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("$porcentaje%", fontSize = 32.sp,
                fontWeight = FontWeight.Bold, color = colorArco)
            Text("ANUAL", fontSize = 11.sp,
                color = Gray500, letterSpacing = 1.sp)
        }
    }
}


// ── Tarjeta de resumen ────────────────────────────────────────
@Composable
fun TarjetaResumenAsistencia(
    icono:    androidx.compose.ui.graphics.vector.ImageVector,
    color:    Color,
    label:    String,
    valor:    String,
    subtexto: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier  = modifier,
        shape     = RoundedCornerShape(16.dp),
        colors    = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .background(
                        color = color,
                        shape = RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp)
                    )
            )
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(icono, null, tint = color, modifier = Modifier.size(18.dp))
                    Text(label, fontSize = 10.sp,
                        fontWeight = FontWeight.Bold, color = Gray500,
                        letterSpacing = 0.5.sp)
                }
                Spacer(Modifier.height(4.dp))
                Text(valor, fontSize = 26.sp,
                    fontWeight = FontWeight.Bold, color = Color(0xFF191C21))
                Text(subtexto, fontSize = 10.sp, color = Gray500)
            }
        }
    }
}


// ── Fila de registro diario ───────────────────────────────────
@Composable
fun FilaAsistencia(registro: Asistencia) {
    val colorIcono: Color
    val colorFondo: Color
    val icono: androidx.compose.ui.graphics.vector.ImageVector
    val textoEstado: String

    when (registro.estado) {
        "presente" -> {
            colorIcono = GreenMid
            colorFondo = GreenLight.copy(.2f)
            icono = Icons.Default.Check
            textoEstado = "Estado: Presente"
        }
        "tardanza" -> {
            colorIcono = SecondCol
            colorFondo = SecCont.copy(.3f)
            icono = Icons.Default.Schedule
            textoEstado = "Estado: Tardanza"
        }
        else -> {
            colorIcono = ErrorRed
            colorFondo = ErrorCont
            icono = Icons.Default.Close
            textoEstado = "Estado: Ausente - Justificar ahora"
        }
    }

    val esAusente = registro.estado == "ausente"

    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(16.dp),
        colors    = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (esAusente) 4.dp else 1.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            // Borde lateral solo en ausentes
            if (esAusente) {
                Box(
                    modifier = Modifier
                        .width(4.dp)
                        .fillMaxHeight()
                        .background(
                            color = ErrorRed,
                            shape = RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp)
                        )
                )
            }

            Row(
                modifier = Modifier
                    .weight(1f)
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Ícono de estado
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(colorFondo),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icono,
                        contentDescription = null,
                        tint     = colorIcono,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Fecha y estado
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text       = registro.fecha,
                        fontSize   = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color      = Color(0xFF191C21)
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text     = textoEstado,
                        fontSize = 12.sp,
                        color    = colorIcono
                    )
                }

                // Ícono derecho
                Icon(
                    imageVector = if (esAusente) Icons.Default.Report else Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint     = if (esAusente) ErrorRed else Gray200,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
