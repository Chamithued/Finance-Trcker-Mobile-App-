package com.example.personalfinancetracker.util

import com.example.personalfinancetracker.model.TransactionCategory

object CategoryUtil {

    fun getAllExpenseCategories(): List<TransactionCategory> {
        return listOf(
            TransactionCategory.FOOD,
            TransactionCategory.TRANSPORT,
            TransactionCategory.BILLS,
            TransactionCategory.SHOPPING,
            TransactionCategory.ENTERTAINMENT,
            TransactionCategory.HEALTH,
            TransactionCategory.EDUCATION,
            TransactionCategory.HOUSING,
            TransactionCategory.TRAVEL,
            TransactionCategory.OTHERS
        )
    }

    fun getAllIncomeCategories(): List<TransactionCategory> {
        return listOf(
            TransactionCategory.SALARY,
            TransactionCategory.BUSINESS,
            TransactionCategory.INVESTMENT,
            TransactionCategory.GIFT,
            TransactionCategory.OTHERS
        )
    }

    fun getCategoryIcon(category: TransactionCategory): Int {
        // We'll return drawable resource IDs here
        // For now we'll return a placeholder
        return android.R.drawable.ic_menu_help
    }
}