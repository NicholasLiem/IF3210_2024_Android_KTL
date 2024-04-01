package com.ktl.bondoman.services

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.ktl.bondoman.network.ApiClient
import com.ktl.bondoman.token.TokenManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.Timer
import kotlin.concurrent.timerTask

class JwtCheckService : Service() {
    private lateinit var tokenManager: TokenManager
    private val serviceScope = CoroutineScope(Job() + Dispatchers.IO)
    private lateinit var timer: Timer

    companion object {
        private const val LOG_TAG = "JwtCheckService"
        private const val CHECK_INTERVAL = 1 * 60 * 1000L
        fun startJwtCheckService(context: Context) {
            val serviceIntent = Intent(context, JwtCheckService::class.java)
            context.startService(serviceIntent)
            val prefs = context.getSharedPreferences("ServicePrefs", Context.MODE_PRIVATE)
            prefs.edit().putBoolean("isServiceRunning", true).apply()
        }

        fun stopJwtCheckService(context: Context) {
            val serviceIntent = Intent(context, JwtCheckService::class.java)
            context.stopService(serviceIntent)
            val prefs = context.getSharedPreferences("ServicePrefs", Context.MODE_PRIVATE)
            prefs.edit().putBoolean("isServiceRunning", false).apply()
        }

        fun isServiceRunning(context: Context): Boolean {
            val prefs = context.getSharedPreferences("ServicePrefs", Context.MODE_PRIVATE)
            return prefs.getBoolean("isServiceRunning", false)
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        tokenManager = TokenManager(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.i(LOG_TAG, "Starting JWT check service")
        checkJwtPeriodically()
        return START_STICKY
    }

    private fun checkJwtPeriodically() {
        Log.d(LOG_TAG, "Scheduling JWT checks")
        timer = Timer()
        timer.schedule(timerTask {
            serviceScope.launch {
                checkJwt()
            }
        }, 0, CHECK_INTERVAL)
    }

    private suspend fun checkJwt() {
        try {
            Log.i(LOG_TAG, "Checking JWT validity")
            val tokenStr = tokenManager.getTokenStr()
            if (tokenStr == null) {
                Log.w(LOG_TAG, "Token is null, cannot check validity")
                return
            }
            val response = ApiClient.apiService.checkTokenExpiration("Bearer $tokenStr")

            if (!response.isSuccessful || response.code() == 401) {
                Log.i(LOG_TAG, "JWT is expired or invalid, clearing token and notifying")
                tokenManager.clearToken()
                tokenManager.setTokenExpired()
            }

        } catch (e: Exception) {
            Log.e(LOG_TAG, "Error checking JWT validity: ${e.message}")
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        timer.cancel()
    }
}