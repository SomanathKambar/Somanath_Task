package com.hiring.somanath_task.domain.repository

import com.hiring.somanath_task.domain.model.UserHolding

interface LocalDataSource {
    suspend fun getHoldings(): List<UserHolding>
    suspend fun saveHoldings(holdings: List<UserHolding>)
    suspend fun clearHoldings()
}