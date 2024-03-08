package com.ktl.bondoman.network.models

import com.google.gson.annotations.SerializedName

data class CheckTokenExpirationResponse(
    @SerializedName("nim")
    val nim: String,

    @SerializedName("iat")
    val iat: Int,

    @SerializedName("exp")
    val exp: Int
)