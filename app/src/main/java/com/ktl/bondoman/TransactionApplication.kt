package com.ktl.bondoman;

import android.app.Application;
import android.util.Log
import com.ktl.bondoman.db.TransactionDatabase
import com.ktl.bondoman.db.TransactionRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class TransactionApplication : Application() {
        val database by lazy { TransactionDatabase.getDatabase(this) }
        val repository by lazy { TransactionRepository(database.transactionDao()) }
}