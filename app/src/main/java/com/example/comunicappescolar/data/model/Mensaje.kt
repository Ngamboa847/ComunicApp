package com.example.comunicappescolar.data.model

data class Mensaje(
    val id: Int,
    val remitente_id: Int,
    val receptor_id: Int,
    val contenido: String,
    val leido: Boolean,
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
data class MensajeRequest(val receptor_id: Int, val contenido: String)
