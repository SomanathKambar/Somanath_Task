package com.hiring.somanath_task.domain.usecase

import com.hiring.somanath_task.domain.model.UserHolding
import org.junit.Assert.*
import org.junit.Test

class CalculatePortfolioUseCaseTest {

    private val useCase = CalculatePortfolioUseCase()

    @Test
    fun `calculate portfolio summary correctly`() {
        val holdings = listOf(
            UserHolding("TEST1", 100, 50.0, 45.0, 55.0),
            UserHolding("TEST2", 50, 100.0, 90.0, 110.0)
        )

        val summary = useCase.execute(holdings)

        assertEquals(10000.0, summary.totalCurrentValue, 0.001)

        assertEquals(9000.0, summary.totalInvestment, 0.001)

        assertEquals(1000.0, summary.totalPnl, 0.001)

        assertEquals(1000.0, summary.todaysPnl, 0.001)

        assertEquals(11.111, summary.totalPnlPercentage, 0.001)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `calculate with empty holdings throws exception`() {
        useCase.execute(emptyList())
    }

    @Test
    fun `calculate with negative pnl`() {
        val holdings = listOf(
            UserHolding("TEST1", 100, 40.0, 45.0, 35.0),
            UserHolding("TEST2", 50, 80.0, 90.0, 70.0)
        )

        val summary = useCase.execute(holdings)

        assertEquals(8000.0, summary.totalCurrentValue, 0.001)
        assertEquals(9000.0, summary.totalInvestment, 0.001)
        assertEquals(-1000.0, summary.totalPnl, 0.001)
        assertEquals(-1000.0, summary.todaysPnl, 0.001)
        assertEquals(-11.111, summary.totalPnlPercentage, 0.001)
    }

    @Test
    fun `calculate portfolio summary with multiple holdings`() {
        val holdings = listOf(
            UserHolding("TEST1", 100, 50.0, 45.0, 55.0),
            UserHolding("TEST2", 50, 100.0, 90.0, 110.0),
            UserHolding("TEST3", 200, 25.0, 20.0, 30.0)
        )

        val summary = useCase.execute(holdings)

        assertEquals(15000.0, summary.totalCurrentValue, 0.001)
        assertEquals(13000.0, summary.totalInvestment, 0.001)
        assertEquals(2000.0, summary.totalPnl, 0.001)
        assertEquals(2000.0, summary.todaysPnl, 0.001)
        assertEquals(15.3846, summary.totalPnlPercentage, 0.001)
    }
}