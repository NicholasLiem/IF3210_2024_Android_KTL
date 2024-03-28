package com.ktl.bondoman

import android.app.Application
import com.ktl.bondoman.db.TransactionDatabase
import com.ktl.bondoman.db.TransactionRepository

class TransactionApplication : Application() {
        val database by lazy { TransactionDatabase.getDatabase(this) }
        val repository by lazy { TransactionRepository(database.transactionDao()) }
}