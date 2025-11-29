package com.hiring.somanath_task.domain.repository

import com.hiring.somanath_task.domain.model.ApiResult
import com.hiring.somanath_task.domain.model.UserHolding

interface RemoteDataSource {
    suspend fun fetchHoldings(): ApiResult<List<UserHolding>>
}