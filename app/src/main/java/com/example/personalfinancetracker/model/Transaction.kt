package com.example.personalfinancetracker.model

import java.util.Date

data class Transaction(
    val id: String,
    val title: String,
    val amount: Double,
    val category: TransactionCategory,
    val date: Date,
    val isExpense: Boolean
)
