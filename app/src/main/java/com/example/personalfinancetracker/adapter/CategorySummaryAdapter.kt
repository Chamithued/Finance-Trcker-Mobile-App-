package com.example.personalfinancetracker.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.personalfinancetracker.R
import com.example.personalfinancetracker.model.TransactionCategory
import com.example.personalfinancetracker.util.CategoryUtil
import com.example.personalfinancetracker.util.CurrencyFormatter // ✅ Added import

data class CategorySummary(
    val category: TransactionCategory,
    val amount: Double,
    val percentage: Int
)

class CategorySummaryAdapter(
    private var categorySummaries: List<CategorySummary>
) : RecyclerView.Adapter<CategorySummaryAdapter.ViewHolder>() {

    // ✅ Removed old currencyFormat (replaced by CurrencyFormatter)
    // private val currencyFormat = NumberFormat.getCurrencyInstance()

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val categoryIcon: ImageView = itemView.findViewById(R.id.categoryIcon)
        val categoryName: TextView = itemView.findViewById(R.id.categoryName)
        val percentageText: TextView = itemView.findViewById(R.id.percentageText)
        val amountText: TextView = itemView.findViewById(R.id.amountText)
        val progressBar: ProgressBar = itemView.findViewById(R.id.categoryProgressBar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_category_summary, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val summary = categorySummaries[position]

        // Set category icon
        holder.categoryIcon.setImageResource(
            CategoryUtil.getCategoryIcon(summary.category)
        )

        // Set texts
        holder.categoryName.text = summary.category.displayName
        holder.percentageText.text = "${summary.percentage}% of total expenses"

        // ✅ Updated to use CurrencyFormatter
        holder.amountText.text = CurrencyFormatter.getFormattedCurrency(
            summary.amount,
            holder.itemView.context
        )

        // Set progress
        holder.progressBar.progress = summary.percentage
    }

    override fun getItemCount() = categorySummaries.size

    fun updateCategorySummaries(newSummaries: List<CategorySummary>) {
        categorySummaries = newSummaries
        notifyDataSetChanged()
    }
}
