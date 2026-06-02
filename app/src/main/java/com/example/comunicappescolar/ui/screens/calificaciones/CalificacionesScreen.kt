package com.example.comunicappescolar.ui.screens.calificaciones


import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.comunicappescolar.data.local.SessionDataStore
import com.example.comunicappescolar.data.model.Calificacion
import com.example.comunicappescolar.ui.screens.home.BottomNavBar
import java.util.Locale

// ── Colores ──────────────────────────────────────────────────
private val Blue900   = Color(0xFF004D99)
private val Blue800   = Color(0xFF1565C0)
private val BlueFixed = Color(0xFFD6E3FF)
private val BlueCont  = Color(0xFF1565C0)
private val BlueOnCont= Color(0xFFDAE5FF)
private val Gray500   = Color(0xFF727783)
private val Gray200   = Color(0xFFE1E2EA)
private val Surface   = Color(0xFFF9F9FF)
private val White     = Color(0xFFFFFFFF)
private val ErrorRed  = Color(0xFFBA1A1A)
private val ErrorCont = Color(0xFFFFDAD6)
private val GreenDark = Color(0xFF25752B)
private val GreenLight= Color(0xFFA3F69C)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalificacionesScreen(navController: NavController) {
    val context   = LocalContext.current
    val session   = remember { SessionDataStore(context) }
    val viewModel: CalificacionesViewModel = viewModel()
    val uiState   by viewModel.uiState.collectAsState()
    val rol       by session.rol.collectAsState(initial = "")
    val nombre    by session.nombre.collectAsState(initial = "")

    var mostrarRegistrar by remember { mutableStateOf(false) }

    if (mostrarRegistrar) {
        RegistrarNotaSheet(
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
                        Text("ComunicApp Escolar", fontSize = 18.sp,
                            fontWeight = FontWeight.Bold, color = Blue900)
                    }
                    Row(
                        verticalAlignment     = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text((nombre ?: "").split(" ").take(2).joinToString(" "),
                            fontSize = 12.sp, color = Gray500)
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
                    Icon(Icons.Default.Add, "Registrar nota")
                }
            }
        },
        bottomBar      = { BottomNavBar(navController) },
        containerColor = Surface
    ) { padding ->

        when (val estado = uiState) {
            is CalificacionesUiState.Loading -> {
                Box(
                    Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator(color = Blue900) }
            }
            is CalificacionesUiState.Error -> {
                Box(
                    Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.WifiOff, null,
                            tint = Gray500, modifier = Modifier.size(48.dp))
                        Spacer(Modifier.height(8.dp))
                        Text(estado.mensaje, color = Gray500)
                        Spacer(Modifier.height(12.dp))
                        Button(
                            onClick = viewModel::cargarCalificaciones,
                            colors  = ButtonDefaults.buttonColors(containerColor = Blue900)
                        ) { Text("Reintentar") }
                    }
                }
            }
            is CalificacionesUiState.Success -> {
                val cals     = estado.calificaciones
                val promedio = viewModel.promedio(cals)

                LazyColumn(
                    modifier        = Modifier.fillMaxSize().padding(padding),
                    contentPadding  = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {

                    // ── Selector de periodos ─────────────────
                    item {
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(4) { i ->
                                val p   = i + 1
                                val sel = viewModel.periodoActivo == p
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(50))
                                        .background(if (sel) Blue900 else Gray200)
                                        .clickable { viewModel.cambiarPeriodo(p) }
                                        .padding(horizontal = 20.dp, vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("Periodo $p", fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (sel) White else Gray500)
                                }
                            }
                        }
                    }

                    // ── Tarjeta de promedio + resumen ────────
                    item {
                        Row(
                            modifier              = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Tarjeta promedio con círculo animado
                            Card(
                                modifier = Modifier.weight(2f),
                                shape    = RoundedCornerShape(16.dp),
                                colors   = CardDefaults.cardColors(containerColor = BlueCont)
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("Rendimiento Académico",
                                            fontSize = 11.sp, color = BlueOnCont.copy(.8f),
                                            letterSpacing = 0.3.sp)
                                        Spacer(Modifier.height(4.dp))
                                        Text(viewModel.nivelRendimiento(promedio),
                                            fontSize = 20.sp, fontWeight = FontWeight.Bold,
                                            color = White)
                                        Spacer(Modifier.height(8.dp))
                                        Text(
                                            text = if (promedio >= 4.0)
                                                "¡Excelente desempeño académico!"
                                            else "Sigue esforzándote, vas bien.",
                                            fontSize = 12.sp, color = White.copy(.85f)
                                        )
                                    }
                                    // Círculo de progreso con Canvas
                                    CirculoProgreso(
                                        promedio = promedio,
                                        max      = 5.0
                                    )
                                }
                            }

                            // Tarjeta resumen
                            Card(
                                modifier = Modifier.weight(1f),
                                shape    = RoundedCornerShape(16.dp),
                                colors   = CardDefaults.cardColors(containerColor = White),
                                elevation = CardDefaults.cardElevation(1.dp)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text("Resumen", fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold, color = Blue900)
                                    Spacer(Modifier.height(10.dp))
                                    FilaResumen("Aprobadas",
                                        "${cals.count { it.nota >= 3.0 }} / ${cals.size}",
                                        GreenDark)
                                    HorizontalDivider(color = Gray200, modifier = Modifier.padding(vertical = 6.dp))
                                    FilaResumen("Promedio",
                                        String.format(Locale.getDefault(), "%.1f", promedio),
                                        Blue900)
                                    HorizontalDivider(color = Gray200, modifier = Modifier.padding(vertical = 6.dp))
                                    FilaResumen("Críticas",
                                        "${cals.count { it.nota < 3.0 }}",
                                        ErrorRed)
                                }
                            }
                        }
                    }

                    // ── Título detalle ───────────────────────
                    item {
                        Text("Detalle de Materias", fontSize = 22.sp,
                            fontWeight = FontWeight.Bold, color = Color(0xFF191C21))
                    }

                    // ── Grid de tarjetas de materias ─────────
                    items(cals.chunked(2)) { fila ->
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            fila.forEach { cal ->
                                TarjetaMateria(
                                    calificacion = cal,
                                    viewModel    = viewModel,
                                    modifier     = Modifier.weight(1f)
                                )
                            }
                            if (fila.size == 1) Spacer(Modifier.weight(1f))
                        }
                    }

                    item { Spacer(Modifier.height(80.dp)) }
                }
            }
        }
    }
}


