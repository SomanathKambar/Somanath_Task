package com.hiring.somanath_task.util.extensions

import android.content.Context
import androidx.core.content.ContextCompat

fun Context.getColorCompat(resId: Int): Int {
    return ContextCompat.getColor(this, resId)
}