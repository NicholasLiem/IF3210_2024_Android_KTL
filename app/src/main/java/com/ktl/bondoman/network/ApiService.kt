package com.ktl.bondoman.network

import com.ktl.bondoman.network.reponses.BillUploadResponse
import com.ktl.bondoman.network.reponses.CheckTokenExpirationResponse
import com.ktl.bondoman.network.reponses.LoginResponse
import com.ktl.bondoman.network.requests.LoginRequest
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {
    @POST("/api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("/api/auth/token")
    suspend fun checkTokenExpiration(@Header("Authorization") token: String): Response<CheckTokenExpirationResponse>

    @Multipart
    @POST("/api/bill/upload")
    suspend fun uploadBill(@Header("Authorization") token: String, @Part file: MultipartBody.Part): Response<BillUploadResponse>
}
