package com.example.personalfinancetracker.util

import android.content.Context
import com.example.personalfinancetracker.model.Transaction
import com.example.personalfinancetracker.model.TransactionCategory
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class BackupManager(private val context: Context) {

    private val transactionManager = TransactionManager(context)
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    // Create backup of all transactions
    fun createBackup(): Boolean {
        try {
            val transactions = transactionManager.getAllTransactions()
            val jsonArray = convertTransactionsToJson(transactions)

            // Create backup file
            val backupFile = getBackupFile()
            val fileOutputStream = FileOutputStream(backupFile)
            fileOutputStream.write(jsonArray.toString().toByteArray())
            fileOutputStream.close()

            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    // Restore transactions from backup
    fun restoreFromBackup(): Boolean {
        try {
            val backupFile = getBackupFile()
            if (!backupFile.exists()) {
                return false
            }

            val fileInputStream = FileInputStream(backupFile)
            val size = fileInputStream.available()
            val buffer = ByteArray(size)
            fileInputStream.read(buffer)
            fileInputStream.close()

            val jsonString = String(buffer)
            val jsonArray = JSONArray(jsonString)
            val transactions = parseTransactionsFromJson(jsonArray)

            // Clear existing transactions and add restored ones
            transactionManager.clearAllTransactions()
            for (transaction in transactions) {
                transactionManager.addTransaction(transaction)
            }

            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    // Get backup file
    private fun getBackupFile(): File {
        val backupDir = File(context.filesDir, "backups")
        if (!backupDir.exists()) {
            backupDir.mkdir()
        }
        return File(backupDir, "transactions_backup.json")
    }

    // Convert transactions to JSON
    private fun convertTransactionsToJson(transactions: List<Transaction>): JSONArray {
        val jsonArray = JSONArray()

        for (transaction in transactions) {
            val jsonObject = JSONObject().apply {
                put("id", transaction.id)
                put("title", transaction.title)
                put("amount", transaction.amount)
                put("category", transaction.category.name)
                put("date", dateFormat.format(transaction.date))
                put("isExpense", transaction.isExpense)
            }

            jsonArray.put(jsonObject)
        }

        return jsonArray
    }

    // Parse transactions from JSON
    private fun parseTransactionsFromJson(jsonArray: JSONArray): List<Transaction> {
        val transactions = mutableListOf<Transaction>()

        for (i in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(i)

            try {
                val id = jsonObject.getString("id")
                val title = jsonObject.getString("title")
                val amount = jsonObject.getDouble("amount")
                val categoryName = jsonObject.getString("category")
                val dateStr = jsonObject.getString("date")
                val isExpense = jsonObject.getBoolean("isExpense")

                val category = try {
                    TransactionCategory.valueOf(categoryName)
                } catch (e: Exception) {
                    TransactionCategory.OTHERS
                }

                val date = try {
                    dateFormat.parse(dateStr) ?: Date()
                } catch (e: Exception) {
                    Date()
                }

                transactions.add(
                    Transaction(id, title, amount, category, date, isExpense)
                )
            } catch (e: Exception) {
                e.printStackTrace()
                // Skip this transaction if there's an error
            }
        }

        return transactions
    }
}