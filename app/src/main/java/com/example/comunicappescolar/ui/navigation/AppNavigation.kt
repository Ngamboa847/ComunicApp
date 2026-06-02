package com.example.comunicappescolar.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.comunicappescolar.data.local.SessionDataStore
import com.example.comunicappescolar.ui.screens.asistencia.AsistenciaScreen
import com.example.comunicappescolar.ui.screens.avisos.AvisosScreen
import com.example.comunicappescolar.ui.screens.avisos.DetalleAvisoScreen
import com.example.comunicappescolar.ui.screens.calendario.CalendarioScreen
import com.example.comunicappescolar.ui.screens.calificaciones.CalificacionesScreen
import com.example.comunicappescolar.ui.screens.directorio.DirectorioScreen
import com.example.comunicappescolar.ui.screens.home.HomeScreen
import com.example.comunicappescolar.ui.screens.login.LoginScreen
import com.example.comunicappescolar.ui.screens.mensajes.ChatScreen
import com.example.comunicappescolar.ui.screens.mensajes.MensajesScreen
import com.example.comunicappescolar.ui.screens.onboarding.OnboardingScreen
import com.example.comunicappescolar.ui.screens.perfil.PerfilScreen
import com.example.comunicappescolar.ui.screens.registro.RegistroScreen
import com.example.comunicappescolar.ui.screens.splash.SplashScreen
import com.example.comunicappescolar.ui.screens.tareas.TareasScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val session = remember { SessionDataStore(context) }

    Box(modifier = Modifier.safeDrawingPadding()) {
        NavHost(
            navController = navController,
            startDestination = AppRoutes.Splash.route
        ) {
            composable(AppRoutes.Splash.route) {
                SplashScreen(navController)
            }
            composable(AppRoutes.Onboarding.route) {
                OnboardingScreen(navController)
            }
            composable(AppRoutes.Login.route) {
                LoginScreen(navController)
            }
            composable(AppRoutes.Registro.route) {
                RegistroScreen(navController)
            }
            composable(AppRoutes.Home.route) {
                HomeScreen(navController)
            }
            composable(AppRoutes.Avisos.route) {
                AvisosScreen(navController)
            }
            composable(AppRoutes.Avisos.route) {
                AvisosScreen(navController)
            }
            composable(
                route = AppRoutes.DetalleAviso.route,
                arguments = listOf(navArgument("id") { type = NavType.IntType })
            ) { backStack ->
                val id = backStack.arguments?.getInt("id") ?: 0
                DetalleAvisoScreen(navController, id)
            }
            composable(AppRoutes.Mensajes.route) {
                MensajesScreen(navController)
            }
            composable(
                route     = AppRoutes.Chat.route,
                arguments = listOf(
                    navArgument("contactoId")     { type = NavType.IntType },
                    navArgument("contactoNombre") { type = NavType.StringType }
                )
            ) { backStack ->
                val contactoId     = backStack.arguments?.getInt("contactoId") ?: 0
                val contactoNombre = backStack.arguments?.getString("contactoNombre") ?: "Contacto"
                ChatScreen(navController, contactoId, contactoNombre)
            }
            composable(AppRoutes.Tareas.route) {
                TareasScreen(navController)
            }
            composable(AppRoutes.Calificaciones.route) {
                CalificacionesScreen(navController)
            }
            composable(AppRoutes.Asistencia.route) {
                AsistenciaScreen(navController)
            }
            composable(AppRoutes.Calendario.route) {
                CalendarioScreen(navController)
            }
            composable(AppRoutes.Directorio.route) {
                DirectorioScreen(navController)
            }
            composable(AppRoutes.Perfil.route) {
                PerfilScreen(navController)
            }
            composable(AppRoutes.Avisos.route) {
                AvisosScreen(navController)
            }
            composable(
                route = AppRoutes.DetalleAviso.route,
                arguments = listOf(navArgument("id") { type = NavType.IntType })
            ) { backStack ->
                val id = backStack.arguments?.getInt("id") ?: 0
                DetalleAvisoScreen(navController, id)
            }
        }
    }
}
