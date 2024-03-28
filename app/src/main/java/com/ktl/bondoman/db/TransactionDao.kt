package com.ktl.bondoman.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Query("SELECT * FROM _transactions ORDER BY date DESC")
    fun getAll(): Flow<List<Transaction>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(transaction: Transaction)
    
    @Update
    suspend fun update(transaction: Transaction)

    @Delete
    suspend fun delete(transaction: Transaction)
    @Query("DELETE FROM _transactions")
    suspend fun deleteAll()

    // getAll return List
    @Query("SELECT * FROM _transactions ORDER BY date DESC")
    suspend fun getAllList(): List<Transaction>

}