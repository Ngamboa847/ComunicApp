package com.example.comunicappescolar.ui.screens.directorio

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
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
import com.example.comunicappescolar.data.model.Usuario
import com.example.comunicappescolar.ui.navigation.AppRoutes
import com.example.comunicappescolar.ui.screens.home.BottomNavBar

// ── Colores ──────────────────────────────────────────────────
private val Blue900   = Color(0xFF004D99)
private val Blue800   = Color(0xFF1565C0)
private val BlueFixed = Color(0xFFD6E3FF)
private val BlueOnFix = Color(0xFF001B3D)
private val SecFixed  = Color(0xFFD4E3FF)
private val SecOnFix  = Color(0xFF004786)
private val Gray500   = Color(0xFF727783)
private val Gray200   = Color(0xFFE1E2EA)
private val SurfCont  = Color(0xFFECEDF6)
private val Surface   = Color(0xFFF9F9FF)
private val White     = Color(0xFFFFFFFF)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DirectorioScreen(navController: NavController) {
    val context   = LocalContext.current
    val session   = remember { SessionDataStore(context) }
    val viewModel: DirectorioViewModel = viewModel()
    val uiState   by viewModel.uiState.collectAsState()

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
        bottomBar      = { BottomNavBar(navController) },
        containerColor = Surface
    ) { padding ->

        LazyColumn(
            modifier       = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // ── Barra de búsqueda ────────────────────────────
            item {
                OutlinedTextField(
                    value         = viewModel.textoBusqueda,
                    onValueChange = { viewModel.textoBusqueda = it },
                    placeholder   = {
                        Text("Buscar profesores por nombre o materia...",
                            color = Gray500, fontSize = 13.sp)
                    },
                    leadingIcon   = {
                        Icon(Icons.Default.Search, null, tint = Gray500)
                    },
                    trailingIcon  = {
                        if (viewModel.textoBusqueda.isNotBlank()) {
                            IconButton(onClick = { viewModel.textoBusqueda = "" }) {
                                Icon(Icons.Default.Clear, null, tint = Gray500)
                            }
                        }
                    },
                    singleLine    = true,
                    shape         = RoundedCornerShape(50),
                    modifier      = Modifier.fillMaxWidth(),
                    colors        = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor      = Blue800,
                        unfocusedBorderColor    = Gray200,
                        focusedContainerColor   = White,
                        unfocusedContainerColor = White
                    )
                )
            }

            // ── Chips de materia ─────────────────────────────
            when (val estado = uiState) {
                is DirectorioUiState.Success -> {
                    item {
                        val materias = viewModel.materias(estado.docentes)
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            contentPadding        = PaddingValues(vertical = 2.dp)
                        ) {
                            items(materias) { materia ->
                                val sel = viewModel.filtroMateria == materia
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(50))
                                        .background(if (sel) BlueFixed else Gray200)
                                        .clickable { viewModel.filtroMateria = materia }
                                        .padding(horizontal = 14.dp, vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(materia, fontSize = 12.sp,
                                        fontWeight = if (sel) FontWeight.Bold else FontWeight.Normal,
                                        color = if (sel) Blue900 else Gray500)
                                }
                            }
                        }
                    }
                }
                else -> {}
            }

            // ── Título ───────────────────────────────────────
            item {
                Column {
                    Text("Directorio de Profesores", fontSize = 24.sp,
                        fontWeight = FontWeight.Bold, color = Color(0xFF191C21))
                    Text("Conéctate con los educadores de tus hijos",
                        fontSize = 13.sp, color = Gray500)
                }
            }

            // ── Contenido ────────────────────────────────────
            when (val estado = uiState) {
                is DirectorioUiState.Loading -> {
                    item {
                        Box(Modifier.fillMaxWidth().height(300.dp),
                            contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = Blue900)
                        }
                    }
                }
                is DirectorioUiState.Error -> {
                    item {
                        Box(Modifier.fillMaxWidth().height(300.dp),
                            contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.WifiOff, null,
                                    tint = Gray500, modifier = Modifier.size(48.dp))
                                Spacer(Modifier.height(8.dp))
                                Text(estado.mensaje, color = Gray500)
                                Spacer(Modifier.height(12.dp))
                                Button(
                                    onClick = viewModel::cargarDocentes,
                                    colors  = ButtonDefaults.buttonColors(containerColor = Blue900)
                                ) { Text("Reintentar") }
                            }
                        }
                    }
                }
                is DirectorioUiState.Success -> {
                    val filtrados = viewModel.docentesFiltrados(estado.docentes)

                    if (filtrados.isEmpty()) {
                        item {
                            Box(Modifier.fillMaxWidth().height(200.dp),
                                contentAlignment = Alignment.Center) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(Icons.Default.SearchOff, null,
                                        tint = Gray200, modifier = Modifier.size(48.dp))
                                    Spacer(Modifier.height(8.dp))
                                    Text("No se encontraron profesores", color = Gray500)
                                }
                            }
                        }
                    } else {
                        // Grid 2 columnas con chunked
                        itemsIndexed(filtrados.chunked(2)) { _, fila ->
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                modifier              = Modifier.fillMaxWidth()
                            ) {
                                fila.forEachIndexed { i, docente ->
                                    val globalIndex = filtrados.indexOf(docente)
                                    TarjetaDocente(
                                        docente      = docente,
                                        colorAvatar  = viewModel.colorAvatar(globalIndex),
                                        tieneActividad = globalIndex == 1, // simula docente activo
                                        modifier     = Modifier.weight(1f),
                                        onLlamar     = {
                                            docente.telefono?.let { tel ->
                                                val intent = Intent(Intent.ACTION_DIAL,
                                                    Uri.parse("tel:$tel"))
                                                context.startActivity(intent)
                                            }
                                        },
                                        onMensaje    = {
                                            navController.navigate(AppRoutes.Chat.createRoute(docente.id, docente.nombre))
                                        }
                                    )
                                }
                                if (fila.size == 1) Spacer(Modifier.weight(1f))
                            }
                        }
                    }
                }
            }

            item { Spacer(Modifier.height(80.dp)) }
        }
    }
}


