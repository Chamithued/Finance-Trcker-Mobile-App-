package com.example.personalfinancetracker.util

import android.content.Context
import android.content.SharedPreferences
import com.example.personalfinancetracker.model.Transaction
import com.example.personalfinancetracker.model.TransactionCategory
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TransactionManager(context: Context) {

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        PREFS_NAME, Context.MODE_PRIVATE
    )

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    // Add a new transaction
    fun addTransaction(transaction: Transaction) {
        val transactions = getAllTransactions().toMutableList()
        transactions.add(transaction)
        saveTransactions(transactions)
    }

    // Get all transactions
    fun getAllTransactions(): List<Transaction> {
        val transactionsJson = sharedPreferences.getString(KEY_TRANSACTIONS, null) ?: return emptyList()

        return try {
            val jsonArray = JSONArray(transactionsJson)
            val transactions = mutableListOf<Transaction>()

            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                transactions.add(parseTransaction(jsonObject))
            }

            transactions
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    // Delete a transaction by ID
    fun deleteTransaction(transactionId: String) {
        val transactions = getAllTransactions().toMutableList()
        transactions.removeAll { it.id == transactionId }
        saveTransactions(transactions)
    }

    // Update an existing transaction
    fun updateTransaction(updatedTransaction: Transaction) {
        val transactions = getAllTransactions().toMutableList()
        val index = transactions.indexOfFirst { it.id == updatedTransaction.id }

        if (index != -1) {
            transactions[index] = updatedTransaction
            saveTransactions(transactions)
        }
    }

    // Save the list of transactions to SharedPreferences
    private fun saveTransactions(transactions: List<Transaction>) {
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

        sharedPreferences.edit().putString(KEY_TRANSACTIONS, jsonArray.toString()).apply()
    }

    // Parse a JSON object into a Transaction object
    private fun parseTransaction(jsonObject: JSONObject): Transaction {
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

        return Transaction(id, title, amount, category, date, isExpense)
    }

    // Initialize with sample data if no transactions exist
    fun initWithSampleDataIfEmpty() {
        if (getAllTransactions().isEmpty()) {
            saveTransactions(SampleDataUtil.getSampleTransactions())
        }
    }
    fun clearAllTransactions() {
        sharedPreferences.edit().putString(KEY_TRANSACTIONS, "[]").apply()
    }

    companion object {
        private const val PREFS_NAME = "FinanceTrackerPrefs"
        private const val KEY_TRANSACTIONS = "transactions"
    }
}