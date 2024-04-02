package com.ktl.bondoman.ui.graph

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.ktl.bondoman.db.Transaction
import com.ktl.bondoman.db.TransactionRepository
import kotlinx.coroutines.launch

class GraphViewModel(private val repository: TransactionRepository) : ViewModel() {

    val allTransactions: LiveData<List<Transaction>> = repository.allTransactions.asLiveData()

}