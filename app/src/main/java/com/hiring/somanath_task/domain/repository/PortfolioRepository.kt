package com.hiring.somanath_task.domain.repository

import com.hiring.somanath_task.domain.model.ApiResult
import com.hiring.somanath_task.domain.model.UserHolding
import kotlinx.coroutines.flow.StateFlow

interface PortfolioRepository {
    val holdings: StateFlow<List<UserHolding>>
    suspend fun getHoldings(): List<UserHolding>
    suspend fun refreshHoldings(): ApiResult<List<UserHolding>>
    suspend fun clearLocalData()
}