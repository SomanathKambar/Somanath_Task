package com.hiring.somanath_task.data.local.database.entity

data class UserHoldingDto(
    val symbol: String,
    val quantity: Int,
    val ltp: Double,
    val avgPrice: Double,
    val close: Double
)