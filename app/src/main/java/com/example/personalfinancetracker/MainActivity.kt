package com.example.personalfinancetracker

//adapter
import android.content.Intent
import android.os.Bundle
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.personalfinancetracker.adapter.TransactionAdapter
import com.example.personalfinancetracker.util.BudgetManager
import com.example.personalfinancetracker.util.CurrencyFormatter
import com.example.personalfinancetracker.util.NotificationHelper
import com.example.personalfinancetracker.util.SettingsManager
import com.example.personalfinancetracker.util.TransactionManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private lateinit var transactionManager: TransactionManager
    private lateinit var budgetManager: BudgetManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize managers
        transactionManager = TransactionManager(this)
        budgetManager = BudgetManager(this)

        // Initialize with sample data if first run
        //transactionManager.initWithSampleDataIfEmpty()

        // In onCreate method, add this for Monthly Budget click
        val monthlyBudgetTitle = findViewById<TextView>(R.id.monthlyBudgetTitle)
        monthlyBudgetTitle.setOnClickListener {
            val intent = Intent(this, BudgetSettingActivity::class.java)
            startActivity(intent)
        }

        // Set up Floating Action Button for adding transactions
        val addTransactionButton = findViewById<FloatingActionButton>(R.id.addTransactionButton)
        addTransactionButton.setOnClickListener {
            val intent = Intent(this, AddTransactionActivity::class.java)
            startActivity(intent)
        }

        // Set up "View All" transactions link
        val viewAllTransactions = findViewById<TextView>(R.id.viewAllTransactions)
        viewAllTransactions.setOnClickListener {
            val intent = Intent(this, TransactionHistoryActivity::class.java)
            startActivity(intent)
        }

        // Set up bottom navigation
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigation.setOnItemSelectedListener { item ->
            when(item.itemId) {
                R.id.nav_home -> {
                    // Already on home screen
                    true
                }
                R.id.nav_transactions -> {
                    val intent = Intent(this, TransactionHistoryActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.nav_analytics -> {
                    val intent = Intent(this, AnalyticsActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.nav_settings -> {
                    val intent = Intent(this, SettingsActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }

        // Initial data load
        refreshData()
    }

    // Add to MainActivity class
    override fun onResume() {
        super.onResume()

        // Refresh transactions and update the UI
        refreshData()
    }

    private fun refreshData() {
        // Get all transactions
        val allTransactions = transactionManager.getAllTransactions()

        // Calculate totals
        val totalIncome = allTransactions.filter { !it.isExpense }.sumOf { it.amount }
        val totalExpense = allTransactions.filter { it.isExpense }.sumOf { it.amount }
        val balance = totalIncome - totalExpense

        // Get currency formatter
        val currencyFormat = CurrencyFormatter.getCurrencyInstance(this)

        // Update text views
        findViewById<TextView>(R.id.balanceAmount).text = currencyFormat.format(balance)
        findViewById<TextView>(R.id.incomeAmount).text = currencyFormat.format(totalIncome)
        findViewById<TextView>(R.id.expenseAmount).text = currencyFormat.format(totalExpense)

        // Update budget progress
        val overallBudget = budgetManager.getOverallBudget()
        val budgetProgress = findViewById<ProgressBar>(R.id.budgetProgress)
        val budgetStatus = findViewById<TextView>(R.id.budgetStatus)

        // Calculate budget usage percentage
        val budgetPercentage = if (overallBudget > 0) {
            ((totalExpense / overallBudget) * 100).toInt().coerceIn(0, 100)
        } else {
            0
        }
// Check if budget percentage warrants a notification
        if (budgetPercentage >= 75) { // Notify at 75% or higher
            val settingsManager = SettingsManager(this)
            if (settingsManager.isBudgetAlertEnabled()) {
                val notificationHelper = NotificationHelper(this)
                notificationHelper.showBudgetWarningNotification(budgetPercentage, overallBudget)
            }
        }

        budgetProgress.progress = budgetPercentage
        budgetStatus.text = "$budgetPercentage% of ${currencyFormat.format(overallBudget)} used"

        // Get recent transactions for display
        val recentTransactions = allTransactions.sortedByDescending { it.date }.take(5)

        // Update adapter
        val recyclerView = findViewById<RecyclerView>(R.id.recentTransactionsRecyclerView)

        if (recyclerView.adapter == null) {
            // Initialize adapter if it doesn't exist
            val adapter = TransactionAdapter(
                recentTransactions,
                showEditIcon = false, // Don't show edit icons in dashboard
                onClick = { transaction ->
                    val intent = Intent(this, TransactionDetailActivity::class.java)
                    intent.putExtra("transaction_id", transaction.id)
                    startActivity(intent)
                }
            )
            recyclerView.layoutManager = LinearLayoutManager(this)
            recyclerView.adapter = adapter
        } else {
            // Update existing adapter
            val adapter = recyclerView.adapter as TransactionAdapter
            adapter.updateTransactions(recentTransactions)
        }
    }
}