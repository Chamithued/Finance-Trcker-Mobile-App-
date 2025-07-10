package com.example.personalfinancetracker.util

import android.content.Context
import android.content.SharedPreferences
import com.example.personalfinancetracker.model.TransactionCategory
import org.json.JSONObject

class BudgetManager(context: Context) {

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        PREFS_NAME, Context.MODE_PRIVATE
    )

    // Set overall monthly budget
    fun setOverallBudget(amount: Double) {
        sharedPreferences.edit().putFloat(KEY_OVERALL_BUDGET, amount.toFloat()).apply()
    }

    // Get overall monthly budget
    fun getOverallBudget(): Double {
        return sharedPreferences.getFloat(KEY_OVERALL_BUDGET, 1000f).toDouble()
    }

    // Set category budget
    fun setCategoryBudget(category: TransactionCategory, amount: Double) {
        val categoryBudgets = getCategoryBudgets().toMutableMap()
        categoryBudgets[category.name] = amount
        saveCategoryBudgets(categoryBudgets)
    }

    // Get all category budgets
    fun getCategoryBudgets(): Map<String, Double> {
        val budgetsJson = sharedPreferences.getString(KEY_CATEGORY_BUDGETS, null)
            ?: return getDefaultCategoryBudgets()

        return try {
            val jsonObject = JSONObject(budgetsJson)
            val budgets = mutableMapOf<String, Double>()

            val keys = jsonObject.keys()
            while (keys.hasNext()) {
                val key = keys.next()
                budgets[key] = jsonObject.getDouble(key)
            }

            budgets
        } catch (e: Exception) {
            e.printStackTrace()
            getDefaultCategoryBudgets()
        }
    }

    // Get budget for a specific category
    fun getBudgetForCategory(category: TransactionCategory): Double {
        return getCategoryBudgets()[category.name] ?: 0.0
    }

    // Save all category budgets
    private fun saveCategoryBudgets(budgets: Map<String, Double>) {
        val jsonObject = JSONObject()

        for ((category, amount) in budgets) {
            jsonObject.put(category, amount)
        }

        sharedPreferences.edit().putString(KEY_CATEGORY_BUDGETS, jsonObject.toString()).apply()
    }

    // Default budgets for categories
    private fun getDefaultCategoryBudgets(): Map<String, Double> {
        val budgets = mutableMapOf<String, Double>()

        // Assign default budget values for expense categories
        CategoryUtil.getAllExpenseCategories().forEach { category ->
            when (category) {
                TransactionCategory.FOOD -> budgets[category.name] = 300.0
                TransactionCategory.BILLS -> budgets[category.name] = 200.0
                TransactionCategory.TRANSPORT -> budgets[category.name] = 150.0
                TransactionCategory.ENTERTAINMENT -> budgets[category.name] = 100.0
                else -> budgets[category.name] = 50.0
            }
        }

        return budgets
    }

    companion object {
        private const val PREFS_NAME = "FinanceTrackerPrefs"
        private const val KEY_OVERALL_BUDGET = "overall_budget"
        private const val KEY_CATEGORY_BUDGETS = "category_budgets"
    }
}