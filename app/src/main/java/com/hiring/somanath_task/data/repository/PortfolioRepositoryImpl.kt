package com.hiring.somanath_task.data.repository


import com.hiring.somanath_task.domain.model.ApiResult
import com.hiring.somanath_task.domain.model.UserHolding
import com.hiring.somanath_task.domain.repository.LocalDataSource
import com.hiring.somanath_task.domain.repository.PortfolioRepository
import com.hiring.somanath_task.domain.repository.RemoteDataSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class PortfolioRepositoryImpl(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource
) : PortfolioRepository {

    private val _holdings = MutableStateFlow<List<UserHolding>>(emptyList())
    override val holdings: StateFlow<List<UserHolding>> = _holdings

    override suspend fun getHoldings(): List<UserHolding> {
        return try {
            val localHoldings = localDataSource.getHoldings()
            if (localHoldings.isNotEmpty()) {
                _holdings.value = localHoldings
                return localHoldings
            }
            when (val result = remoteDataSource.fetchHoldings()) {
                is ApiResult.Success -> {
                    saveHoldingsSafely(result.data)
                }
                is ApiResult.Failure -> {
                    emptyList()
                }
                is ApiResult.Loading -> emptyList()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    override suspend fun refreshHoldings(): ApiResult<List<UserHolding>> {
        return try {
            when (val result = remoteDataSource.fetchHoldings()) {
                is ApiResult.Success -> {
                    val domainHoldings = saveHoldingsSafely(result.data)
                    ApiResult.Success(domainHoldings)
                }
                is ApiResult.Failure -> {
                    // On refresh failure, still return local data if available
                    val localHoldings = localDataSource.getHoldings()
                    if (localHoldings.isNotEmpty()) {
                        _holdings.value = localHoldings
                        ApiResult.Success(localHoldings)
                    } else {
                        result
                    }
                }
                is ApiResult.Loading -> ApiResult.Loading
            }
        } catch (e: Exception) {
            e.printStackTrace()
            ApiResult.Failure("Local storage error: ${e.message}")
        }
    }

    private suspend fun saveHoldingsSafely(holdings: List<UserHolding>): List<UserHolding> {
        return try {
            localDataSource.saveHoldings(holdings)
            _holdings.value = holdings
            holdings
        } catch (e: Exception) {
            e.printStackTrace()
            _holdings.value = holdings
            holdings
        }
    }

    override suspend fun clearLocalData() {
        try {
            localDataSource.clearHoldings()
            _holdings.value = emptyList()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}