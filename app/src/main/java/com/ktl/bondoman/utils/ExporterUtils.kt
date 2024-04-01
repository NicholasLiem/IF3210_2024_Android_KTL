package com.ktl.bondoman.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.core.content.FileProvider
import com.ktl.bondoman.db.Transaction
import com.ktl.bondoman.db.TransactionDao
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


object ExporterUtils {

    suspend fun exportTransactions(context: Context, transactionDao: TransactionDao, fileType: String, email : Boolean = false) {
        Log.w("ExporterUtils", "Exporting transactions")

        val transactions = transactionDao.getAllList();

        Log.w("ExporterUtils", "Checkpoint 2 Exporting transactions value ${transactions}")
            val wb: Workbook = if (fileType == "xlsx") XSSFWorkbook() else HSSFWorkbook()
            val sheet: Sheet = wb.createSheet("Transaction Data")

            // Create header row
            val headerRow: Row = sheet.createRow(0)
            headerRow.createCell(0).setCellValue("ID")
            headerRow.createCell(1).setCellValue("User")
            headerRow.createCell(2).setCellValue("Title")
            headerRow.createCell(3).setCellValue("Category")
            headerRow.createCell(4).setCellValue("Amount")
            headerRow.createCell(5).setCellValue("Location")
            headerRow.createCell(6).setCellValue("Date")

            // Populate data rows
            var rowNum = 1
            for (transaction in transactions) {
                val row: Row = sheet.createRow(rowNum++)
                row.createCell(0).setCellValue(transaction.id.toDouble())
                row.createCell(1).setCellValue(transaction.nim)
                row.createCell(2).setCellValue(transaction.title)
                row.createCell(3).setCellValue(transaction.category)
                row.createCell(4).setCellValue(transaction.amount)
                row.createCell(5).setCellValue(transaction.location ?: "")
                row.createCell(6).setCellValue(Transaction.getDateString(transaction.date))
            }

            Log.w("ExporterUtils", "Exporting transactions Okay")

            // Write the workbook to a file
            try {
                    Log.w("ExporterUtils", "Exporting transactions download")
                    val fileDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath()
                    val dateTimeString = System.currentTimeMillis()
                    val file = File(fileDir, "transaction_data_${dateTimeString}.${if (fileType == "xlsx") "xlsx" else "xls"}")
                    val fos = FileOutputStream(file)
                    wb.write(fos)
                    fos.close()
                if (email) {
                    val contentUri = FileProvider.getUriForFile(context, "com.ktl.bondoman.fileprovider", file)
                    shareFileByEmail(context, contentUri)
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
private fun shareFileByEmail(context: Context, contentUri: Uri) {
    try {
        val uri = contentUri
        Log.w("EMAIL", "URI: $uri")
        val emailIntent = Intent(Intent.ACTION_SEND)
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Transaction Data")
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Please find attached the transaction data file.")
        emailIntent.putExtra(Intent.EXTRA_STREAM, uri)
        emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        emailIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        emailIntent.type = "application/vnd.ms-excel"

        emailIntent.setPackage("com.google.android.gm")

        context.startActivity(emailIntent)
    } catch (e: Exception) {
        e.printStackTrace()
    }
    }

}