// ── Círculo de progreso con Canvas animado ────────────────────
@Composable
fun CirculoProgreso(promedio: Double, max: Double) {
    val progreso  = (promedio / max).coerceIn(0.0, 1.0).toFloat()
    val animacion by animateFloatAsState(
        targetValue   = progreso,
        animationSpec = tween(1200, easing = FastOutSlowInEasing),
        label         = "progresoCirculo"
    )

    Box(
        modifier         = Modifier.size(96.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val stroke = Stroke(width = 10.dp.toPx(), cap = StrokeCap.Round)
            val inset  = stroke.width / 2
            val rect   = androidx.compose.ui.geometry.Rect(
                offset = Offset(inset, inset),
                size   = Size(size.width - stroke.width, size.height - stroke.width)
            )
            // Fondo del arco
            drawArc(
                color       = Color.White.copy(alpha = 0.2f),
                startAngle  = -90f,
                sweepAngle  = 360f,
                useCenter   = false,
                topLeft     = rect.topLeft,
                size        = rect.size,
                style       = stroke
            )
            // Arco de progreso
            drawArc(
                color       = Color.White,
                startAngle  = -90f,
                sweepAngle  = 360f * animacion,
                useCenter   = false,
                topLeft     = rect.topLeft,
                size        = rect.size,
                style       = stroke
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(String.format(Locale.getDefault(), "%.1f", promedio),
                fontSize   = 22.sp,
                fontWeight = FontWeight.Bold,
                color      = White)
            Text("/ 5.0", fontSize = 11.sp, color = White.copy(.7f))
        }
    }
}


// ── Fila del resumen ──────────────────────────────────────────
@Composable
fun FilaResumen(etiqueta: String, valor: String, color: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(etiqueta, fontSize = 11.sp, color = Gray500)
        Text(valor, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = color)
    }
}


// ── Tarjeta de materia ────────────────────────────────────────
@Composable
fun TarjetaMateria(
    calificacion: Calificacion,
    viewModel:    CalificacionesViewModel,
    modifier:     Modifier = Modifier
) {
    val (colorNota, labelBadge) = viewModel.badgeInfo(calificacion.nota)

    // Color del borde según nota
    val colorBorde = when {
        calificacion.nota >= 4.5 -> Blue900
        calificacion.nota >= 4.0 -> Blue800
        calificacion.nota >= 3.0 -> GreenDark
        else                     -> ErrorRed
    }

    // Color de la barra de progreso
    val colorBarra = colorBorde

    // Animación de la barra
    val progreso  = (calificacion.nota / 5.0).toFloat()
    val animBarra by animateFloatAsState(
        targetValue   = progreso,
        animationSpec = tween(1000, easing = FastOutSlowInEasing),
        label         = "barra_${calificacion.id}"
    )

    // Color del badge fondo
    val (badgeFondo, badgeTexto) = when {
        calificacion.nota >= 4.5 -> GreenLight to Color(0xFF002204)
        calificacion.nota >= 4.0 -> BlueFixed  to Blue900
        calificacion.nota >= 3.0 -> Gray200    to Gray500
        else                     -> ErrorCont  to ErrorRed
    }

    Card(
        modifier  = modifier,
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
                        color = colorBorde,
                        shape = RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp)
                    )
            )
            Column(modifier = Modifier.weight(1f).padding(12.dp)) {
                // Materia + nota
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text       = calificacion.materia,
                            fontSize   = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color      = Color(0xFF191C21),
                            maxLines   = 1,
                            overflow   = TextOverflow.Ellipsis
                        )
                        Text(
                            text     = calificacion.docente_nombre ?: "Docente",
                            fontSize = 11.sp,
                            color    = Gray500,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text       = String.format(Locale.getDefault(), "%.1f", calificacion.nota),
                            fontSize   = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color      = colorNota
                        )
                        Text("/ 5.0", fontSize = 10.sp, color = Gray500)
                    }
                }

                Spacer(Modifier.height(8.dp))

                // Barra de progreso animada
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(50))
                        .background(Gray200)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(animBarra)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(50))
                            .background(colorBarra)
                    )
                }

                Spacer(Modifier.height(8.dp))

                // Badge de rendimiento
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(badgeFondo)
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(labelBadge, fontSize = 10.sp,
                            fontWeight = FontWeight.Bold, color = badgeTexto)
                    }
                    Icon(Icons.Default.ChevronRight, null,
                        tint = Gray200, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}