package com.example.comunicappescolar.data.model

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
data class AvisoRequest(val titulo: String, val descripcion: String, val contenido: String, val categoria: String)
