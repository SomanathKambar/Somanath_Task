package com.hiring.somanath_task.domain.model

import com.hiring.somanath_task.util.AppConstants


data class PortfolioSummary(
    val totalCurrentValue: Double,
    val totalInvestment: Double,
    val totalPnl: Double,
    val todaysPnl: Double,
    val totalPnlPercentage: Double
) {
    val isTotalProfitable: Boolean get() = totalPnl >= 0
    val isTodaysProfitable: Boolean get() = todaysPnl >= 0
    
    fun getFormattedTotalPnl(): String = 
        "₹${String.format(AppConstants.DECIMAL_FORMAT, totalPnl)} (${String.format(AppConstants.DECIMAL_FORMAT, totalPnlPercentage)}%)"
    
    fun getFormattedTodaysPnl(): String = "₹${String.format(AppConstants.DECIMAL_FORMAT, todaysPnl)}"
    fun getFormattedCurrentValue(): String = "₹${String.format(AppConstants.DECIMAL_FORMAT, totalCurrentValue)}"
    fun getFormattedInvestment(): String = "₹${String.format(AppConstants.DECIMAL_FORMAT, totalInvestment)}"
}