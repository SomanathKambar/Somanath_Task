package com.hiring.somanath_task.domain.model

import org.junit.Assert.*
import org.junit.Test

class PortfolioSummaryTest {

    @Test
    fun `portfolio summary calculates values correctly`() {
        val summary = PortfolioSummary(
            totalCurrentValue = 10000.0,
            totalInvestment = 9000.0,
            totalPnl = 1000.0,
            todaysPnl = 500.0,
            totalPnlPercentage = 11.11
        )

        assertEquals(10000.0, summary.totalCurrentValue, 0.001)
        assertEquals(9000.0, summary.totalInvestment, 0.001)
        assertEquals(1000.0, summary.totalPnl, 0.001)
        assertEquals(500.0, summary.todaysPnl, 0.001)
        assertEquals(11.11, summary.totalPnlPercentage, 0.001)
    }

    @Test
    fun `portfolio summary identifies profitable correctly`() {
        val profitable = PortfolioSummary(10000.0, 9000.0, 1000.0, 500.0, 11.11)
        val loss = PortfolioSummary(8000.0, 9000.0, -1000.0, -500.0, -11.11)
        
        assertTrue(profitable.isTotalProfitable)
        assertFalse(loss.isTotalProfitable)
        assertTrue(profitable.isTodaysProfitable)
        assertFalse(loss.isTodaysProfitable)
    }

    @Test
    fun `portfolio summary formats values correctly`() {
        val summary = PortfolioSummary(10000.0, 9000.0, 1000.0, 500.0, 11.11)
        
        assertEquals("₹10000.00", summary.getFormattedCurrentValue())
        assertEquals("₹9000.00", summary.getFormattedInvestment())
        assertEquals("₹1000.00 (11.11%)", summary.getFormattedTotalPnl())
        assertEquals("₹500.00", summary.getFormattedTodaysPnl())
    }

    @Test
    fun `portfolio summary formats negative pnl correctly`() {
        val summary = PortfolioSummary(8000.0, 9000.0, -1000.0, -500.0, -11.11)
        
        assertEquals("₹-1000.00 (-11.11%)", summary.getFormattedTotalPnl())
        assertEquals("₹-500.00", summary.getFormattedTodaysPnl())
    }
}