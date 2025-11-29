package com.hiring.somanath_task.util

import android.content.Context
import android.content.res.Configuration

object UiConfig {

    fun getScreenSize(context: Context): String {
        val configuration = context.resources.configuration
        return when (configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK) {
            Configuration.SCREENLAYOUT_SIZE_SMALL -> "small"
            Configuration.SCREENLAYOUT_SIZE_NORMAL -> "normal"
            Configuration.SCREENLAYOUT_SIZE_LARGE -> "large"
            Configuration.SCREENLAYOUT_SIZE_XLARGE -> "xlarge"
            else -> "undefined"
        }
    }

    fun isTablet(context: Context): Boolean {
        val screenSize = getScreenSize(context)
        return screenSize == "large" || screenSize == "xlarge"
    }

    fun isSmallScreen(context: Context): Boolean {
        return getScreenSize(context) == "small"
    }

}