package com.example.comunicappescolar.data.repository


import com.example.comunicappescolar.data.model.LoginRequest
import com.example.comunicappescolar.data.model.LoginResponse
import com.example.comunicappescolar.data.remote.ApiService
import com.example.comunicappescolar.data.remote.RetrofitClient

class AuthRepository(private val api: ApiService = RetrofitClient.apiService) {

    suspend fun login(email: String, password: String): LoginResponse {
        return api.login(LoginRequest(email.trim(), password))
    }
}