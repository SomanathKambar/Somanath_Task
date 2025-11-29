package com.hiring.somanath_task.data.local.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(
    context,
    "portfolio.db",
    null,
    1
) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE ${AppDatabase.TABLE_HOLDINGS} (
                ${AppDatabase.COLUMN_SYMBOL} TEXT PRIMARY KEY,
                ${AppDatabase.COLUMN_QUANTITY} INTEGER NOT NULL,
                ${AppDatabase.COLUMN_LTP} REAL NOT NULL,
                ${AppDatabase.COLUMN_AVG_PRICE} REAL NOT NULL,
                ${AppDatabase.COLUMN_CLOSE} REAL NOT NULL
            )
            """.trimIndent()
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS ${AppDatabase.TABLE_HOLDINGS}")
        onCreate(db)
    }
}