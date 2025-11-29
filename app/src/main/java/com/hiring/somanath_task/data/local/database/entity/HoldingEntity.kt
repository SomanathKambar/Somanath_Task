package com.hiring.somanath_task.data.local.database.entity

import kotlinx.serialization.Serializable

@Serializable
data class HoldingEntity(
    val symbol: String,
    val quantity: Int,
    val ltp: Double,
    val avgPrice: Double,
    val close: Double
) {
    companion object {
        const val TABLE_NAME = "holdings"
    }
}