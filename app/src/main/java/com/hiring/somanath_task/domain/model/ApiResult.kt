package com.hiring.somanath_task.domain.model

sealed class ApiResult<out T : Any> {
    data object Loading : ApiResult<Nothing>()
    data class Success<out T : Any>(val data: T) : ApiResult<T>()
    data class Failure(val errorMessage: String, val errorCode: Int = 0) : ApiResult<Nothing>()
}