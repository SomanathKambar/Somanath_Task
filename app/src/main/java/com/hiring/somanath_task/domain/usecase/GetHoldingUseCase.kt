package com.hiring.somanath_task.domain.usecase

import com.hiring.somanath_task.domain.repository.PortfolioRepository

class GetHoldingUseCase(val repository: PortfolioRepository) {
    suspend fun execute() =  repository.getHoldings()
}