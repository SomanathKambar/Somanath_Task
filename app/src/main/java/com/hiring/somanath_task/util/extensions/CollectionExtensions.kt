package com.hiring.somanath_task.util.extensions

import com.hiring.somanath_task.domain.model.UserHolding
import com.hiring.somanath_task.util.AppConstants


fun List<UserHolding>.calculateTotalCurrentValue(): Double {
    return sumOf { it.currentValue }
}

fun List<UserHolding>.calculateTotalInvestment(): Double {
    return sumOf { it.investmentValue }
}

fun List<UserHolding>.calculateTotalPnl(): Double {
    return calculateTotalCurrentValue() - calculateTotalInvestment()
}

fun List<UserHolding>.calculateTodaysPnl(): Double {
    return sumOf { it.todaysPnl }
}

fun List<UserHolding>.calculatePnlPercentage(): Double {
    val totalInvestment = calculateTotalInvestment()
    return if (totalInvestment > 0) {
        (calculateTotalPnl() / totalInvestment) * 100
    } else 0.0
}

fun List<UserHolding>.validateHoldings(): Boolean {
    return all { holding ->
        holding.symbol.isNotBlank() &&
                holding.quantity >= AppConstants.MIN_QUANTITY &&
                holding.ltp >= AppConstants.MIN_PRICE &&
                holding.avgPrice >= AppConstants.MIN_PRICE &&
                holding.close >= AppConstants.MIN_PRICE
    }
}