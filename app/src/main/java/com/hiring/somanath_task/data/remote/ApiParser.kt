package com.hiring.somanath_task.data.remote

import com.hiring.somanath_task.data.local.database.entity.UserHoldingDto
import com.hiring.somanath_task.data.remote.dto.ApiResponse
import com.hiring.somanath_task.util.logging.Logger
import kotlinx.serialization.json.Json

class ApiParser(private val logger: Logger) {

    companion object {
        private const val TAG = "ApiParser"
    }

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        coerceInputValues = true
    }

    fun parseHoldingsJson(jsonString: String): List<UserHoldingDto> {
        return try {
            logger.d(TAG, "Starting JSON parsing with kotlinx.serialization...")

            if (jsonString.isBlank()) {
                throw IllegalArgumentException("Empty JSON response")
            }

            val apiResponse = json.decodeFromString<ApiResponse>(jsonString)
            logger.d(TAG, "Successfully parsed API response")

            val holdings = apiResponse.data.userHolding
            logger.d(TAG, "Found ${holdings.size} holdings in response")

            // Validate holdings
            val validHoldings = holdings.filter { holding ->
                isValidHolding(holding).also { isValid ->
                    if (!isValid) {
                        logger.w(TAG, "Skipping invalid holding: ${holding.symbol}")
                    }
                }
            }

            if (validHoldings.isEmpty()) {
                throw IllegalArgumentException("No valid holdings found in response")
            }

            logger.d(TAG, "Successfully parsed ${validHoldings.size} valid holdings")
            validHoldings
        } catch (e: Exception) {
            logger.e(TAG, "JSON parsing error", e)
            throw IllegalArgumentException("Failed to parse holdings: ${e.message}")
        }
    }

    private fun isValidHolding(holding: UserHoldingDto): Boolean {
        return try {
            val isValid = holding.symbol.isNotBlank() &&
                    holding.quantity >= 0 &&
                    holding.ltp >= 0.0 &&
                    holding.avgPrice >= 0.0 &&
                    holding.close >= 0.0

            if (!isValid) {
                logger.w(TAG, "Invalid holding data: $holding")
            }

            isValid
        } catch (e: Exception) {
            logger.e(TAG, "Error validating holding: $holding", e)
            false
        }
    }
}