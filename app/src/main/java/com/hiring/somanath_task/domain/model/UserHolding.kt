package com.hiring.somanath_task.domain.model

import com.hiring.somanath_task.util.AppConstants


data class UserHolding(
    val symbol: String,
    val quantity: Int,
    val ltp: Double,
    val avgPrice: Double,
    val close: Double
) {
    val currentValue: Double get() = ltp * quantity
    val investmentValue: Double get() = avgPrice * quantity
    val pnl: Double get() = currentValue - investmentValue
    val todaysPnl: Double get() = (close - ltp) * quantity

    fun getFormattedLtp(): String = "LTP: ₹${String.format(AppConstants.DECIMAL_FORMAT, ltp)}"
    fun getFormattedQuantity(): String = "Qty: $quantity"
    fun getFormattedPnl(): String = "P&L: ${getPnlWithSign()}"

    private fun getPnlWithSign(): String {
        val sign = if (pnl >= 0) "+" else ""
        return "$sign₹${String.format(AppConstants.DECIMAL_FORMAT, pnl)}"
    }

    fun isProfitable(): Boolean = pnl >= 0
}