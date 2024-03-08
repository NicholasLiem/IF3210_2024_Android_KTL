package com.ktl.bondoman.network

import com.ktl.bondoman.network.models.LoginResponse
import com.ktl.bondoman.network.requests.LoginRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("/api/auth/login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>
}
