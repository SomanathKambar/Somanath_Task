package com.hiring.somanath_task.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hiring.somanath_task.domain.Interactor
import com.hiring.somanath_task.domain.model.ApiResult
import com.hiring.somanath_task.domain.model.PortfolioSummary
import com.hiring.somanath_task.domain.model.UserHolding
import com.hiring.somanath_task.presentation.state.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class PortfolioViewModel(
    private val interactor: Interactor
) : ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _portfolioSummary = MutableStateFlow<PortfolioSummary?>(null)
    val portfolioSummary: StateFlow<PortfolioSummary?> = _portfolioSummary.asStateFlow()

    init {
        loadHoldings()
    }

    fun loadHoldings() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null
            )

            try {
                val holdingsList = interactor.getHoldingUseCase.execute()
                updateUiWithHoldings(holdingsList)

                if (holdingsList.isEmpty()) {
                    _uiState.value = _uiState.value.copy(
                        errorMessage = "No investment data available",
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Failed to load portfolio data",
                    isLoading = false,
                    hasData = _uiState.value.holdings.isNotEmpty()
                )
            }
        }
    }

    fun refreshHoldings() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isRefreshing = true,
                errorMessage = null
            )

            try {
                when (val result = interactor.refreshHoldingUseCase.execute()) {
                    is ApiResult.Success -> {
                        updateUiWithHoldings(result.data)
                    }
                    is ApiResult.Failure -> {
                        _uiState.value = _uiState.value.copy(
                            errorMessage = result.errorMessage,
                            isRefreshing = false,
                            hasData = _uiState.value.holdings.isNotEmpty()
                        )
                    }
                    is ApiResult.Loading -> {
                        // Loading state already set
                    }
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Refresh failed: ${e.message}",
                    isRefreshing = false
                )
            }
        }
    }

    private fun updateUiWithHoldings(holdings: List<UserHolding>) {
        _portfolioSummary.value = interactor.calculatePortfolioUseCase.execute(holdings)
        _uiState.value = UiState(
            holdings = holdings,
            hasData = holdings.isNotEmpty(),
            isSummaryExpanded = _uiState.value.isSummaryExpanded,
            isLoading = false,
            isRefreshing = false
        )
    }

    fun toggleSummaryExpanded() {
        _uiState.value = _uiState.value.copy(
            isSummaryExpanded = !_uiState.value.isSummaryExpanded
        )
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    fun retryWithOfflineData() {
        viewModelScope.launch {
            try {
                val localHoldings = interactor.getCachedHoldingUseCase.execute().value
                if (localHoldings.isNotEmpty()) {
                    updateUiWithHoldings(localHoldings)
                } else {
                    _uiState.value = _uiState.value.copy(
                        errorMessage = "No local data available. Please check your connection."
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Cannot access local data"
                )
            }
        }
    }
}