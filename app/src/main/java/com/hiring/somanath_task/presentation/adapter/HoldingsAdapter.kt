package com.hiring.somanath_task.presentation.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.hiring.somanath_task.R
import com.hiring.somanath_task.databinding.ItemHoldingBinding
import com.hiring.somanath_task.domain.model.UserHolding
import com.hiring.somanath_task.util.UiConfig
import com.hiring.somanath_task.util.extensions.getColorCompat

class HoldingsAdapter(private val context: Context) :
    ListAdapter<UserHolding, HoldingsAdapter.HoldingViewHolder>(HoldingDiffCallback) {

    private object HoldingDiffCallback : DiffUtil.ItemCallback<UserHolding>() {
        override fun areItemsTheSame(oldItem: UserHolding, newItem: UserHolding): Boolean {
            return oldItem.symbol == newItem.symbol
        }

        override fun areContentsTheSame(oldItem: UserHolding, newItem: UserHolding): Boolean {
            return oldItem == newItem
        }
    }

    inner class HoldingViewHolder(private val binding: ItemHoldingBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(holding: UserHolding) {
            with(binding) {
                symbolText.text = holding.symbol
                ltpText.text = holding.getFormattedLtp()
                quantityText.text = holding.getFormattedQuantity()
                pnlText.text = holding.getFormattedPnl()

                val pnlColor = if (holding.isProfitable()) {
                    context.getColorCompat(R.color.profit_green)
                } else {
                    context.getColorCompat(R.color.loss_red)
                }
                pnlText.setTextColor(pnlColor)

                adjustTextSizes()
            }
        }

        private fun adjustTextSizes() {
            with(binding) {
                if (UiConfig.isSmallScreen(context)) {
                    symbolText.textSize = 14f
                    ltpText.textSize = 12f
                    quantityText.textSize = 12f
                    pnlText.textSize = 12f
                } else if (UiConfig.isTablet(context)) {
                    symbolText.textSize = 18f
                    ltpText.textSize = 16f
                    quantityText.textSize = 16f
                    pnlText.textSize = 16f
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HoldingViewHolder {
        val binding = ItemHoldingBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return HoldingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HoldingViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

}