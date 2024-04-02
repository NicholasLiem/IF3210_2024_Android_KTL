package com.ktl.bondoman.db

import androidx.annotation.WorkerThread
import kotlinx.coroutines.flow.Flow

class TransactionRepository(private val transactionDao: TransactionDao) {
    val allTransactions: Flow<List<Transaction>> = transactionDao.getAll()

    @WorkerThread
    suspend fun insert(transaction: Transaction) {
        transactionDao.insert(transaction)
    }

    @WorkerThread
    suspend fun delete(transaction: Transaction) {
        transactionDao.delete(transaction)
    }
}