package com.example.personalfinancetracker.util

import com.example.personalfinancetracker.model.Transaction
import com.example.personalfinancetracker.model.TransactionCategory
import java.util.Calendar
import java.util.UUID

object SampleDataUtil {

    fun getSampleTransactions(): List<Transaction> {
        val calendar = Calendar.getInstance()
        val currentDate = calendar.time

        // Create some sample transactions
        val transactions = mutableListOf<Transaction>()

        // Add a month's worth of transactions, going backward from today
        for (i in 0 until 30) {
            calendar.time = currentDate
            calendar.add(Calendar.DAY_OF_MONTH, -i)

            // Add an expense
            if (i % 2 == 0) {
                transactions.add(
                    Transaction(
                        id = UUID.randomUUID().toString(),
                        title = "Grocery shopping",
                        amount = 45.75 + i,
                        category = TransactionCategory.FOOD,
                        date = calendar.time,
                        isExpense = true
                    )
                )
            }

            // Add another type of expense
            if (i % 3 == 0) {
                transactions.add(
                    Transaction(
                        id = UUID.randomUUID().toString(),
                        title = "Movie tickets",
                        amount = 25.00 + (i/2),
                        category = TransactionCategory.ENTERTAINMENT,
                        date = calendar.time,
                        isExpense = true
                    )
                )
            }

            // Add a bill every 15 days
            if (i % 15 == 0) {
                transactions.add(
                    Transaction(
                        id = UUID.randomUUID().toString(),
                        title = "Electricity bill",
                        amount = 85.50,
                        category = TransactionCategory.BILLS,
                        date = calendar.time,
                        isExpense = true
                    )
                )
            }

            // Add income at the beginning of the month
            calendar.get(Calendar.DAY_OF_MONTH).let { dayOfMonth ->
                if (dayOfMonth == 1) {
                    transactions.add(
                        Transaction(
                            id = UUID.randomUUID().toString(),
                            title = "Monthly salary",
                            amount = 3200.00,
                            category = TransactionCategory.SALARY,
                            date = calendar.time,
                            isExpense = false
                        )
                    )
                }
            }
        }

        return transactions
    }
}