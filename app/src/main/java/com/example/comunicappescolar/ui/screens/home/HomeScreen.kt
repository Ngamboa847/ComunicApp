package com.example.comunicappescolar.ui.screens.home


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import coil.compose.AsyncImage
import com.example.comunicappescolar.data.local.SessionDataStore
import com.example.comunicappescolar.ui.navigation.AppRoutes
import java.util.Calendar

// ── Colores ──────────────────────────────────────────────────
private val Blue900      = Color(0xFF004D99)
private val Blue800      = Color(0xFF1565C0)
private val Blue50       = Color(0xFFE3F2FD)
private val BlueFixed    = Color(0xFFD6E3FF)
private val BlueSecCont  = Color(0xFF54A0FE)
private val BlueTertCont = Color(0xFF25752B)
private val Gray500      = Color(0xFF727783)
private val Surface      = Color(0xFFF9F9FF)
private val SurfaceCont  = Color(0xFFECEDF6)

// ── Modelo de ítem del menú ───────────────────────────────────
data class ItemMenu(
    val titulo:      String,
    val descripcion: String,
    val icono:       ImageVector,
    val colorFondo:  Color,
    val colorIcono:  Color,
    val ruta:        String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    val context   = LocalContext.current
    val session   = remember { SessionDataStore(context) }
    val viewModel: HomeViewModel = viewModel(factory = HomeViewModelFactory(session))

    val nombre by viewModel.nombre.collectAsState()
    val rol    by viewModel.rol.collectAsState()

    // Saludo según hora del día
    val saludo = remember {
        val hora = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        when {
            hora < 12 -> "Buenos días"
            hora < 18 -> "Buenas tardes"
            else      -> "Buenas noches"
        }
    }

    val primerNombre = nombre.split(" ").firstOrNull() ?: ""

    val itemsMenu = listOf(
        ItemMenu("Avisos",        "Boletines escolares",      Icons.Default.Notifications, BlueFixed,            Blue900,            AppRoutes.Avisos.route),
        ItemMenu("Mensajes",      "Chat con profesores",      Icons.Default.Chat,          BlueSecCont.copy(.2f),Color(0xFF003567),  AppRoutes.Mensajes.route),
        ItemMenu("Tareas",        "Deberes y actividades",    Icons.Default.Checklist,     Color(0xFFA5F99E).copy(.4f), Color(0xFF005C15), AppRoutes.Tareas.route),
        ItemMenu("Calificaciones","Progreso académico",       Icons.Default.BarChart,      Color(0xFF6366F1).copy(.15f), Color(0xFF4338CA), AppRoutes.Calificaciones.route),
        ItemMenu("Asistencia",    "Seguimiento de presencia", Icons.Default.CalendarToday, Color(0xFFF97316).copy(.15f), Color(0xFFEA580C), AppRoutes.Asistencia.route),
        ItemMenu("Calendario",    "Eventos y festivos",       Icons.Default.Event,         Color(0xFFA855F7).copy(.15f), Color(0xFF7C3AED), AppRoutes.Calendario.route),
        ItemMenu("Directorio",    "Contactos del personal",   Icons.Default.Group,         Color(0xFF06B6D4).copy(.15f), Color(0xFF0891B2), AppRoutes.Directorio.route),
        ItemMenu("Perfil",        "Ajustes de cuenta",        Icons.Default.Person,        Color(0xFFEF4444).copy(.15f), Color(0xFFDC2626), AppRoutes.Perfil.route),
    )

    Scaffold(
        // ── Bottom Navigation Bar ────────────────────────────
        bottomBar = {
            BottomNavBar(navController)
        },
        containerColor = Surface
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {

            // ── Top App Bar ──────────────────────────────────
            Surface(shadowElevation = 2.dp, color = Color.White) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    Text(
                        text       = "ComunicApp Escolar",
                        fontSize   = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color      = Blue900
                    )
                    // Avatar del usuario
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(BlueFixed)
                            .clickable { navController.navigate(AppRoutes.Perfil.route) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text       = primerNombre.take(1).uppercase(),
                            fontSize   = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color      = Blue900
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {

                Spacer(Modifier.height(20.dp))

                // ── Sección bienvenida ───────────────────────
                Text(
                    text       = "$saludo, $primerNombre",
                    fontSize   = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color      = Color(0xFF191C21)
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text     = if (rol == "padre")
                        "Tus hijos van muy bien hoy. Esto es lo que está pasando."
                    else
                        "Bienvenido de nuevo. Aquí está tu resumen del día.",
                    fontSize = 14.sp,
                    color    = Gray500
                )

                Spacer(Modifier.height(24.dp))

                // ── Bento Grid 2x4 ───────────────────────────
                val filas = itemsMenu.chunked(2)
                filas.forEach { fila ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        fila.forEach { item ->
                            TarjetaMenu(
                                item     = item,
                                modifier = Modifier.weight(1f),
                                onClick  = { navController.navigate(item.ruta) }
                            )
                        }
                        if (fila.size == 1) Spacer(Modifier.weight(1f))
                    }
                    Spacer(Modifier.height(12.dp))
                }

                Spacer(Modifier.height(8.dp))

                // ── Sección inferior ─────────────────────────
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Actividad Reciente
                    Card(
                        modifier = Modifier.weight(2f),
                        shape    = RoundedCornerShape(16.dp),
                        colors   = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment     = Alignment.CenterVertically
                            ) {
                                Text("Actividad Reciente", fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold)
                                TextButton(onClick = { navController.navigate(AppRoutes.Avisos.route) }) {
                                    Text("Ver todo", fontSize = 11.sp, color = Blue800)
                                }
                            }

                            // Actividad 1
                            FilaActividad(
                                colorFondo  = Color(0xFFFFDAD6),
                                colorIcono  = Color(0xFF93000A),
                                icono       = Icons.Default.PriorityHigh,
                                titulo      = "Nueva nota: Matemáticas",
                                descripcion = "Examen Unidad 4: Geometría disponible.",
                                tiempo      = "hace 10 min"
                            )

                            Spacer(Modifier.height(8.dp))

                            // Actividad 2
                            FilaActividad(
                                colorFondo  = BlueSecCont.copy(alpha = 0.2f),
                                colorIcono  = Color(0xFF003567),
                                icono       = Icons.Default.Chat,
                                titulo      = "Mensaje del Docente",
                                descripcion = "No olvides el permiso para la excursión.",
                                tiempo      = "hace 2 horas"
                            )
                        }
                    }

                    // Próximo Evento
                    Card(
                        modifier = Modifier.weight(1f),
                        shape    = RoundedCornerShape(16.dp),
                        colors   = CardDefaults.cardColors(containerColor = Blue800)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("Próximo Evento", fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold, color = Color.White)

                            Spacer(Modifier.height(12.dp))

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color.White.copy(alpha = 0.1f))
                                    .padding(12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("24", fontSize = 36.sp,
                                        fontWeight = FontWeight.Bold, color = Color.White)
                                    Text("OCTUBRE", fontSize = 10.sp,
                                        color = Color.White.copy(alpha = 0.8f),
                                        letterSpacing = 2.sp)
                                }
                            }

                            Spacer(Modifier.height(8.dp))

                            Text("Reunión de padres", fontSize = 12.sp,
                                fontWeight = FontWeight.Medium, color = Color.White)
                            Text("Aula 204 • 15:30", fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.8f))

                            Spacer(Modifier.height(12.dp))

                            Button(
                                onClick  = { },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(36.dp),
                                shape    = RoundedCornerShape(50),
                                colors   = ButtonDefaults.buttonColors(
                                    containerColor = Color.White
                                )
                            ) {
                                Text("Recordatorio", fontSize = 11.sp,
                                    color = Blue800, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }

                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

// ── Tarjeta del menú bento ────────────────────────────────────
@Composable
fun TarjetaMenu(
    item:     ItemMenu,
    modifier: Modifier = Modifier,
    onClick:  () -> Unit
) {
    Card(
        modifier  = modifier
            .aspectRatio(1f)
            .clickable { onClick() },
        shape     = RoundedCornerShape(16.dp),
        colors    = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Ícono decorativo de fondo grande
            Icon(
                imageVector        = item.icono,
                contentDescription = null,
                tint               = item.colorIcono.copy(alpha = 0.08f),
                modifier           = Modifier
                    .size(80.dp)
                    .align(Alignment.TopEnd)
                    .offset(x = 16.dp, y = (-8).dp)
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Bottom
            ) {
                // Ícono principal con fondo
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(item.colorFondo),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(item.icono, null,
                        tint = item.colorIcono,
                        modifier = Modifier.size(22.dp))
                }
                Spacer(Modifier.height(10.dp))
                Text(item.titulo, fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold, color = Color(0xFF191C21))
                Text(item.descripcion, fontSize = 11.sp,
                    color = Color(0xFF727783))
            }
        }
    }
}

