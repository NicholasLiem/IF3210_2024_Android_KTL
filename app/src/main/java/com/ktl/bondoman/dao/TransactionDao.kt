package com.ktl.bondoman.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ktl.bondoman.db.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    // The flow always holds/caches latest version of data. Notifies its observers when the
    // data has changed.
    @Query("SELECT * FROM [transaction] ORDER BY date ASC")
    fun getAlphabetizedWords(): Flow<List<Transaction>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(transaction: Transaction)

    @Query("DELETE FROM [transaction]")
    suspend fun deleteAll()

}