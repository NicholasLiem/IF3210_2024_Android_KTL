package com.ktl.bondoman.workers

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.ktl.bondoman.token.TokenManager
import java.util.concurrent.TimeUnit

class TokenExpiryWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val tokenManager = TokenManager(applicationContext)
        val token = tokenManager.loadToken()

        token?.exp?.let { exp ->

            val expMillis = exp * 1000

            if (System.currentTimeMillis() > expMillis - 4 * 1000 * 60) {
                tokenManager.clearToken()

                val sharedPreferences = applicationContext.getSharedPreferences("appPreferences", Context.MODE_PRIVATE)
                with(sharedPreferences.edit()) {
                    putBoolean("tokenExpired", true)
                    apply()
                }
            }
        }

        rescheduleWork()
        return Result.success()
    }

    private fun rescheduleWork() {
        val workRequest = OneTimeWorkRequestBuilder<TokenExpiryWorker>()
            .setInitialDelay(2, TimeUnit.MINUTES)
            .build()

        WorkManager.getInstance(applicationContext).enqueueUniqueWork(
            "TokenExpiryCheck",
            ExistingWorkPolicy.REPLACE,
            workRequest
        )
    }
}
