package com.example.comunicappescolar.data.model

data class Asistencia(
    val id: Int,
    val fecha: String,
    val estado: String
)


data class RegistrarAsistenciaRequest(
    val estudiante_id: Int,
    val fecha:         String,
    val estado:        String
)