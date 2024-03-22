package com.ktl.bondoman.network.reponses

import com.google.gson.annotations.SerializedName

data class CheckTokenExpirationResponse(
    @SerializedName("nim")
    val nim: String,

    @SerializedName("iat")
    val iat: Long,

    @SerializedName("exp")
    val exp: Long
)