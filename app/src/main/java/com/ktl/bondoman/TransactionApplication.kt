package com.ktl.bondoman;

import android.app.Application;
import android.util.Log
import com.ktl.bondoman.db.TransactionDatabase
import com.ktl.bondoman.repository.TransactionRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class TransactionApplication : Application() {
        lateinit var applicationScope: CoroutineScope
        lateinit var database: TransactionDatabase
        lateinit var repository: TransactionRepository

        override fun onCreate() {
                super.onCreate()
                Log.w("TransactionApplication", "onCreate Transaction App")
                applicationScope = CoroutineScope(SupervisorJob())
                database = TransactionDatabase.getDatabase(this, applicationScope)
                repository = TransactionRepository(database.transactionDao())
        }
}