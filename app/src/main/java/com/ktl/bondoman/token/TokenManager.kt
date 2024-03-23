package com.ktl.bondoman.token

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.google.gson.Gson
import java.util.Base64

class TokenManager(context: Context) {
    private val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
    private val sharedPreferences = EncryptedSharedPreferences.create(
        "encryptedPreferences",
        masterKeyAlias,
        context.applicationContext,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun getSharedPreferences(): SharedPreferences {
        return this.sharedPreferences
    }
    fun saveTokenRaw(token: String) {
        with(sharedPreferences.edit()) {
            putString("token", token)
            apply()
        }

        parseJwt(token)?.let { saveToken(it) }
    }

    fun loadToken(): Token? {
        return if (sharedPreferences.contains("nim")) {
            Token(
                nim = sharedPreferences.getString("nim", null),
                iat = sharedPreferences.getLong("iat", 0),
                exp = sharedPreferences.getLong("exp", 0)
            )
        } else {
            null
        }
    }
    private fun parseJwt(token: String): Token? {
        val parts = token.split(".")
        if (parts.size == 3) {
            val payload = parts[1]
            val decodedBytes = Base64.getUrlDecoder().decode(payload)
            val payloadStr = String(decodedBytes)
            val payloadMap = parsePayloadToJsonMap(payloadStr)

            return Token(
                nim = payloadMap["nim"] as? String,
                iat = (payloadMap["iat"] as? Number)?.toLong(),
                exp = (payloadMap["exp"] as? Number)?.toLong()
            )
        }
        return null
    }

    fun clearToken() {
        with(sharedPreferences.edit()) {
            remove("nim")
            remove("iat")
            remove("exp")
            apply()
        }
    }

    private fun parsePayloadToJsonMap(payload: String): Map<String, Any> {
        return Gson().fromJson(payload, Map::class.java) as Map<String, Any>
    }

    private fun saveToken(token: Token) {
        with(sharedPreferences.edit()) {
            putString("nim", token.nim)
            putLong("iat", token.iat ?: 0L)
            putLong("exp", token.exp ?: 0L)
            apply()
        }
    }
}