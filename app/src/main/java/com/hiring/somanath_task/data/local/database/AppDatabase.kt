package com.hiring.somanath_task.data.local.database

import android.content.ContentValues
import androidx.core.database.sqlite.transaction
import com.hiring.somanath_task.data.local.database.entity.HoldingEntity
import com.hiring.somanath_task.domain.model.UserHolding

class AppDatabase(private val databaseHelper: DatabaseHelper) {

    companion object {
        const val TABLE_HOLDINGS = "holdings"
        const val COLUMN_SYMBOL = "symbol"
        const val COLUMN_QUANTITY = "quantity"
        const val COLUMN_LTP = "ltp"
        const val COLUMN_AVG_PRICE = "avg_price"
        const val COLUMN_CLOSE = "close"
    }

    fun insertHoldings(holdings: List<HoldingEntity>) {
        val db = databaseHelper.writableDatabase
        db.transaction {
            try {
                delete(TABLE_HOLDINGS, null, null)
                holdings.forEach { holding ->
                    val values = ContentValues().apply {
                        put(COLUMN_SYMBOL, holding.symbol)
                        put(COLUMN_QUANTITY, holding.quantity)
                        put(COLUMN_LTP, holding.ltp)
                        put(COLUMN_AVG_PRICE, holding.avgPrice)
                        put(COLUMN_CLOSE, holding.close)
                    }
                    insert(TABLE_HOLDINGS, null, values)
                }
            } finally {
            }
        }
    }

    fun getHoldings(): List<UserHolding> {
        val holdings = mutableListOf<UserHolding>()
        val db = databaseHelper.readableDatabase
        val cursor = db.query(
            TABLE_HOLDINGS,
            null, null, null, null, null, null
        )

        cursor.use {
            while (it.moveToNext()) {
                val holding = UserHolding(
                    symbol = it.getString(it.getColumnIndexOrThrow(COLUMN_SYMBOL)),
                    quantity = it.getInt(it.getColumnIndexOrThrow(COLUMN_QUANTITY)),
                    ltp = it.getDouble(it.getColumnIndexOrThrow(COLUMN_LTP)),
                    avgPrice = it.getDouble(it.getColumnIndexOrThrow(COLUMN_AVG_PRICE)),
                    close = it.getDouble(it.getColumnIndexOrThrow(COLUMN_CLOSE))
                )
                holdings.add(holding)
            }
        }
        return holdings
    }

    fun clearHoldings() {
        val db = databaseHelper.writableDatabase
        db.delete(HoldingEntity.TABLE_NAME, null, null)
    }
}