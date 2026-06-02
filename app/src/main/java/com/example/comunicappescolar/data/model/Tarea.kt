package com.example.comunicappescolar.data.model

data class Tarea(
    val id: Int,
    val titulo: String,
    val descripcion: String?,
    val materia: String?,
    val docente_nombre: String?,
    val grado: String?,
    val fecha_limite: String?,
    val estado_usuario: String = "pendiente"
)
data class TareaRequest(val titulo: String, val descripcion: String, val materia: String, val grado: String, val fecha_limite: String)
data class EstadoRequest(val estado: String)
