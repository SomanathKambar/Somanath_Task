package com.hiring.somanath_task.domain.usecase

import com.hiring.somanath_task.domain.repository.PortfolioRepository

class GetCachedHoldingUseCase(val repository: PortfolioRepository) {

     fun execute() =  repository.holdings
}