package com.example.personalfinancetracker.util

import android.content.Context
import java.text.NumberFormat
import java.util.Currency

object CurrencyFormatter {

    fun getFormattedCurrency(amount: Double, context: Context): String {
        val settingsManager = SettingsManager(context)
        return getFormattedCurrency(amount, settingsManager.getCurrency())
    }

    fun getFormattedCurrency(amount: Double, currencyCode: String): String {
        val currencyFormat = NumberFormat.getCurrencyInstance()
        currencyFormat.currency = Currency.getInstance(currencyCode)
        return currencyFormat.format(amount)
    }

    fun getCurrencyInstance(context: Context): NumberFormat {
        val settingsManager = SettingsManager(context)
        val currencyFormat = NumberFormat.getCurrencyInstance()
        currencyFormat.currency = Currency.getInstance(settingsManager.getCurrency())
        return currencyFormat
    }
}