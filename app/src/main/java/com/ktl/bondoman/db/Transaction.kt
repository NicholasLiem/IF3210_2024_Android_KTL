package com.ktl.bondoman.db

import androidx.annotation.Nullable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transaction")
data class Transaction(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,

    @ColumnInfo(name = "user")
    val nim: String,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "category")
    val category: String, // "Pemasukan" or "Pengeluaran"

    @ColumnInfo(name = "amount")
    val amount: Double,

    @ColumnInfo(name = "location")
    val location: String? = null,

    @ColumnInfo(name = "date")
    val date: Long = System.currentTimeMillis()
)