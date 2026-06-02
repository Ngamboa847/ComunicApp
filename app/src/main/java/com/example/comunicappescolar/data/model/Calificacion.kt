package com.example.comunicappescolar.data.model

data class Calificacion(
    val id: Int,
    val materia: String,
    val nota: Double,
    val periodo: Int,
    val docente_nombre: String?
)
