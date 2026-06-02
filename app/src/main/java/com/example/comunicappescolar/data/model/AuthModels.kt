package com.example.comunicappescolar.data.model

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
data class PerfilRequest(val nombre: String, val telefono: String)
