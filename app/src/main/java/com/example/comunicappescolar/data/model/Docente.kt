package com.example.comunicappescolar.data.model

data class Docente(
    val id: Int,
    val nombre: String,
    val email: String,
    val grado: String?,
    val telefono: String?,
    val avatar_url: String?
)
