package com.example.personalfinancetracker.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.personalfinancetracker.R
import com.example.personalfinancetracker.model.Transaction
import com.example.personalfinancetracker.util.CategoryUtil
import com.example.personalfinancetracker.util.CurrencyFormatter
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale

class TransactionAdapter(
    private var transactions: List<Transaction>,
    private val showEditIcon: Boolean = false, // Default to not showing
    private val onClick: (Transaction) -> Unit
) : RecyclerView.Adapter<TransactionAdapter.ViewHolder>() {

    private val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    private val currencyFormat = NumberFormat.getCurrencyInstance()

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val categoryIcon: ImageView = itemView.findViewById(R.id.categoryIcon)
        val title: TextView = itemView.findViewById(R.id.transactionTitle)
        val category: TextView = itemView.findViewById(R.id.transactionCategory)
        val date: TextView = itemView.findViewById(R.id.transactionDate)
        val amount: TextView = itemView.findViewById(R.id.transactionAmount)
        val editIcon: ImageView? = itemView.findViewById(R.id.editIcon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_transaction, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val transaction = transactions[position]

        // Set category icon
        holder.categoryIcon.setImageResource(
            CategoryUtil.getCategoryIcon(transaction.category)
        )

        // Set texts
        holder.title.text = transaction.title
        holder.category.text = transaction.category.displayName
        holder.date.text = dateFormat.format(transaction.date)
// Show or hide edit icon based on parameter
        if (holder.editIcon != null) {
            holder.editIcon.visibility = if (showEditIcon) View.VISIBLE else View.GONE
        }

        // Format amount based on transaction type
        val amountText = if (transaction.isExpense) {
            "-${CurrencyFormatter.getFormattedCurrency(transaction.amount, holder.itemView.context)}"
        } else {
            "+${CurrencyFormatter.getFormattedCurrency(transaction.amount, holder.itemView.context)}"
        }
        holder.amount.text = amountText

        // Set text color based on transaction type
        holder.amount.setTextColor(
            holder.itemView.context.getColor(
                if (transaction.isExpense) R.color.expense else R.color.income
            )
        )

        // Set click listener
        holder.itemView.setOnClickListener {
            onClick(transaction)
        }
    }

    override fun getItemCount() = transactions.size

    fun updateTransactions(newTransactions: List<Transaction>) {
        transactions = newTransactions
        notifyDataSetChanged()
    }
}