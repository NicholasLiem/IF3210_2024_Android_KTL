package com.ktl.bondoman.network.requests

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

data class BillUploadRequest(
    val filePath: String
) {
    fun toMultipartBodyPart(): MultipartBody.Part {
        val file = File(filePath)
        val requestBody = file.asRequestBody("image/png".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData("file", file.toString(), requestBody)
    }
}
