package com.ktl.bondoman.repository

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import com.ktl.bondoman.dao.TransactionDao
import com.ktl.bondoman.db.Transaction
import kotlinx.coroutines.flow.Flow

// Declares the DAO as a private property in the constructor. Pass in the DAO
// instead of the whole database, because you only need access to the DAO
class TransactionRepository(private val transactionDao: TransactionDao) {

    // Room executes all queries on a separate thread.
    // Observed Flow will notify the observer when the data has changed.
    val allTransactions: Flow<List<Transaction>> = transactionDao.getAlphabetizedWords()

    // By default Room runs suspend queries off the main thread, therefore, we don't need to
    // implement anything else to ensure we're not doing long running database work
    // off the main thread.
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(transaction: Transaction) {
        transactionDao.insert(transaction)
    }
}