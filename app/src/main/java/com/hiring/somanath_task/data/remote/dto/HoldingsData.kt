package com.hiring.somanath_task.data.remote.dto

import com.hiring.somanath_task.data.local.database.entity.UserHoldingDto
import kotlinx.serialization.Serializable

@Serializable
data class HoldingsData(
    val userHolding: List<UserHoldingDto>
)