package com.ktl.bondoman.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Entity(tableName = "_transactions")
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
    val location: String? = "Lat: -6.8733093, Lon: 107.6050709",

    @ColumnInfo(name = "date")
    val date: Long = System.currentTimeMillis()
) {
    companion object {
        @JvmStatic
        @TypeConverter
        fun getDateString(millis: Long): String {
            val formatter = SimpleDateFormat("EEEE, d MMMM yyyy, HH:mm", Locale.getDefault())
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = millis
            return formatter.format(calendar.time)
        }
    }
}