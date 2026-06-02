package com.example.comunicappescolar.data.model


data class Evento(
    val id:          Int,
    val titulo:      String,
    val descripcion: String?,
    val fecha:       String,
    val hora_inicio: String?,
    val hora_fin:    String?,
    val lugar:       String?,
    val tipo:        String,   // "academico" | "deportivo" | "reunion" | "cultural"
    val grado:       String?,
    val autor_id:    Int?
)

data class CrearEventoRequest(
    val titulo:      String,
    val descripcion: String,
    val fecha:       String,
    val hora_inicio: String,
    val hora_fin:    String,
    val lugar:       String,
    val tipo:        String,
    val grado:       String
)