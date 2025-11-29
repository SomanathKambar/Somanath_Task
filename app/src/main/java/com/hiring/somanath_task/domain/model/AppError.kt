package com.hiring.somanath_task.domain.model

sealed class AppError {
    data object NoInternet : AppError()
    data object Timeout : AppError()
    data object DatabaseError : AppError()
    data object Unknown : AppError()
    
    data class Custom(val message: String) : AppError()
    data class HttpError(val code: Int, val message: String) : AppError()
}