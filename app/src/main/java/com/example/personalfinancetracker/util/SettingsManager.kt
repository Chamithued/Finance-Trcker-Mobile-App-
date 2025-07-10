package com.example.personalfinancetracker.util

import android.content.Context
import android.content.SharedPreferences

class SettingsManager(context: Context) {

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        PREFS_NAME, Context.MODE_PRIVATE
    )

    // Currency settings
    fun setCurrency(currencyCode: String) {
        sharedPreferences.edit().putString(KEY_CURRENCY, currencyCode).apply()
    }

    fun getCurrency(): String {
        return sharedPreferences.getString(KEY_CURRENCY, DEFAULT_CURRENCY) ?: DEFAULT_CURRENCY
    }

    // Notification settings
    fun setBudgetAlertEnabled(enabled: Boolean) {
        sharedPreferences.edit().putBoolean(KEY_BUDGET_ALERT, enabled).apply()
    }

    fun isBudgetAlertEnabled(): Boolean {
        return sharedPreferences.getBoolean(KEY_BUDGET_ALERT, true)
    }

    fun setDailyReminderEnabled(enabled: Boolean) {
        sharedPreferences.edit().putBoolean(KEY_DAILY_REMINDER, enabled).apply()
    }

    fun isDailyReminderEnabled(): Boolean {
        return sharedPreferences.getBoolean(KEY_DAILY_REMINDER, false)
    }

    companion object {
        private const val PREFS_NAME = "FinanceTrackerPrefs"
        private const val KEY_CURRENCY = "currency"
        private const val KEY_BUDGET_ALERT = "budget_alert"
        private const val KEY_DAILY_REMINDER = "daily_reminder"
        private const val DEFAULT_CURRENCY = "USD"
    }
}