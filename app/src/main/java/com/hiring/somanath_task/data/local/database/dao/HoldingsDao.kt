package com.hiring.somanath_task.data.local.database.dao

import com.hiring.somanath_task.data.local.database.AppDatabase
import com.hiring.somanath_task.data.local.database.entity.HoldingEntity
import com.hiring.somanath_task.domain.model.UserHolding
import com.hiring.somanath_task.domain.repository.LocalDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class HoldingsDao(private val appDatabase: AppDatabase) : LocalDataSource {

    override suspend fun getHoldings(): List<UserHolding> = withContext(Dispatchers.IO) {
        val entities = appDatabase.getHoldings()
        entities.map { entity ->
            UserHolding(
                symbol = entity.symbol,
                quantity = entity.quantity,
                ltp = entity.ltp,
                avgPrice = entity.avgPrice,
                close = entity.close
            )
        }
    }

    override suspend fun saveHoldings(holdings: List<UserHolding>) = withContext(Dispatchers.IO) {
        val entities = holdings.map { domain ->
            HoldingEntity(
                symbol = domain.symbol,
                quantity = domain.quantity,
                ltp = domain.ltp,
                avgPrice = domain.avgPrice,
                close = domain.close
            )
        }
        appDatabase.insertHoldings(entities)
    }

    override suspend fun clearHoldings() = withContext(Dispatchers.IO) {
        appDatabase.clearHoldings()
    }
}