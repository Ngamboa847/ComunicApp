package com.example.comunicappescolar.ui.screens.avisos

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.comunicappescolar.data.model.Aviso
import com.example.comunicappescolar.ui.screens.home.BottomNavBar
import kotlinx.coroutines.delay

// ── Colores ──────────────────────────────────────────────────
private val Blue900    = Color(0xFF004D99)
private val Blue800    = Color(0xFF1565C0)
private val BlueFixed  = Color(0xFFD6E3FF)
private val Blue50     = Color(0xFFE3F2FD)
private val Gray500    = Color(0xFF727783)
private val Gray200    = Color(0xFFC2C6D4)
private val Surface    = Color(0xFFF9F9FF)
private val ErrorCont  = Color(0xFFFFDAD6)
private val TertFixed  = Color(0xFFA3F69C)
private val TertText   = Color(0xFF002204)
private val SecFixed   = Color(0xFFD4E3FF)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleAvisoScreen(
    navController: NavController,
    avisoId:       Int,
    viewModel:     AvisosViewModel = viewModel()
) {
    // Buscar el aviso en el estado actual
    val uiState by viewModel.uiState.collectAsState()
    val aviso = (uiState as? AvisosUiState.Success)
        ?.avisos?.find { it.id == avisoId }

    // Badge "leído" con animación de rebote
    var mostrarBadge by remember { mutableStateOf(true) }
    LaunchedEffect(Unit) {
        delay(3000)
        mostrarBadge = false
    }

    // Config visual por categoría
    val colorBadgeFondo: Color
    val colorBadgeTexto: Color
    val labelCategoria: String

    when (aviso?.categoria) {
        "urgente" -> {
            colorBadgeFondo = ErrorCont
            colorBadgeTexto = Color(0xFF93000A)
            labelCategoria = "URGENTE"
        }
        "evento" -> {
            colorBadgeFondo = TertFixed
            colorBadgeTexto = Color(0xFF002204)
            labelCategoria = "EVENTO"
        }
        else -> {
            colorBadgeFondo = Blue50
            colorBadgeTexto = Blue900
            labelCategoria = "AVISO ACADÉMICO"
        }
    }

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
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Blue900)
                        }
                        Text("ComunicApp Escolar", fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold, color = Blue900)
                    }
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(BlueFixed),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Person, null,
                            tint = Blue900, modifier = Modifier.size(18.dp))
                    }
                    Spacer(Modifier.width(8.dp))
                }
            }
        },
        bottomBar      = { BottomNavBar(navController) },
        containerColor = Surface
    ) { padding ->

        if (aviso == null) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.ErrorOutline, null,
                        tint = Gray500, modifier = Modifier.size(48.dp))
                    Spacer(Modifier.height(8.dp))
                    Text("Aviso no encontrado", color = Gray500)
                    Spacer(Modifier.height(12.dp))
                    OutlinedButton(onClick = { navController.popBackStack() }) {
                        Text("Volver", color = Blue900)
                    }
                }
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {

            // ── Tarjeta de encabezado con imagen ─────────────
            Card(
                modifier  = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape     = RoundedCornerShape(16.dp),
                colors    = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column {
                    // Imagen decorativa de encabezado
                    Box(modifier = Modifier.fillMaxWidth().height(180.dp)) {
                        AsyncImage(
                            model              = "https://lh3.googleusercontent.com/aida-public/AB6AXuCh46nH0kxc5jiLtH4V3FYoltk-dDVEvShNSaZmAH0cQZYxJn4ZiH8R-1v2uLO29ls9gYbahtW9fXr3CKGcizLfusd98HI6hl0a9JC0LXGeYL67a9bgPyTmkFT1X8y5AVLF-7rj4YXSsoN-I4279W3cmW12ZQSFiH1z6h2hrhw3yka3kVVUS3pDJHtzaJby7lbwSa4NiBq0QRVs4xDcA9Mr-TJzy7Yni11Op3tWxgt2k_jxKDnCn5hHeGzZ49Zy5uSJufGj_Jv55tQ",
                            contentDescription = "Imagen del aviso",
                            contentScale       = ContentScale.Crop,
                            modifier           = Modifier.fillMaxSize()
                        )
                        // Degradado sobre la imagen
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            Color.Transparent,
                                            Color.Black.copy(alpha = 0.3f)
                                        )
                                    )
                                )
                        )
                        // Badge de categoría sobre la imagen
                        Box(
                            modifier = Modifier
                                .padding(12.dp)
                                .align(Alignment.TopStart)
                                .clip(RoundedCornerShape(50))
                                .background(colorBadgeFondo)
                                .padding(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            Text(labelCategoria, fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = colorBadgeTexto, letterSpacing = 0.8.sp)
                        }
                    }

                    Column(modifier = Modifier.padding(16.dp)) {
                        // Título
                        Text(
                            text       = aviso.titulo,
                            fontSize   = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color      = Color(0xFF191C21),
                            lineHeight = 30.sp
                        )

                        Spacer(Modifier.height(16.dp))
                        HorizontalDivider(color = Gray200.copy(alpha = 0.5f))
                        Spacer(Modifier.height(12.dp))

                        // Autor y fecha
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(SecFixed),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Person, null,
                                    tint = Blue900, modifier = Modifier.size(22.dp))
                            }
                            Spacer(Modifier.width(12.dp))
                            Column {
                                Text(
                                    text       = aviso.autor_nombre ?: "Administración",
                                    fontSize   = 15.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color      = Color(0xFF191C21)
                                )
                                Text(
                                    text     = aviso.fecha,
                                    fontSize = 11.sp,
                                    color    = Gray500
                                )
                            }
                        }
                    }
                }
            }

            // ── Tarjeta de contenido ─────────────────────────
            Card(
                modifier  = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape     = RoundedCornerShape(16.dp),
                colors    = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {

                    Text(
                        text       = aviso.contenido ?: aviso.descripcion ?: "",
                        fontSize   = 15.sp,
                        color      = Gray500,
                        lineHeight = 24.sp
                    )

                    Spacer(Modifier.height(16.dp))

                    // Fechas clave (si es evento)
                    if (aviso.categoria == "evento") {
                        Text("Fechas clave para recordar:", fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold, color = Color(0xFF191C21))
                        Spacer(Modifier.height(8.dp))
                        listOf(
                            Icons.Default.CalendarToday to "Apertura de inscripciones: próximamente",
                            Icons.Default.EventAvailable to "Fecha límite de propuesta: a confirmar",
                            Icons.Default.RocketLaunch to "Evento principal: a confirmar"
                        ).forEach { (icono, texto) ->
                            Row(
                                modifier = Modifier.padding(vertical = 4.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Icon(icono, null, tint = Blue800,
                                    modifier = Modifier.size(18.dp).padding(top = 2.dp))
                                Spacer(Modifier.width(8.dp))
                                Text(texto, fontSize = 13.sp, color = Gray500)
                            }
                        }
                        Spacer(Modifier.height(12.dp))
                    }

                    // Cita/Quote destacada
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Blue50)
                            .then(
                                Modifier.padding(start = 0.dp)
                            )
                    ) {
                        Row {
                            Box(
                                modifier = Modifier
                                    .width(4.dp)
                                    .fillMaxHeight()
                                    .background(Blue800)
                            )
                            Text(
                                text      = "\"La comunicación efectiva entre el colegio y la familia es clave para el éxito de los estudiantes.\"",
                                fontSize  = 13.sp,
                                color     = Color(0xFF191C21),
                                fontStyle = FontStyle.Italic,
                                modifier  = Modifier.padding(12.dp)
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // ── Sección de acciones ──────────────────────────
            Column(
                modifier            = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Badge "Marcado como leído" con animación
                AnimatedVisibility(
                    visible = mostrarBadge && aviso.leido == 1,
                    enter   = fadeIn() + scaleIn(),
                    exit    = fadeOut() + scaleOut()
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(TertFixed)
                            .padding(horizontal = 20.dp, vertical = 8.dp)
                    ) {
                        Row(
                            verticalAlignment     = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(Icons.Default.CheckCircle, null,
                                tint = TertText, modifier = Modifier.size(18.dp))
                            Text("Marcado como leído", fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold, color = TertText)
                        }
                    }
                }

                // Botón volver
                OutlinedButton(
                    onClick  = { navController.popBackStack() },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape    = RoundedCornerShape(50),
                    colors   = ButtonDefaults.outlinedButtonColors(contentColor = Blue900),
                    border   = BorderStroke(
                        width = 1.dp,
                        brush = androidx.compose.ui.graphics.SolidColor(Blue900)
                    )
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, null,
                        modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Volver a avisos", fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}