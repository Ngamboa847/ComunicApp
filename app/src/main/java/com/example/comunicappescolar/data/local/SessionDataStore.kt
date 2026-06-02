package com.example.comunicappescolar.data.local

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import com.example.comunicappescolar.data.model.Usuario

val Context.dataStore by preferencesDataStore(name = "session")

class SessionDataStore(private val context: Context) {
    companion object {
        val KEY_TOKEN    = stringPreferencesKey("token")
        val KEY_ID       = stringPreferencesKey("id")
        val KEY_NOMBRE   = stringPreferencesKey("nombre")
        val KEY_EMAIL    = stringPreferencesKey("email")
        val KEY_ROL      = stringPreferencesKey("rol")
        val KEY_GRADO    = stringPreferencesKey("grado")
        val KEY_TELEFONO = stringPreferencesKey("telefono")
        val KEY_COLEGIO  = stringPreferencesKey("colegio")
        val KEY_AVATAR   = stringPreferencesKey("avatar")

    }

    suspend fun guardarSesion(token: String, usuario: Usuario) {
        context.dataStore.edit {
            it[KEY_TOKEN]    = token
            it[KEY_ID]       = usuario.id.toString()
            it[KEY_NOMBRE]   = usuario.nombre
            it[KEY_EMAIL]    = usuario.email
            it[KEY_ROL]      = usuario.rol
            it[KEY_GRADO]    = usuario.grado ?: ""
            it[KEY_TELEFONO] = usuario.telefono ?: ""
            it[KEY_COLEGIO]  = usuario.colegio ?: ""
            it[KEY_AVATAR]   = usuario.avatar_url ?: ""
        }
    }

    val token:  Flow<String?> = context.dataStore.data.map { it[KEY_TOKEN] }
    val nombre: Flow<String?> = context.dataStore.data.map { it[KEY_NOMBRE] }
    val rol:    Flow<String?> = context.dataStore.data.map { it[KEY_ROL] }
    val id:     Flow<String?> = context.dataStore.data.map { it[KEY_ID] }

    suspend fun cerrarSesion() = context.dataStore.edit { it.clear() }
}
