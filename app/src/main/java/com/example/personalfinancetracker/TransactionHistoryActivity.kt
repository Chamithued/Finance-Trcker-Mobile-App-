package com.example.personalfinancetracker

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.personalfinancetracker.adapter.TransactionAdapter
import com.example.personalfinancetracker.model.Transaction
import com.example.personalfinancetracker.util.TransactionManager

class TransactionHistoryActivity : AppCompatActivity() {

    private lateinit var adapter: TransactionAdapter
    private lateinit var transactionManager: TransactionManager
    private var allTransactions: List<Transaction> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transaction_history)

        // Initialize transaction manager
        transactionManager = TransactionManager(this)

        // Set up back button
        val backButton = findViewById<ImageButton>(R.id.backButton)
        backButton.setOnClickListener {
            finish()
        }

        // Load transactions from TransactionManager
        allTransactions = transactionManager.getAllTransactions().sortedByDescending { it.date }

        // Set up RecyclerView
        val recyclerView = findViewById<RecyclerView>(R.id.transactionsRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Create adapter with click handling
        // In TransactionHistoryActivity
        adapter = TransactionAdapter(allTransactions) { transaction ->
            // Open transaction detail screen
            val intent = Intent(this, TransactionDetailActivity::class.java)
            intent.putExtra("transaction_id", transaction.id)
            startActivity(intent)
        }

        adapter = TransactionAdapter(
            allTransactions,
            showEditIcon = true, // Show edit icons in transaction history
            onClick = { transaction ->
                val intent = Intent(this, TransactionDetailActivity::class.java)
                intent.putExtra("transaction_id", transaction.id)
                startActivity(intent)
            }
        )

//        adapter = TransactionAdapter(allTransactions) { transaction ->
//            Toast.makeText(this, "Clicked: ${transaction.title}", Toast.LENGTH_SHORT).show()
//            // Later we'll implement editing of transactions
//        }
        recyclerView.adapter = adapter

        // Set up filter buttons
        findViewById<Button>(R.id.allFilter).setOnClickListener {
            updateFilter(FilterType.ALL)
        }

        findViewById<Button>(R.id.incomeFilter).setOnClickListener {
            updateFilter(FilterType.INCOME)
        }

        findViewById<Button>(R.id.expenseFilter).setOnClickListener {
            updateFilter(FilterType.EXPENSE)
        }

        // In TransactionHistoryActivity.kt's onCreate method, after setting up the RecyclerView

// Show a hint Toast the first time the activity is opened
        val preferences = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val hintShown = preferences.getBoolean("transaction_edit_hint_shown", false)

        if (!hintShown) {
            Toast.makeText(
                this,
                "Tap on a transaction to edit or delete it",
                Toast.LENGTH_LONG
            ).show()

            // Mark hint as shown
            preferences.edit().putBoolean("transaction_edit_hint_shown", true).apply()
        }
    }

    override fun onResume() {
        super.onResume()
        // Refresh data when returning to this screen
        refreshTransactions()
    }

    private fun refreshTransactions() {
        // Reload transactions in case they've changed
        allTransactions = transactionManager.getAllTransactions().sortedByDescending { it.date }

        // Update the adapter with the current filter
        when {
            findViewById<Button>(R.id.incomeFilter).isSelected -> updateFilter(FilterType.INCOME)
            findViewById<Button>(R.id.expenseFilter).isSelected -> updateFilter(FilterType.EXPENSE)
            else -> updateFilter(FilterType.ALL)
        }
    }

    private enum class FilterType {
        ALL, INCOME, EXPENSE
    }

    private fun updateFilter(filterType: FilterType) {
        // Get the buttons
        val allButton = findViewById<Button>(R.id.allFilter)
        val incomeButton = findViewById<Button>(R.id.incomeFilter)
        val expenseButton = findViewById<Button>(R.id.expenseFilter)

        // Reset background colors for all buttons
        allButton.setBackgroundColor(resources.getColor(R.color.light_gray, null))
        incomeButton.setBackgroundColor(resources.getColor(R.color.light_gray, null))
        expenseButton.setBackgroundColor(resources.getColor(R.color.light_gray, null))

        // Highlight the selected button
        when (filterType) {
            FilterType.ALL -> allButton.setBackgroundColor(resources.getColor(R.color.primary, null))
            FilterType.INCOME -> incomeButton.setBackgroundColor(resources.getColor(R.color.primary, null))
            FilterType.EXPENSE -> expenseButton.setBackgroundColor(resources.getColor(R.color.primary, null))
        }

        // Filter transactions
        val filteredTransactions = when (filterType) {
            FilterType.ALL -> allTransactions
            FilterType.INCOME -> allTransactions.filter { !it.isExpense }
            FilterType.EXPENSE -> allTransactions.filter { it.isExpense }
        }

        // Update adapter
        adapter.updateTransactions(filteredTransactions)
    }
}