package com.example.personalfinancetracker

import android.app.DatePickerDialog
import android.os.Bundle
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
import java.util.UUID

class AddTransactionActivity : AppCompatActivity() {

    private lateinit var categorySpinner: Spinner
    private lateinit var transactionTypeGroup: RadioGroup
    private lateinit var transactionManager: TransactionManager
    private var selectedDate: Date = Date()
    private val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_transaction)

        // Initialize transaction manager
        transactionManager = TransactionManager(this)

        // Initialize views
        categorySpinner = findViewById(R.id.categorySpinner)
        transactionTypeGroup = findViewById(R.id.transactionTypeGroup)

        // Initialize date display with current date
        val dateDisplay = findViewById<TextView>(R.id.dateDisplay)
        dateDisplay.text = dateFormat.format(selectedDate)

        // Set up back button
        val backButton = findViewById<ImageButton>(R.id.backButton)
        backButton.setOnClickListener {
            finish()
        }

        // Set up category spinner with expense categories by default
        setupCategorySpinner(true)

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
                saveTransaction()
                finish()
            }
        }
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

    private fun saveTransaction() {
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

        // Create and save the transaction
        val transaction = Transaction(
            id = UUID.randomUUID().toString(),
            title = title,
            amount = amount,
            category = category,
            date = selectedDate,
            isExpense = isExpense
        )

        transactionManager.addTransaction(transaction)
        Toast.makeText(this, "Transaction saved", Toast.LENGTH_SHORT).show()
    }
}