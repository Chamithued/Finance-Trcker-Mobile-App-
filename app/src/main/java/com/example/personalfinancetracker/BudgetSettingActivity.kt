package com.example.personalfinancetracker

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.personalfinancetracker.util.BudgetManager

class BudgetSettingActivity : AppCompatActivity() {

    private lateinit var budgetManager: BudgetManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_budget_setting)

        // Initialize budget manager
        budgetManager = BudgetManager(this)

        // Set up back button
        val backButton = findViewById<ImageButton>(R.id.backButton)
        backButton.setOnClickListener {
            finish()
        }

        // Set up overall budget input
        val overallBudgetInput = findViewById<EditText>(R.id.overallBudget)
        overallBudgetInput.setText(budgetManager.getOverallBudget().toString())

        // Set up save button
        val saveButton = findViewById<Button>(R.id.saveBudgetButton)
        saveButton.setOnClickListener {
            // Save overall budget
            val overallBudgetText = overallBudgetInput.text.toString()
            if (overallBudgetText.isNotEmpty()) {
                try {
                    val overallBudget = overallBudgetText.toDouble()
                    if (overallBudget <= 0) {
                        overallBudgetInput.error = "Budget must be greater than zero"
                        return@setOnClickListener
                    }

                    // Save the budget
                    budgetManager.setOverallBudget(overallBudget)

                    // For category budgets, you would need a RecyclerView and adapter
                    // This would be implemented if you have a CategoryBudgetAdapter

                    Toast.makeText(this, "Budget settings saved", Toast.LENGTH_SHORT).show()
                    finish()
                } catch (e: NumberFormatException) {
                    overallBudgetInput.error = "Invalid budget amount"
                }
            } else {
                overallBudgetInput.error = "Budget amount is required"
            }
        }

        // If you have a RecyclerView for category budgets, set it up here
        /* Example code for category budgets RecyclerView:
        val recyclerView = findViewById<RecyclerView>(R.id.categoryBudgetsRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Get all expense categories and their budgets
        val categoryBudgets = CategoryUtil.getAllExpenseCategories().map { category ->
            CategoryBudget(
                category = category,
                budget = budgetManager.getBudgetForCategory(category)
            )
        }

        // Create and set adapter
        adapter = CategoryBudgetAdapter(categoryBudgets) { category, amount ->
            // This would be called when a category budget is changed
            budgetManager.setCategoryBudget(category, amount)
        }
        recyclerView.adapter = adapter
        */
    }
}