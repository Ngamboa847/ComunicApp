# ============================================================
# ComunicApp Escolar - Script de creacion de estructura
# Ejecutar desde la raiz del proyecto Android Studio
# ============================================================

$base = "app\src\main\java\com\example\comunicappescolar"

# Crear carpetas
$carpetas = @(
    "$base\data\local",
    "$base\data\model",
    "$base\data\remote",
    "$base\data\repository",
    "$base\ui\theme",
    "$base\ui\navigation",
    "$base\ui\components",
    "$base\ui\screens\splash",
    "$base\ui\screens\onboarding",
    "$base\ui\screens\login",
    "$base\ui\screens\registro",
    "$base\ui\screens\home",
    "$base\ui\screens\avisos",
    "$base\ui\screens\mensajes",
    "$base\ui\screens\tareas",
    "$base\ui\screens\calificaciones",
    "$base\ui\screens\asistencia",
    "$base\ui\screens\calendario",
    "$base\ui\screens\directorio",
    "$base\ui\screens\perfil"
)

foreach ($carpeta in $carpetas) {
    New-Item -ItemType Directory -Force -Path $carpeta | Out-Null
    Write-Host "OK Carpeta: $carpeta"
}

# Funcion para crear archivos
function Crear-Archivo($ruta, $contenido) {
    New-Item -ItemType File -Force -Path $ruta | Out-Null
    Set-Content -Path $ruta -Value $contenido -Encoding UTF8
    Write-Host "Archivo: $ruta"
}

# ── DATA / MODEL ─────────────────────────────────────────────

Crear-Archivo "$base\data\model\AuthModels.kt" "package com.example.comunicappescolar.data.model

data class LoginRequest(val email: String, val password: String)
data class RegisterRequest(val nombre: String, val email: String, val password: String, val rol: String)
data class LoginResponse(val token: String, val usuario: Usuario)
data class Usuario(
    val id: Int,
    val nombre: String,
    val email: String,
    val rol: String,
    val telefono: String?,
    val grado: String?,
    val colegio: String?,
    val avatar_url: String?
)
data class PerfilRequest(val nombre: String, val telefono: String)"

Crear-Archivo "$base\data\model\Aviso.kt" "package com.example.comunicappescolar.data.model

data class Aviso(
    val id: Int,
    val titulo: String,
    val descripcion: String?,
    val contenido: String?,
    val categoria: String,
    val autor_nombre: String?,
    val fecha: String,
    val leido: Int = 0
)
data class AvisoRequest(val titulo: String, val descripcion: String, val contenido: String, val categoria: String)"

Crear-Archivo "$base\data\model\Mensaje.kt" "package com.example.comunicappescolar.data.model

data class Mensaje(
    val id: Int,
    val remitente_id: Int,
    val receptor_id: Int,
    val contenido: String,
    val leido: Int,
    val created_at: String,
    val remitente_nombre: String?
)
data class Conversacion(
    val contacto_id: Int,
    val contacto_nombre: String,
    val contacto_rol: String,
    val contacto_avatar: String?,
    val ultimo_mensaje: String?,
    val no_leidos: Int
)
data class MensajeRequest(val receptor_id: Int, val contenido: String)"

Crear-Archivo "$base\data\model\Tarea.kt" "package com.example.comunicappescolar.data.model

data class Tarea(
    val id: Int,
    val titulo: String,
    val descripcion: String?,
    val materia: String?,
    val docente_nombre: String?,
    val grado: String?,
    val fecha_limite: String?,
    val estado_usuario: String = `"pendiente`"
)
data class TareaRequest(val titulo: String, val descripcion: String, val materia: String, val grado: String, val fecha_limite: String)
data class EstadoRequest(val estado: String)"

Crear-Archivo "$base\data\model\Calificacion.kt" "package com.example.comunicappescolar.data.model

data class Calificacion(
    val id: Int,
    val materia: String,
    val nota: Double,
    val periodo: Int,
    val docente_nombre: String?
)"

Crear-Archivo "$base\data\model\Asistencia.kt" "package com.example.comunicappescolar.data.model

data class Asistencia(
    val id: Int,
    val fecha: String,
    val estado: String
)"

Crear-Archivo "$base\data\model\Evento.kt" "package com.example.comunicappescolar.data.model

