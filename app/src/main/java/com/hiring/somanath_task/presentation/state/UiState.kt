package com.hiring.somanath_task.presentation.state

import com.hiring.somanath_task.domain.model.UserHolding

data class UiState(
    val isLoading: Boolean = false,
    val holdings: List<UserHolding> = emptyList(),
    val errorMessage: String? = null,
    val isSummaryExpanded: Boolean = false,
    val hasData: Boolean = false,
    val isRefreshing: Boolean = false
)