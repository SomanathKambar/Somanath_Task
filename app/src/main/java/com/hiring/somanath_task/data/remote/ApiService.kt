package com.hiring.somanath_task.data.remote

import com.hiring.somanath_task.domain.model.ApiResult
import com.hiring.somanath_task.domain.model.UserHolding
import com.hiring.somanath_task.domain.repository.RemoteDataSource
import com.hiring.somanath_task.util.AppConstants
import com.hiring.somanath_task.util.ErrorMapper
import com.hiring.somanath_task.util.logging.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL
import javax.net.ssl.SSLHandshakeException

class ApiService(private val logger: Logger) : RemoteDataSource {

    private val parser = ApiParser(logger)

    companion object {
        private const val TAG = "ApiService"
    }

    override suspend fun fetchHoldings(): ApiResult<List<UserHolding>> = withContext(Dispatchers.IO) {
        return@withContext try {
            logger.d(TAG, "Starting API call...")

            val jsonString = makeApiCall()
            logger.d(TAG, "API call successful, response length: ${jsonString.length}")

            val holdingsDtoList = parser.parseHoldingsJson(jsonString)

            // Convert DTO to Domain Model
            val domainHoldings = holdingsDtoList.map { dto ->
                UserHolding(
                    symbol = dto.symbol,
                    quantity = dto.quantity,
                    ltp = dto.ltp,
                    avgPrice = dto.avgPrice,
                    close = dto.close
                )
            }

            logger.d(TAG, "Successfully converted to domain models: ${domainHoldings.size} holdings")
            ApiResult.Success(domainHoldings)

        } catch (e: Exception) {
            logger.e(TAG, "API call failed", e)
            val appError = ErrorMapper.mapExceptionToAppError(e)
            val userMessage = ErrorMapper.mapToUserMessage(appError)
            ApiResult.Failure(userMessage)
        }
    }

    private fun makeApiCall(): String {
        var connection: HttpURLConnection? = null
        try {
            val url = URL(AppConstants.API_BASE_URL)
            connection = url.openConnection() as HttpURLConnection
            connection.apply {
                connectTimeout = AppConstants.REQUEST_TIMEOUT.toInt()
                readTimeout = AppConstants.REQUEST_TIMEOUT.toInt()
                requestMethod = "GET"
                setRequestProperty("Accept", "application/json")
                setRequestProperty("Content-Type", "application/json")
                setRequestProperty("User-Agent", "StockPortfolio/1.0")
                setRequestProperty("Cache-Control", "no-cache")
                useCaches = false
            }

            logger.d(TAG, "Connecting to: ${AppConstants.API_BASE_URL}")

            val responseCode = connection.responseCode
            logger.d(TAG, "HTTP Response Code: $responseCode")

            if (responseCode == HttpURLConnection.HTTP_OK) {
                val response = readResponseSafely(connection)
                logger.d(TAG, "Response received, length: ${response.length}")
                return response
            } else {
                val errorStream = connection.errorStream
                val errorResponse = errorStream?.bufferedReader()?.use { it.readText() } ?: "No error body"
                logger.e(TAG, "HTTP Error $responseCode: $errorResponse")
                throw when (responseCode) {
                    in 400..499 -> Exception("Client error: $responseCode")
                    in 500..599 -> Exception("Server error: $responseCode")
                    else -> Exception("HTTP error: $responseCode")
                }
            }
        } catch (e: SocketTimeoutException) {
            e.printStackTrace()
            logger.e(TAG, "Connection timeout")
            throw Exception("Connection timeout - server took too long to respond")
        } catch (e: SSLHandshakeException) {
            e.printStackTrace()
            logger.e(TAG, "SSL Handshake failed")
            throw Exception("Security error - SSL handshake failed")
        } catch (e: SecurityException) {
            e.printStackTrace()
            logger.e(TAG, "Network security exception")
            throw Exception("Network permission denied")
        } catch (e: Exception) {
            e.printStackTrace()
            logger.e(TAG, "Network error", e)
            throw Exception("Network error: ${e.message}")
        } finally {
            connection?.disconnect()
            logger.d(TAG, "Connection closed")
        }
    }

    private fun readResponseSafely(connection: HttpURLConnection): String {
        return try {
            val inputStream: InputStream = connection.inputStream
            val reader = inputStream.bufferedReader()
            val response = StringBuilder()
            val buffer = CharArray(AppConstants.BUFFER_SIZE)

            var read: Int
            var totalRead = 0

            while (reader.read(buffer).also { read = it } != -1) {
                response.append(buffer, 0, read)
                totalRead += read

                if (totalRead > AppConstants.MAX_RESPONSE_SIZE) {
                    throw Exception("Response too large: $totalRead bytes")
                }
            }

            response.toString()
        } catch (e: Exception) {
            logger.e(TAG, "Error reading response", e)
            throw Exception("Failed to read response: ${e.message}")
        }
    }
}