data class Evento(
    val id: Int,
    val titulo: String,
    val descripcion: String?,
    val fecha: String,
    val lugar: String?,
    val tipo: String
)"

Crear-Archivo "$base\data\model\Docente.kt" "package com.example.comunicappescolar.data.model

data class Docente(
    val id: Int,
    val nombre: String,
    val email: String,
    val grado: String?,
    val telefono: String?,
    val avatar_url: String?
)"

# ── DATA / REMOTE ─────────────────────────────────────────────

Crear-Archivo "$base\data\remote\AuthInterceptor.kt" "package com.example.comunicappescolar.data.remote

import okhttp3.Interceptor
import okhttp3.Response

object AuthInterceptor : Interceptor {
    var token: String? = null
    override fun intercept(chain: Interceptor.Chain): Response {
        val req = chain.request().newBuilder().apply {
            token?.let { addHeader(`"Authorization`", `"Bearer `$it`") }
        }.build()
        return chain.proceed(req)
    }
}"

Crear-Archivo "$base\data\remote\RetrofitClient.kt" "package com.example.comunicappescolar.data.remote

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL =
        `"https://comunicapp-api-c4drhef2eug3f8cs.centralus-01.azurewebsites.net/api/v1/`"

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val httpClient = OkHttpClient.Builder()
        .addInterceptor(logging)
        .addInterceptor(AuthInterceptor)
        .build()

    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}"

Crear-Archivo "$base\data\remote\ApiService.kt" "package com.example.comunicappescolar.data.remote

import com.example.comunicappescolar.data.model.*
import retrofit2.http.*

interface ApiService {
    @POST(`"auth/login`")    suspend fun login(@Body r: LoginRequest): LoginResponse
    @POST(`"auth/register`") suspend fun register(@Body r: RegisterRequest): LoginResponse
    @GET(`"auth/me`")        suspend fun getMe(): Usuario

    @GET(`"avisos`")         suspend fun getAvisos(@Query(`"categoria`") c: String? = null): List<Aviso>
    @GET(`"avisos/{id}`")    suspend fun getAvisoById(@Path(`"id`") id: Int): Aviso
    @POST(`"avisos`")        suspend fun crearAviso(@Body a: AvisoRequest): Aviso
    @PATCH(`"avisos/{id}/leer`") suspend fun marcarAvisoLeido(@Path(`"id`") id: Int): Map<String, String>

    @GET(`"mensajes`")       suspend fun getConversaciones(): List<Conversacion>
    @GET(`"mensajes/{id}`")  suspend fun getChat(@Path(`"id`") id: Int): List<Mensaje>
    @POST(`"mensajes`")      suspend fun enviarMensaje(@Body m: MensajeRequest): Mensaje

    @GET(`"tareas`")         suspend fun getTareas(): List<Tarea>
    @POST(`"tareas`")        suspend fun crearTarea(@Body t: TareaRequest): Tarea
    @PATCH(`"tareas/{id}/estado`") suspend fun actualizarEstado(@Path(`"id`") id: Int, @Body e: EstadoRequest): Map<String, String>

    @GET(`"calificaciones`") suspend fun getCalificaciones(@Query(`"periodo`") p: Int? = null): List<Calificacion>
    @GET(`"asistencia`")     suspend fun getAsistencia(): List<Asistencia>
    @GET(`"eventos`")        suspend fun getEventos(): List<Evento>
    @GET(`"usuarios`")       suspend fun getUsuarios(): List<Usuario>
    @PUT(`"usuarios/{id}`")  suspend fun actualizarPerfil(@Path(`"id`") id: Int, @Body d: PerfilRequest): Usuario
}"

# ── DATA / REPOSITORY ─────────────────────────────────────────

$repos = @("AuthRepository","AvisoRepository","MensajeRepository","TareaRepository","CalificacionRepository","AsistenciaRepository","EventoRepository")
foreach ($r in $repos) {
    Crear-Archivo "$base\data\repository\$r.kt" "package com.example.comunicappescolar.data.repository

import com.example.comunicappescolar.data.remote.RetrofitClient

class $r(private val api = RetrofitClient.apiService) {
    // TODO: implementar metodos
}"
}

# ── DATA / LOCAL ──────────────────────────────────────────────

Crear-Archivo "$base\data\local\SessionDataStore.kt" "package com.example.comunicappescolar.data.local

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import com.example.comunicappescolar.data.model.Usuario

val Context.dataStore by preferencesDataStore(name = `"session`")

class SessionDataStore(private val context: Context) {
    companion object {
        val KEY_TOKEN    = stringPreferencesKey(`"token`")
        val KEY_ID       = stringPreferencesKey(`"id`")
        val KEY_NOMBRE   = stringPreferencesKey(`"nombre`")
        val KEY_EMAIL    = stringPreferencesKey(`"email`")
        val KEY_ROL      = stringPreferencesKey(`"rol`")
        val KEY_GRADO    = stringPreferencesKey(`"grado`")
        val KEY_TELEFONO = stringPreferencesKey(`"telefono`")
        val KEY_COLEGIO  = stringPreferencesKey(`"colegio`")
        val KEY_AVATAR   = stringPreferencesKey(`"avatar`")
    }

    suspend fun guardarSesion(token: String, usuario: Usuario) {
        context.dataStore.edit {
            it[KEY_TOKEN]    = token
            it[KEY_ID]       = usuario.id.toString()
            it[KEY_NOMBRE]   = usuario.nombre
            it[KEY_EMAIL]    = usuario.email
            it[KEY_ROL]      = usuario.rol
            it[KEY_GRADO]    = usuario.grado ?: `"`"
            it[KEY_TELEFONO] = usuario.telefono ?: `"`"
            it[KEY_COLEGIO]  = usuario.colegio ?: `"`"
            it[KEY_AVATAR]   = usuario.avatar_url ?: `"`"
        }
    }

    val token:  Flow<String?> = context.dataStore.data.map { it[KEY_TOKEN] }
    val nombre: Flow<String?> = context.dataStore.data.map { it[KEY_NOMBRE] }
    val rol:    Flow<String?> = context.dataStore.data.map { it[KEY_ROL] }

    suspend fun cerrarSesion() = context.dataStore.edit { it.clear() }
}"

# ── UI / THEME ────────────────────────────────────────────────

Crear-Archivo "$base\ui\theme\Color.kt" "package com.example.comunicappescolar.ui.theme

import androidx.compose.ui.graphics.Color

val Blue900      = Color(0xFF0D47A1)
val Blue800      = Color(0xFF1565C0)
val Blue700      = Color(0xFF1976D2)
val Blue100      = Color(0xFFBBDEFB)
val Blue50       = Color(0xFFE3F2FD)
val White        = Color(0xFFFFFFFF)
val Gray100      = Color(0xFFF5F5F5)
val Gray600      = Color(0xFF757575)
val ColorError   = Color(0xFFC62828)
val ColorSuccess = Color(0xFF2E7D32)
val ColorWarning = Color(0xFFF57F17)"

Crear-Archivo "$base\ui\theme\Type.kt" "package com.example.comunicappescolar.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val Typography = Typography(
    headlineLarge = TextStyle(fontWeight = FontWeight.Bold, fontSize = 28.sp),
    headlineMedium = TextStyle(fontWeight = FontWeight.Bold, fontSize = 22.sp),
    titleLarge = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 18.sp),
    bodyLarge = TextStyle(fontWeight = FontWeight.Normal, fontSize = 14.sp),
    labelMedium = TextStyle(fontWeight = FontWeight.Medium, fontSize = 12.sp)
)"

Crear-Archivo "$base\ui\theme\Theme.kt" "package com.example.comunicappescolar.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary          = Blue800,
    onPrimary        = White,
    primaryContainer = Blue50,
    secondary        = Blue700,
    background       = Blue50,
    surface          = White,
    error            = ColorError
)

@Composable
fun ComunicAppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColors,
        typography  = Typography,
        content     = content
    )
}"

# ── UI / NAVIGATION ───────────────────────────────────────────

Crear-Archivo "$base\ui\navigation\AppRoutes.kt" "package com.example.comunicappescolar.ui.navigation

sealed class AppRoutes(val route: String) {
    object Splash         : AppRoutes(`"splash`")
    object Onboarding     : AppRoutes(`"onboarding`")
    object Login          : AppRoutes(`"login`")
    object Registro       : AppRoutes(`"registro`")
    object Home           : AppRoutes(`"home`")
    object Avisos         : AppRoutes(`"avisos`")
    object Mensajes       : AppRoutes(`"mensajes`")
    object Tareas         : AppRoutes(`"tareas`")
    object Calificaciones : AppRoutes(`"calificaciones`")
    object Asistencia     : AppRoutes(`"asistencia`")
    object Calendario     : AppRoutes(`"calendario`")
    object Directorio     : AppRoutes(`"directorio`")
    object Perfil         : AppRoutes(`"perfil`")
    object DetalleAviso   : AppRoutes(`"aviso/{id}`") { fun createRoute(id: Int) = `"aviso/`$id`" }
    object Chat           : AppRoutes(`"chat/{contactoId}`") { fun createRoute(id: Int) = `"chat/`$id`" }
}"

Crear-Archivo "$base\ui\navigation\AppNavigation.kt" "package com.example.comunicappescolar.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.comunicappescolar.data.local.SessionDataStore

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val session = remember { SessionDataStore(context) }

    NavHost(navController = navController, startDestination = AppRoutes.Splash.route) {
        composable(AppRoutes.Splash.route)         { }
        composable(AppRoutes.Onboarding.route)     { }
        composable(AppRoutes.Login.route)          { }
        composable(AppRoutes.Registro.route)       { }
        composable(AppRoutes.Home.route)           { }
        composable(AppRoutes.Avisos.route)         { }
        composable(AppRoutes.Mensajes.route)       { }
        composable(AppRoutes.Tareas.route)         { }
        composable(AppRoutes.Calificaciones.route) { }
        composable(AppRoutes.Asistencia.route)     { }
        composable(AppRoutes.Calendario.route)     { }
        composable(AppRoutes.Directorio.route)     { }
        composable(AppRoutes.Perfil.route)         { }
    }
}"

# ── UI / COMPONENTS ───────────────────────────────────────────

$components = @("TopBar","BottomNavBar","LoadingView","ErrorView","EmptyStateView")
foreach ($c in $components) {
    Crear-Archivo "$base\ui\components\$c.kt" "package com.example.comunicappescolar.ui.components

// TODO: $c composable"
}

# ── UI / SCREENS ──────────────────────────────────────────────

$screens = @(
    @{ folder="splash";         files=@("SplashScreen") },
    @{ folder="onboarding";     files=@("OnboardingScreen") },
    @{ folder="login";          files=@("LoginScreen","LoginViewModel","LoginUiState") },
    @{ folder="registro";       files=@("RegistroScreen","RegistroViewModel") },
    @{ folder="home";           files=@("HomeScreen","HomeViewModel") },
    @{ folder="avisos";         files=@("AvisosScreen","AvisosViewModel","DetalleAvisoScreen") },
    @{ folder="mensajes";       files=@("MensajesScreen","MensajesViewModel","ChatScreen") },
    @{ folder="tareas";         files=@("TareasScreen","TareasViewModel") },
    @{ folder="calificaciones"; files=@("CalificacionesScreen","CalificacionesViewModel") },
    @{ folder="asistencia";     files=@("AsistenciaScreen","AsistenciaViewModel") },
    @{ folder="calendario";     files=@("CalendarioScreen","CalendarioViewModel") },
    @{ folder="directorio";     files=@("DirectorioScreen","DirectorioViewModel") },
    @{ folder="perfil";         files=@("PerfilScreen","PerfilViewModel") }
)

foreach ($s in $screens) {
    foreach ($f in $s.files) {
        Crear-Archivo "$base\ui\screens\$($s.folder)\$f.kt" "package com.example.comunicappescolar.ui.screens.$($s.folder)

// TODO: $f"
    }
}

# ── MAINACTIVITY ──────────────────────────────────────────────

Crear-Archivo "$base\MainActivity.kt" "package com.example.comunicappescolar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.comunicappescolar.ui.navigation.AppNavigation
import com.example.comunicappescolar.ui.theme.ComunicAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComunicAppTheme {
                AppNavigation()
            }
        }
    }
}"

Write-Host ""
Write-Host "============================================"
Write-Host "Estructura creada exitosamente"
Write-Host "Carpetas: $($carpetas.Count)"
Write-Host "Sincroniza el proyecto en Android Studio"
Write-Host "============================================"