package com.example.comunicappescolar.data.remote

import com.example.comunicappescolar.data.model.*
import retrofit2.http.*

interface ApiService {
    @POST("auth/login")    suspend fun login(@Body r: LoginRequest): LoginResponse
    @POST("auth/register") suspend fun register(@Body r: RegisterRequest): LoginResponse
    @GET("auth/me")        suspend fun getMe(): Usuario

    @GET("avisos")         suspend fun getAvisos(@Query("categoria") c: String? = null): List<Aviso>
    @GET("avisos/{id}")    suspend fun getAvisoById(@Path("id") id: Int): Aviso
    @POST("avisos")        suspend fun crearAviso(@Body a: AvisoRequest): Aviso
    @PATCH("avisos/{id}/leer") suspend fun marcarAvisoLeido(@Path("id") id: Int): Map<String, String>

    @GET("mensajes")       suspend fun getConversaciones(): List<Conversacion>
    @GET("mensajes/{id}")  suspend fun getChat(@Path("id") id: Int): List<Mensaje>
    @POST("mensajes")      suspend fun enviarMensaje(@Body m: MensajeRequest): Mensaje

    @GET("tareas")         suspend fun getTareas(): List<Tarea>
    @POST("tareas")        suspend fun crearTarea(@Body t: TareaRequest): Tarea
    @PATCH("tareas/{id}/estado") suspend fun actualizarEstado(@Path("id") id: Int, @Body e: EstadoRequest): Map<String, String>

    @GET("calificaciones") suspend fun getCalificaciones(@Query("periodo") p: Int? = null): List<Calificacion>
    @POST("calificaciones") suspend fun registrarCalificacion(@Body r: com.example.comunicappescolar.ui.screens.calificaciones.RegistrarNotaRequest): Calificacion

    @GET("asistencia")     suspend fun getAsistencia(): List<Asistencia>
    @GET("usuarios")       suspend fun getUsuarios(): List<Usuario>
    @PUT("usuarios/{id}")  suspend fun actualizarPerfil(@Path("id") id: Int, @Body d: PerfilRequest): Usuario

    @POST("asistencia")
    suspend fun registrarAsistencia(
        @Body request: RegistrarAsistenciaRequest
    ): Asistencia

    @GET("eventos")
    suspend fun getEventos(): List<Evento>

    @POST("eventos")
    suspend fun crearEvento(@Body request: CrearEventoRequest): Evento
}
