package com.example.personalfinancetracker

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.personalfinancetracker.model.Transaction
import com.example.personalfinancetracker.util.CategoryUtil
import com.example.personalfinancetracker.util.TransactionManager
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class TransactionDetailActivity : AppCompatActivity() {

    private lateinit var transactionManager: TransactionManager
    private lateinit var transaction: Transaction
    private lateinit var categorySpinner: Spinner
    private lateinit var transactionTypeGroup: RadioGroup
    private var selectedDate: Date = Date()
    private val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_transaction) // Reuse add transaction layout

        //setSupportActionBar(findViewById(R.id.toolbar))

        val deleteButton = findViewById<ImageButton>(R.id.deleteButton)
        deleteButton.visibility = View.VISIBLE // Show only in edit mode
        deleteButton.setOnClickListener {
            showDeleteConfirmation()
        }

        // Initialize transaction manager
        transactionManager = TransactionManager(this)

        // Get transaction ID from intent
        val transactionId = intent.getStringExtra("transaction_id") ?: ""
        if (transactionId.isEmpty()) {
            finish()
            return
        }

        // Find the transaction
        val transactions = transactionManager.getAllTransactions()
        transaction = transactions.find { it.id == transactionId } ?: run {
            finish()
            return
        }

        // Initialize views
        val titleInput = findViewById<EditText>(R.id.titleInput)
        val amountInput = findViewById<EditText>(R.id.amountInput)
        val dateDisplay = findViewById<TextView>(R.id.dateDisplay)
        categorySpinner = findViewById(R.id.categorySpinner)
        transactionTypeGroup = findViewById(R.id.transactionTypeGroup)

        // Fill in transaction details
        titleInput.setText(transaction.title)
        amountInput.setText(transaction.amount.toString())
        selectedDate = transaction.date
        dateDisplay.text = dateFormat.format(selectedDate)

        // Set transaction type
        val radioButton = if (transaction.isExpense) R.id.expenseRadio else R.id.incomeRadio
        transactionTypeGroup.check(radioButton)

        // Set up category spinner
        setupCategorySpinner(transaction.isExpense)

        // Select correct category
        val categories = if (transaction.isExpense) {
            CategoryUtil.getAllExpenseCategories()
        } else {
            CategoryUtil.getAllIncomeCategories()
        }
        val categoryNames = categories.map { it.displayName }
        val position = categoryNames.indexOf(transaction.category.displayName)
        if (position != -1) {
            categorySpinner.setSelection(position)
        }

        // Change title to indicate editing
        val titleTextView = findViewById<TextView>(R.id.titleTextView) // You'll need to add this to layout
        titleTextView?.text = "Edit Transaction"

        // Set up back button
        val backButton = findViewById<ImageButton>(R.id.backButton)
        backButton.setOnClickListener {
            finish()
        }

        // Set up transaction type change listener
        transactionTypeGroup.setOnCheckedChangeListener { _, checkedId ->
            val isExpense = checkedId == R.id.expenseRadio
            setupCategorySpinner(isExpense)
        }

        // Set up date selection
        val selectDateButton = findViewById<Button>(R.id.selectDateButton)
        selectDateButton.setOnClickListener {
            showDatePicker()
        }

        // Set up save button
        val saveButton = findViewById<Button>(R.id.saveTransactionButton)
        saveButton.setOnClickListener {
            if (validateInputs()) {
                updateTransaction()
                finish()
            }
        }
    }

//    override fun onCreateOptionsMenu(menu: Menu): Boolean {
//        menuInflater.inflate(R.menu.transaction_detail_menu, menu)
//        return true
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        return when (item.itemId) {
//            R.id.action_delete -> {
//                showDeleteConfirmation()
//                true
//            }
//            else -> super.onOptionsItemSelected(item)
//        }
//    }

    private fun showDeleteConfirmation() {
        AlertDialog.Builder(this)
            .setTitle("Delete Transaction")
            .setMessage("Are you sure you want to delete this transaction?")
            .setPositiveButton("Delete") { _, _ ->
                transactionManager.deleteTransaction(transaction.id)
                Toast.makeText(this, "Transaction deleted", Toast.LENGTH_SHORT).show()
                finish()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun setupCategorySpinner(isExpense: Boolean) {
        val categories = if (isExpense) {
            CategoryUtil.getAllExpenseCategories()
        } else {
            CategoryUtil.getAllIncomeCategories()
        }

        val categoryNames = categories.map { it.displayName }

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            categoryNames
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        categorySpinner.adapter = adapter
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        calendar.time = selectedDate

        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                calendar.set(selectedYear, selectedMonth, selectedDay)
                selectedDate = calendar.time
                findViewById<TextView>(R.id.dateDisplay).text = dateFormat.format(selectedDate)
            },
            year, month, day
        )

        datePickerDialog.show()
    }

    private fun validateInputs(): Boolean {
        val titleInput = findViewById<EditText>(R.id.titleInput)
        val amountInput = findViewById<EditText>(R.id.amountInput)

        if (titleInput.text.isNullOrBlank()) {
            titleInput.error = "Title is required"
            return false
        }

        if (amountInput.text.isNullOrBlank()) {
            amountInput.error = "Amount is required"
            return false
        }

        try {
            val amount = amountInput.text.toString().toDouble()
            if (amount <= 0) {
                amountInput.error = "Amount must be greater than zero"
                return false
            }
        } catch (e: NumberFormatException) {
            amountInput.error = "Invalid amount"
            return false
        }

        return true
    }

    private fun updateTransaction() {
        val titleInput = findViewById<EditText>(R.id.titleInput)
        val amountInput = findViewById<EditText>(R.id.amountInput)

        val title = titleInput.text.toString()
        val amount = amountInput.text.toString().toDouble()
        val isExpense = transactionTypeGroup.checkedRadioButtonId == R.id.expenseRadio

        // Get the selected category
        val categories = if (isExpense) {
            CategoryUtil.getAllExpenseCategories()
        } else {
            CategoryUtil.getAllIncomeCategories()
        }
        val selectedCategoryName = categorySpinner.selectedItem.toString()
        val category = categories.find { it.displayName == selectedCategoryName } ?: categories.first()

        // Create updated transaction
        val updatedTransaction = Transaction(
            id = transaction.id, // Keep the same ID
            title = title,
            amount = amount,
            category = category,
            date = selectedDate,
            isExpense = isExpense
        )

        transactionManager.updateTransaction(updatedTransaction)
        Toast.makeText(this, "Transaction updated", Toast.LENGTH_SHORT).show()
    }
}