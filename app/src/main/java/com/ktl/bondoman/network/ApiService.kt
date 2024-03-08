package com.ktl.bondoman.network

import com.ktl.bondoman.network.models.CheckTokenExpirationResponse
import com.ktl.bondoman.network.models.LoginResponse
import com.ktl.bondoman.network.requests.LoginRequest
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiService {
    @POST("/api/auth/login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    @POST("/api/auth/token")
    fun checkTokenExpiration(@Header("Authorization") token: String): Call<CheckTokenExpirationResponse>

    @POST("/api/bill/upload")
    fun uploadBill(@Header("Authorization") token: String, @Body request: MultipartBody.Part): Call<ResponseBody>
}
