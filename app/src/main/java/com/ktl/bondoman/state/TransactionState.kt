package com.ktl.bondoman.state

import com.ktl.bondoman.db.Transaction

data class TransactionState (
    val transactions: List<Transaction> = emptyList(),
    val nim: String = "",
    val title: String = "",
    val category: String = "",
    val amount: Double = 0.0,
    val location: String? = null,
    val isDialogShown: Boolean = false
)