package com.example.personalfinancetracker

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.personalfinancetracker.util.BackupManager
import com.example.personalfinancetracker.util.SettingsManager

class SettingsActivity : AppCompatActivity() {

    private lateinit var settingsManager: SettingsManager
    private lateinit var backupManager: BackupManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        // Initialize settings manager
        settingsManager = SettingsManager(this)
        // In onCreate after initializing settingsManager
        backupManager = BackupManager(this)


        // Set up back button
        val backButton = findViewById<ImageButton>(R.id.backButton)
        backButton.setOnClickListener {
            finish()
        }

        // Set up currency spinner
        val currencySpinner = findViewById<Spinner>(R.id.currencySpinner)
        val currencies = arrayOf("USD", "EUR", "GBP", "JPY", "CAD", "AUD", "LKR") // Added LKR
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, currencies)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        currencySpinner.adapter = adapter

        // Set the current selection
        val currentCurrency = settingsManager.getCurrency()
        val currencyIndex = currencies.indexOf(currentCurrency)
        if (currencyIndex != -1) {
            currencySpinner.setSelection(currencyIndex)
        }

        // Save currency when spinner selection changes
        currencySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedCurrency = currencies[position]
                if (selectedCurrency != currentCurrency) {
                    settingsManager.setCurrency(selectedCurrency)
                    Toast.makeText(
                        this@SettingsActivity,
                        "Currency changed to $selectedCurrency",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Do nothing
            }
        }

        // Set up notification switches
        val budgetAlertSwitch = findViewById<Switch>(R.id.budgetAlertSwitch)
        budgetAlertSwitch.isChecked = settingsManager.isBudgetAlertEnabled()

        val dailyReminderSwitch = findViewById<Switch>(R.id.dailyReminderSwitch)
        dailyReminderSwitch.isChecked = settingsManager.isDailyReminderEnabled()

        // Handle switch changes
        budgetAlertSwitch.setOnCheckedChangeListener { _, isChecked ->
            settingsManager.setBudgetAlertEnabled(isChecked)
            Toast.makeText(
                this,
                if (isChecked) "Budget alerts enabled" else "Budget alerts disabled",
                Toast.LENGTH_SHORT
            ).show()
        }

        dailyReminderSwitch.setOnCheckedChangeListener { _, isChecked ->
            settingsManager.setDailyReminderEnabled(isChecked)
            Toast.makeText(
                this,
                if (isChecked) "Daily reminders enabled" else "Daily reminders disabled",
                Toast.LENGTH_SHORT
            ).show()
        }

// Set up backup button
        findViewById<Button>(R.id.backupDataButton).setOnClickListener {
            if (backupManager.createBackup()) {
                Toast.makeText(this, "Backup created successfully", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Failed to create backup", Toast.LENGTH_SHORT).show()
            }
        }

// Set up restore button
        findViewById<Button>(R.id.restoreDataButton).setOnClickListener {
            // Show confirmation dialog
            AlertDialog.Builder(this)
                .setTitle("Restore Data")
                .setMessage("This will replace all current data with the backup. Continue?")
                .setPositiveButton("Restore") { _, _ ->
                    if (backupManager.restoreFromBackup()) {
                        Toast.makeText(this, "Data restored successfully", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "No backup found or restore failed", Toast.LENGTH_SHORT).show()
                    }
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }
}