// ── Fila de actividad reciente ────────────────────────────────
@Composable
fun FilaActividad(
    colorFondo:  Color,
    colorIcono:  Color,
    icono:       ImageVector,
    titulo:      String,
    descripcion: String,
    tiempo:      String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { }
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment     = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(colorFondo),
            contentAlignment = Alignment.Center
        ) {
            Icon(icono, null, tint = colorIcono, modifier = Modifier.size(20.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(titulo, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            Text(descripcion, fontSize = 12.sp, color = Color(0xFF727783))
            Text(tiempo, fontSize = 11.sp, color = Color(0xFF9AA0A6))
        }
    }
}

// ── Bottom Navigation Bar ─────────────────────────────────────
@Composable
fun BottomNavBar(navController: NavController) {
    val navBackStack by navController.currentBackStackEntryAsState()
    val rutaActual   = navBackStack?.destination?.route

    val items = listOf(
        Triple(AppRoutes.Home.route,     Icons.Default.Home,          "Inicio"),
        Triple(AppRoutes.Avisos.route,   Icons.Default.Notifications, "Avisos"),
        Triple(AppRoutes.Mensajes.route, Icons.Default.Chat,          "Mensajes"),
        Triple(AppRoutes.Tareas.route,   Icons.Default.Assignment,    "Tareas"),
        Triple(AppRoutes.Perfil.route,   Icons.Default.Person,        "Perfil"),
    )

    NavigationBar(containerColor = Color.White) {
        items.forEach { (ruta, icono, etiqueta) ->
            val seleccionado = rutaActual == ruta
            NavigationBarItem(
                selected = seleccionado,
                onClick  = {
                    navController.navigate(ruta) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState    = true
                    }
                },
                icon     = {
                    Icon(icono, contentDescription = etiqueta,
                        modifier = Modifier.size(24.dp))
                },
                label    = { Text(etiqueta, fontSize = 11.sp) },
                colors   = NavigationBarItemDefaults.colors(
                    selectedIconColor   = Blue900,
                    selectedTextColor   = Blue900,
                    indicatorColor      = BlueFixed,
                    unselectedIconColor = Color(0xFF727783),
                    unselectedTextColor = Color(0xFF727783)
                )
            )
        }
    }
}