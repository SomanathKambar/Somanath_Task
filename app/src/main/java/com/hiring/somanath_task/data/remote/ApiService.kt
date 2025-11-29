package com.hiring.somanath_task.data.remote

import android.util.Log
import com.hiring.somanath_task.domain.model.ApiResult
import com.hiring.somanath_task.domain.model.UserHolding
import com.hiring.somanath_task.domain.repository.RemoteDataSource
import com.hiring.somanath_task.util.AppConstants
import com.hiring.somanath_task.util.ErrorMapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL
import javax.net.ssl.SSLHandshakeException

class ApiService : RemoteDataSource {
    companion object {
        const val TAG = "ApiService"
    }
    private val parser = ApiParser()

    override suspend fun fetchHoldings(): ApiResult<List<UserHolding>> = withContext(Dispatchers.IO) {
        return@withContext try {

            val jsonString = makeApiCall()

            val holdingsDtoList = parser.parseHoldingsJsonWithDebug(jsonString)

            val domainHoldings = holdingsDtoList.map { dto ->
                UserHolding(
                    symbol = dto.symbol,
                    quantity = dto.quantity,
                    ltp = dto.ltp,
                    avgPrice = dto.avgPrice,
                    close = dto.close
                )
            }

            Log.d(TAG,"Successfully converted to domain models: ${domainHoldings.size} holdings")
            ApiResult.Success(domainHoldings)

        } catch (e: Exception) {
            Log.e("error",  "API call failed")
            val appError = ErrorMapper.mapExceptionToAppError(e)
            val userMessage = ErrorMapper.mapToUserMessage(appError)
            ApiResult.Failure("$userMessage - ${e.message}")
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

            Log.d(TAG,"Connecting to: ${AppConstants.API_BASE_URL}")

            val responseCode = connection.responseCode
            Log.d(TAG,"HTTP Response Code: $responseCode")

            if (responseCode == HttpURLConnection.HTTP_OK) {
                val response = readResponseSafely(connection)
                Log.d( TAG,"Response received, length: ${response.length}")
                return response
            } else {
                val errorStream = connection.errorStream
                val errorResponse = errorStream?.bufferedReader()?.use { it.readText() } ?: "No error body"
                Log.e("error", "HTTP Error $responseCode: $errorResponse")
                throw when (responseCode) {
                    in 400..499 -> Exception("Client error: $responseCode - $errorResponse")
                    in 500..599 -> Exception("Server error: $responseCode - $errorResponse")
                    else -> Exception("HTTP error: $responseCode - $errorResponse")
                }
            }
        } catch (e: SocketTimeoutException) {
            e.printStackTrace()
            Log.e("error", "Connection timeout")
            throw Exception("Connection timeout - server took too long to respond")
        } catch (e: SSLHandshakeException) {
            e.printStackTrace()
            Log.e("error", "SSL Handshake failed")
            throw Exception("Security error - SSL handshake failed")
        } catch (e: SecurityException) {
            e.printStackTrace()
            Log.e("error", "Network security exception")
            throw Exception("Network permission denied")
        } catch (e: Exception) {
            Log.e("error", "Network error")
            throw Exception("Network error: ${e.message}")
        } finally {
            connection?.disconnect()
            Log.d("","Connection closed")
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
            Log.e("error", "Error reading response")
            throw Exception("Failed to read response: ${e.message}")
        }
    }
}