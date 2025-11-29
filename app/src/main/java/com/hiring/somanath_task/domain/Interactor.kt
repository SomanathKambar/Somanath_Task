package com.hiring.somanath_task.domain

import com.hiring.somanath_task.domain.usecase.CalculatePortfolioUseCase
import com.hiring.somanath_task.domain.usecase.GetCachedHoldingUseCase
import com.hiring.somanath_task.domain.usecase.GetHoldingUseCase
import com.hiring.somanath_task.domain.usecase.RefreshHoldingUseCase

class Interactor(
    val getHoldingUseCase: GetHoldingUseCase,
    val refreshHoldingUseCase: RefreshHoldingUseCase,
    val calculatePortfolioUseCase: CalculatePortfolioUseCase,
    val getCachedHoldingUseCase: GetCachedHoldingUseCase) {
}