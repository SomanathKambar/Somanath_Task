package com.hiring.somanath_task.domain.usecase

import com.hiring.somanath_task.domain.model.PortfolioSummary
import com.hiring.somanath_task.domain.model.UserHolding
import com.hiring.somanath_task.util.extensions.calculatePnlPercentage
import com.hiring.somanath_task.util.extensions.calculateTodaysPnl
import com.hiring.somanath_task.util.extensions.calculateTotalCurrentValue
import com.hiring.somanath_task.util.extensions.calculateTotalInvestment
import com.hiring.somanath_task.util.extensions.calculateTotalPnl
import com.hiring.somanath_task.util.extensions.validateHoldings


class CalculatePortfolioUseCase {

    fun execute(holdings: List<UserHolding>): PortfolioSummary {
        require(holdings.isNotEmpty()) { "Holdings list cannot be empty" }
        require(holdings.validateHoldings()) { "Invalid holdings data" }

        return PortfolioSummary(
            totalCurrentValue = holdings.calculateTotalCurrentValue(),
            totalInvestment = holdings.calculateTotalInvestment(),
            totalPnl = holdings.calculateTotalPnl(),
            todaysPnl = holdings.calculateTodaysPnl(),
            totalPnlPercentage = holdings.calculatePnlPercentage()
        )
    }
}