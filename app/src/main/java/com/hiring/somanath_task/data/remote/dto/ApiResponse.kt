package com.hiring.somanath_task.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ApiResponse(
    val data: HoldingsData
)