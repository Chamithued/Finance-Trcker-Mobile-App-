package com.example.personalfinancetracker

import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.personalfinancetracker.adapter.CategorySummary
import com.example.personalfinancetracker.adapter.CategorySummaryAdapter
import com.example.personalfinancetracker.model.Transaction
import com.example.personalfinancetracker.util.CategoryUtil
import com.example.personalfinancetracker.util.TransactionManager
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AnalyticsActivity : AppCompatActivity() {

    private lateinit var currentMonthText: TextView
    private lateinit var adapter: CategorySummaryAdapter
    private lateinit var transactionManager: TransactionManager
    private val calendar = Calendar.getInstance()
    private val monthFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_analytics)

        // Initialize transaction manager
        transactionManager = TransactionManager(this)

        // Set up back button
        val backButton = findViewById<ImageButton>(R.id.backButton)
        backButton.setOnClickListener {
            finish()
        }

        // Set up month navigation
        currentMonthText = findViewById(R.id.currentMonth)
        updateCurrentMonthText()

        findViewById<ImageButton>(R.id.prevMonth).setOnClickListener {
            calendar.add(Calendar.MONTH, -1)
            updateCurrentMonthText()
            updateCategorySummaries() // ✅ Already using this method
        }

        findViewById<ImageButton>(R.id.nextMonth).setOnClickListener {
            calendar.add(Calendar.MONTH, 1)
            updateCurrentMonthText()
            updateCategorySummaries() // ✅ Already using this method
        }

        // Set up RecyclerView
        val recyclerView = findViewById<RecyclerView>(R.id.categorySummaryRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Initialize adapter with empty list
        adapter = CategorySummaryAdapter(emptyList())
        recyclerView.adapter = adapter

        // Load data
        updateCategorySummaries() // ✅ Calling the updated method
    }

    private fun updateCurrentMonthText() {
        currentMonthText.text = monthFormat.format(calendar.time)
    }

    private fun updateCategorySummaries() {
        // ✅ Get transactions for current month
        val transactions = getTransactionsForCurrentMonth()
        val expenseTransactions = transactions.filter { it.isExpense }

        // ✅ Calculate total expenses
        val totalExpenses = expenseTransactions.sumOf { it.amount }

        val categorySummaries = mutableListOf<CategorySummary>()

        // ✅ Group transactions by category
        val groupedExpenses = expenseTransactions.groupBy { it.category }

        // ✅ Build summaries with percentage
        CategoryUtil.getAllExpenseCategories().forEach { category ->
            val categoryExpenses = groupedExpenses[category]?.sumOf { it.amount } ?: 0.0

            if (categoryExpenses > 0) {
                val percentage = if (totalExpenses > 0)
                    ((categoryExpenses / totalExpenses) * 100).toInt()
                else 0

                categorySummaries.add(
                    CategorySummary(
                        category = category,
                        amount = categoryExpenses, // Formatting happens inside adapter
                        percentage = percentage
                    )
                )
            }
        }

        // ✅ Sort by amount
        val sortedSummaries = categorySummaries.sortedByDescending { it.amount }

        // ✅ Use CurrencyFormatter inside adapter if needed (not directly here)
        adapter.updateCategorySummaries(sortedSummaries)
    }

    private fun getTransactionsForCurrentMonth(): List<Transaction> {
        val allTransactions = transactionManager.getAllTransactions()

        val monthStart = calendar.clone() as Calendar
        monthStart.set(Calendar.DAY_OF_MONTH, 1)
        monthStart.set(Calendar.HOUR_OF_DAY, 0)
        monthStart.set(Calendar.MINUTE, 0)
        monthStart.set(Calendar.SECOND, 0)

        val monthEnd = calendar.clone() as Calendar
        monthEnd.set(Calendar.DAY_OF_MONTH, monthEnd.getActualMaximum(Calendar.DAY_OF_MONTH))
        monthEnd.set(Calendar.HOUR_OF_DAY, 23)
        monthEnd.set(Calendar.MINUTE, 59)
        monthEnd.set(Calendar.SECOND, 59)

        return allTransactions.filter { transaction ->
            val transactionDate = Calendar.getInstance()
            transactionDate.time = transaction.date
            transactionDate.after(monthStart) && transactionDate.before(monthEnd)
        }
    }
}
