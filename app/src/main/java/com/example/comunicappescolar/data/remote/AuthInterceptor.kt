package com.example.comunicappescolar.data.remote

import okhttp3.Interceptor
import okhttp3.Response

object AuthInterceptor : Interceptor {
    var token: String? = null
    override fun intercept(chain: Interceptor.Chain): Response {
        val req = chain.request().newBuilder().apply {
            token?.let { addHeader("Authorization", "Bearer $it") }
        }.build()
        return chain.proceed(req)
    }
}
