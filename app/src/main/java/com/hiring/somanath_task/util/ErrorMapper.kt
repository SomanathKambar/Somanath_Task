package com.hiring.somanath_task.util


import android.database.SQLException
import com.hiring.somanath_task.domain.model.AppError
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

object ErrorMapper {

    fun mapToUserMessage(error: AppError): String {
        return when (error) {
            is AppError.NoInternet -> "Please check your internet connection and try again"
            is AppError.Timeout -> "Request timed out. Please check your connection"
            is AppError.DatabaseError -> "Failed to save data locally"
            is AppError.Unknown -> "An unexpected error occurred"
            is AppError.Custom -> error.message
            is AppError.HttpError -> when (error.code) {
                404 -> "Data not found"
                500 -> "Internal server error"
                502 -> "Bad gateway"
                503 -> "Service unavailable"
                else -> "Network error: ${error.code}"
            }
        }
    }

    fun mapExceptionToAppError(exception: Exception): AppError {
        return when (exception) {
            is UnknownHostException -> AppError.NoInternet
            is SocketTimeoutException -> AppError.Timeout
            is IOException -> AppError.NoInternet
            is SQLException -> AppError.DatabaseError
            else -> AppError.Unknown
        }
    }
}