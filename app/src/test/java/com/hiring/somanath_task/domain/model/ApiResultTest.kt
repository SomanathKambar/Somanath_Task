package com.hiring.somanath_task.domain.model

import org.junit.Assert.*
import org.junit.Test

class ApiResultTest {

    @Test
    fun `api result success contains data`() {
        val data = listOf(UserHolding("TEST", 100, 50.0, 45.0, 55.0))
        val result = ApiResult.Success(data)
        
        assertTrue(result is ApiResult.Success)
        assertEquals(data, result.data)
    }

    @Test
    fun `api result failure contains error message`() {
        val errorMessage = "Network error"
        val result = ApiResult.Failure(errorMessage)
        
        assertTrue(result is ApiResult.Failure)
        assertEquals(errorMessage, result.errorMessage)
    }

    @Test
    fun `api result failure with error code`() {
        val result = ApiResult.Failure("Error", 404)
        
        assertEquals(404, result.errorCode)
    }
}