// ── Tarjeta de docente ────────────────────────────────────────
@Composable
fun TarjetaDocente(
    docente:         Usuario,
    colorAvatar:     Pair<Color, Color>,
    tieneActividad:  Boolean = false,
    modifier:        Modifier = Modifier,
    onLlamar:        () -> Unit,
    onMensaje:       () -> Unit
) {
    // Animación del punto pulsante
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val alpha by infiniteTransition.animateFloat(
        initialValue   = 0.3f,
        targetValue    = 1f,
        animationSpec  = infiniteRepeatable(
            animation  = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    Card(
        modifier  = modifier,
        shape     = RoundedCornerShape(16.dp),
        colors    = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (tieneActividad) 4.dp else 1.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            // Borde lateral solo si tiene actividad
            if (tieneActividad) {
                Box(
                    modifier = Modifier
                        .width(4.dp)
                        .fillMaxHeight()
                        .background(
                            color = Blue900,
                            shape = RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp)
                        )
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(14.dp)
            ) {
                // Avatar + nombre
                Row(
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Avatar con iniciales
                    Box(modifier = Modifier.size(52.dp)) {
                        Box(
                            modifier = Modifier
                                .size(52.dp)
                                .clip(CircleShape)
                                .background(colorAvatar.first),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text       = docente.nombre
                                    .split(" ")
                                    .take(2)
                                    .mapNotNull { it.firstOrNull()?.uppercase() }
                                    .joinToString(""),
                                fontSize   = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color      = colorAvatar.second
                            )
                        }
                        // Punto pulsante si tiene actividad
                        if (tieneActividad) {
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .align(Alignment.BottomEnd)
                                    .clip(CircleShape)
                                    .background(White)
                                    .padding(2.dp)
                                    .clip(CircleShape)
                                    .background(Blue900.copy(alpha = alpha))
                            )
                        }
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text       = docente.nombre,
                                fontSize   = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color      = Color(0xFF191C21),
                                maxLines   = 1,
                                overflow   = TextOverflow.Ellipsis,
                                modifier   = Modifier.weight(1f)
                            )
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))

                // Badge de materia + grado
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(SecFixed)
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text       = docente.grado ?: "Docente",
                            fontSize   = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color      = SecOnFix,
                            maxLines   = 1,
                            overflow   = TextOverflow.Ellipsis
                        )
                    }
                }

                Spacer(Modifier.height(2.dp))

                // Email
                if (!docente.email.isNullOrBlank()) {
                    Text(
                        text     = docente.email,
                        fontSize = 10.sp,
                        color    = Gray500,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(Modifier.height(12.dp))

                // Botones de acción
                if (tieneActividad) {
                    // Variante activa: botón de mensaje expandido
                    Row(
                        modifier              = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        IconButton(
                            onClick  = onLlamar,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(SurfCont)
                        ) {
                            Icon(Icons.Default.Phone, null,
                                tint = Blue900, modifier = Modifier.size(18.dp))
                        }
                        Button(
                            onClick  = onMensaje,
                            modifier = Modifier.weight(1f).height(40.dp),
                            shape    = RoundedCornerShape(50),
                            colors   = ButtonDefaults.buttonColors(containerColor = Blue900)
                        ) {
                            Icon(Icons.Default.Chat, null,
                                modifier = Modifier.size(15.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Nuevo Mensaje", fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold)
                        }
                    }
                } else {
                    // Variante normal: dos botones iguales
                    Row(
                        modifier              = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            IconButton(
                                onClick  = onLlamar,
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(SurfCont)
                            ) {
                                Icon(Icons.Default.Phone, null,
                                    tint = Blue900, modifier = Modifier.size(18.dp))
                            }
                            IconButton(
                                onClick  = onMensaje,
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(SurfCont)
                            ) {
                                Icon(Icons.Default.Chat, null,
                                    tint = Blue900, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}