package com.example.comunicappescolar.ui.navigation

sealed class AppRoutes(val route: String) {
    object Splash         : AppRoutes("splash")
    object Onboarding     : AppRoutes("onboarding")
    object Login          : AppRoutes("login")
    object Registro       : AppRoutes("registro")
    object Home           : AppRoutes("home")
    object Avisos         : AppRoutes("avisos")
    object Mensajes       : AppRoutes("mensajes")
    object Tareas         : AppRoutes("tareas")
    object Calificaciones : AppRoutes("calificaciones")
    object Asistencia     : AppRoutes("asistencia")
    object Calendario     : AppRoutes("calendario")
    object Directorio     : AppRoutes("directorio")
    object Perfil         : AppRoutes("perfil")
    object DetalleAviso   : AppRoutes("aviso/{id}") { fun createRoute(id: Int) = "aviso/$id" }
    object Chat : AppRoutes("chat/{contactoId}/{contactoNombre}") {
        fun createRoute(id: Int, nombre: String) = "chat/$id/$nombre"
    }
}
