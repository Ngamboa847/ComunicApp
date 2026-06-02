package com.example.comunicappescolar.ui.screens.perfil


import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.comunicappescolar.data.local.SessionDataStore
import com.example.comunicappescolar.data.model.Usuario
import com.example.comunicappescolar.ui.navigation.AppRoutes
import com.example.comunicappescolar.ui.screens.home.BottomNavBar

// ── Colores ──────────────────────────────────────────────────
private val Blue900   = Color(0xFF004D99)
private val Blue800   = Color(0xFF1565C0)
private val Blue700   = Color(0xFF1976D2)
private val BlueFixed = Color(0xFFD6E3FF)
private val Gray500   = Color(0xFF727783)
private val Gray200   = Color(0xFFE1E2EA)
private val Surface   = Color(0xFFF9F9FF)
private val White     = Color(0xFFFFFFFF)
private val ErrorRed  = Color(0xFFBA1A1A)
private val ErrorCont = Color(0xFFFFDAD6)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilScreen(navController: NavController) {
    val context   = LocalContext.current
    val session   = remember { SessionDataStore(context) }
    val viewModel: PerfilViewModel = viewModel(factory = PerfilViewModelFactory(session))
    val uiState   by viewModel.uiState.collectAsState()
    val editarState by viewModel.editarState.collectAsState()

    var mostrarEditar        by remember { mutableStateOf(false) }
    var mostrarConfirmLogout by remember { mutableStateOf(false) }
    var fotoUri              by remember { mutableStateOf<Uri?>(null) }

    // Selector de imagen de galería
    val imagePicker = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri -> fotoUri = uri }

    // Cerrar sheet al guardar exitosamente
    LaunchedEffect(editarState) {
        if (editarState is EditarPerfilUiState.Success) {
            mostrarEditar = false
            viewModel.resetEditarState()
        }
    }

    // Sheet de edición
    if (mostrarEditar) {
        EditarPerfilSheet(
            viewModel  = viewModel,
            fotoUri    = fotoUri,
            onPickFoto = { imagePicker.launch("image/*") },
            onDismiss  = { mostrarEditar = false }
        )
    }

    // Diálogo de confirmación de cierre de sesión
    if (mostrarConfirmLogout) {
        AlertDialog(
            onDismissRequest = { mostrarConfirmLogout = false },
            icon  = {
                Icon(Icons.Default.Logout, null,
                    tint = ErrorRed, modifier = Modifier.size(28.dp))
            },
            title = {
                Text("Cerrar sesión", fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center)
            },
            text  = {
                Text("¿Estás seguro que deseas salir de la aplicación?",
                    textAlign = TextAlign.Center, color = Gray500)
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.cerrarSesion()
                        mostrarConfirmLogout = false
                        navController.navigate(AppRoutes.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ErrorRed),
                    shape  = RoundedCornerShape(50)
                ) { Text("Sí, salir") }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { mostrarConfirmLogout = false },
                    shape   = RoundedCornerShape(50)
                ) { Text("Cancelar", color = Blue900) }
            }
        )
    }

    Scaffold(
        topBar = {
            Surface(shadowElevation = 0.dp, color = Blue800) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .padding(horizontal = 8.dp),
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.Default.ArrowBack, null, tint = White)
                        }
                        Text("Perfil", fontSize = 18.sp,
                            fontWeight = FontWeight.Bold, color = White)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = {
                            (uiState as? PerfilUiState.Success)?.let {
                                viewModel.iniciarEdicion(it.usuario)
                            }
                            mostrarEditar = true
                        }) {
                            Icon(Icons.Default.Edit, null, tint = White)
                        }
                        // Avatar pequeño en topbar
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .border(2.dp, White, CircleShape)
                                .background(BlueFixed),
                            contentAlignment = Alignment.Center
                        ) {
                            if (fotoUri != null) {
                                AsyncImage(
                                    model              = fotoUri,
                                    contentDescription = null,
                                    contentScale       = ContentScale.Crop,
                                    modifier           = Modifier.fillMaxSize()
                                )
                            } else {
                                val nombre = (uiState as? PerfilUiState.Success)
                                    ?.usuario?.nombre ?: ""
                                Text(nombre.take(1).uppercase(),
                                    fontSize = 14.sp, fontWeight = FontWeight.Bold,
                                    color = Blue900)
                            }
                        }
                        Spacer(Modifier.width(8.dp))
                    }
                }
            }
        },
        bottomBar      = { BottomNavBar(navController) },
        containerColor = Surface
    ) { padding ->

        when (val estado = uiState) {
            is PerfilUiState.Loading -> {
                Box(Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Blue900)
                }
            }
            is PerfilUiState.Error -> {
                Box(Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.ErrorOutline, null,
                            tint = Gray500, modifier = Modifier.size(48.dp))
                        Spacer(Modifier.height(8.dp))
                        Text(estado.mensaje, color = Gray500)
                        Spacer(Modifier.height(12.dp))
                        Button(onClick = viewModel::cargarPerfil,
                            colors = ButtonDefaults.buttonColors(containerColor = Blue900)
                        ) { Text("Reintentar") }
                    }
                }
            }
            is PerfilUiState.Success -> {
                val usuario = estado.usuario
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .verticalScroll(rememberScrollState())
                ) {

                    // ── Header azul con foto ─────────────────
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(Blue800, Blue700)
                                )
                            )
                            .padding(bottom = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier            = Modifier.padding(top = 24.dp)
                        ) {
                            // Foto de perfil con badge de rol
                            Box(contentAlignment = Alignment.BottomEnd) {
                                Box(
                                    modifier = Modifier
                                        .size(110.dp)
                                        .clip(CircleShape)
                                        .border(3.dp, White, CircleShape)
                                        .background(BlueFixed)
                                        .clickable {
                                            viewModel.iniciarEdicion(usuario)
                                            mostrarEditar = true
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    when {
                                        fotoUri != null -> AsyncImage(
                                            model              = fotoUri,
                                            contentDescription = null,
                                            contentScale       = ContentScale.Crop,
                                            modifier           = Modifier.fillMaxSize()
                                        )
                                        !usuario.avatar_url.isNullOrBlank() -> AsyncImage(
                                            model              = usuario.avatar_url,
                                            contentDescription = null,
                                            contentScale       = ContentScale.Crop,
                                            modifier           = Modifier.fillMaxSize()
                                        )
                                        else -> Text(
                                            text       = usuario.nombre.take(1).uppercase(),
                                            fontSize   = 42.sp,
                                            fontWeight = FontWeight.Bold,
                                            color      = Blue900
                                        )
                                    }
                                }
                                // Badge de verificación
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(Blue900)
                                        .border(2.dp, White, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Verified, null,
                                        tint = White, modifier = Modifier.size(16.dp))
                                }
                            }

                            Spacer(Modifier.height(12.dp))

                            // Nombre
                            Text(usuario.nombre, fontSize = 22.sp,
                                fontWeight = FontWeight.Bold, color = White)

                            Spacer(Modifier.height(6.dp))

                            // Badge de rol
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(50))
                                    .background(White.copy(alpha = 0.2f))
                                    .padding(horizontal = 16.dp, vertical = 5.dp)
                            ) {
                                Text(
                                    text = when (usuario.rol) {
                                        "docente" -> "Docente"
                                        "admin"   -> "Administrador"
                                        else      -> "Padre"
                                    },
                                    fontSize   = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color      = White
                                )
                            }

                            Spacer(Modifier.height(16.dp))
                        }
                    }

                    // ── Estadísticas superpuestas ────────────
                    Card(
                        modifier  = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .offset(y = (-28).dp),
                        shape     = RoundedCornerShape(16.dp),
                        colors    = CardDefaults.cardColors(containerColor = White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 20.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            EstadisticaPerfil("24", "Avisos\nleídos",    Blue900)
                            VerticalDivider(modifier = Modifier.height(48.dp), color = Gray200)
                            EstadisticaPerfil("12", "Mensajes",          Blue900)
                            VerticalDivider(modifier = Modifier.height(48.dp), color = Gray200)
                            EstadisticaPerfil("05", "Tareas\npendientes",Blue900)
                        }
                    }

                    // ── Tarjetas de información ──────────────
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .offset(y = (-12).dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {

                        // Datos personales
                        Card(
                            modifier  = Modifier.fillMaxWidth(),
                            shape     = RoundedCornerShape(16.dp),
                            colors    = CardDefaults.cardColors(containerColor = White),
                            elevation = CardDefaults.cardElevation(1.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                SeccionHeader(
                                    icono  = Icons.Default.Person,
                                    titulo = "Datos Personales"
                                )
                                Spacer(Modifier.height(12.dp))
                                FilaDato("Correo electrónico", usuario.email)
                                Spacer(Modifier.height(10.dp))
                                FilaDato(
                                    label = "Número de teléfono",
                                    valor = usuario.telefono ?: "No registrado"
                                )
                            }
                        }

                        // Información escolar
                        Card(
                            modifier  = Modifier.fillMaxWidth(),
                            shape     = RoundedCornerShape(16.dp),
                            colors    = CardDefaults.cardColors(containerColor = White),
                            elevation = CardDefaults.cardElevation(1.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                SeccionHeader(
                                    icono  = Icons.Default.School,
                                    titulo = "Información Escolar"
                                )
                                Spacer(Modifier.height(12.dp))

                                // Fila de hijo/materia
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Surface)
                                        .padding(12.dp),
                                    verticalAlignment     = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .background(BlueFixed),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = if (usuario.rol == "docente")
                                                Icons.Default.MenuBook
                                            else Icons.Default.EmojiPeople,
                                            contentDescription = null,
                                            tint     = Blue900,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = if (usuario.rol == "docente")
                                                usuario.grado ?: "Materia no registrada"
                                            else "Estudiante",
                                            fontSize   = 14.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color      = Color(0xFF191C21)
                                        )
                                        Text(
                                            text = usuario.colegio ?: "Institución no registrada",
                                            fontSize = 12.sp, color = Gray500
                                        )
                                    }
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(50))
                                            .background(BlueFixed)
                                            .padding(horizontal = 10.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = usuario.grado ?: "Sin grado",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Blue900
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(Modifier.height(8.dp))

                        // Botón cerrar sesión
                        OutlinedButton(
                            onClick  = { mostrarConfirmLogout = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            shape    = RoundedCornerShape(50),
                            colors   = ButtonDefaults.outlinedButtonColors(
                                contentColor = ErrorRed
                            ),
                            border   = ButtonDefaults.outlinedButtonBorder.copy(
                                brush = androidx.compose.ui.graphics.SolidColor(ErrorRed)
                            )
                        ) {
                            Icon(Icons.Default.Logout, null,
                                modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Cerrar sesión", fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold)
                        }

                        // Versión
                        Text(
                            text      = "Versión 2.4.0 (Build 4452)",
                            fontSize  = 11.sp,
                            color     = Gray500.copy(alpha = 0.6f),
                            textAlign = TextAlign.Center,
                            modifier  = Modifier.fillMaxWidth()
                        )

                        Spacer(Modifier.height(24.dp))
                    }
                }
            }
        }
    }
}


// ── Composables auxiliares ────────────────────────────────────

@Composable
fun EstadisticaPerfil(numero: String, etiqueta: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(numero, fontSize = 26.sp, fontWeight = FontWeight.Bold, color = color)
        Text(etiqueta, fontSize = 11.sp, color = Gray500,
            textAlign = TextAlign.Center, lineHeight = 15.sp)
    }
}

@Composable
fun SeccionHeader(
    icono:  androidx.compose.ui.graphics.vector.ImageVector,
    titulo: String
) {
    Row(
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(BlueFixed),
            contentAlignment = Alignment.Center
        ) {
            Icon(icono, null, tint = Blue900, modifier = Modifier.size(18.dp))
        }
        Text(titulo, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFF191C21))
    }
}

@Composable
fun FilaDato(label: String, valor: String) {
    Column {
        Text(label, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Gray500)
        Spacer(Modifier.height(2.dp))
        Text(valor, fontSize = 14.sp, color = Color(0xFF191C21))
    }
}