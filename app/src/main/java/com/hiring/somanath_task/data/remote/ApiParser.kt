package com.hiring.somanath_task.data.remote

import android.util.Log
import com.hiring.somanath_task.data.local.database.entity.UserHoldingDto
import org.json.JSONObject
import org.json.JSONException


class ApiParser {
companion object {
    const val TAG = "ApiParser"
}
    fun parseHoldingsJson(jsonString: String): List<UserHoldingDto> {
        return try {
            Log.d(TAG,"Starting JSON parsing...")

            if (jsonString.isBlank()) {
                throw IllegalArgumentException("Empty JSON response")
            }

            val jsonObject = JSONObject(jsonString)
            Log.d(TAG,"Root JSON object created")

            if (!jsonObject.has("data")) {
                throw JSONException("Missing 'data' field in response")
            }

            val dataObject = jsonObject.getJSONObject("data")
            Log.d(TAG,"Data object extracted")

            if (!dataObject.has("userHolding")) {
                throw JSONException("Missing 'userHolding' field in data")
            }

            val dataArray = dataObject.getJSONArray("userHolding")
            Log.d(TAG,"UserHolding array found with length: ${dataArray.length()}")

            val holdingsList = mutableListOf<UserHoldingDto>()

            for (i in 0 until dataArray.length()) {
                try {
                    val holdingJson = dataArray.getJSONObject(i)
                    val holding = parseHoldingObject(holdingJson, i)
                    holdingsList.add(holding)
                    Log.v(TAG,"Successfully parsed holding $i: ${holding.symbol}")
                } catch (e: Exception) {
                    e.printStackTrace()
                    Log.e(TAG, "Failed to parse holding at index $i")
                }
            }

            if (holdingsList.isEmpty()) {
                throw IllegalArgumentException("No valid holdings found in response")
            }

            Log.d(TAG,"Successfully parsed ${holdingsList.size} holdings")
            holdingsList
        } catch (e: JSONException) {
            e.printStackTrace()
            Log.e(TAG, "JSON parsing error")
            throw IllegalArgumentException("Invalid JSON format: ${e.message}")
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e(TAG, "Unexpected parsing error")
            throw IllegalArgumentException("Failed to parse holdings: ${e.message}")
        }
    }

    private fun parseHoldingObject(holdingJson: JSONObject, index: Int): UserHoldingDto {
        return try {
            val requiredFields = listOf("symbol", "quantity", "ltp", "avgPrice", "close")
            requiredFields.forEach { field ->
                if (!holdingJson.has(field)) {
                    throw JSONException("Missing required field '$field' at index $index")
                }
            }

            val symbol = holdingJson.getString("symbol").takeIf { it.isNotBlank() }
                ?: throw JSONException("Invalid symbol at index $index")

            val quantity = holdingJson.getInt("quantity").takeIf { it >= 0 }
                ?: throw JSONException("Invalid quantity at index $index")

            val ltp = holdingJson.getDouble("ltp").takeIf { it >= 0 }
                ?: throw JSONException("Invalid LTP at index $index")

            val avgPrice = holdingJson.getDouble("avgPrice").takeIf { it >= 0 }
                ?: throw JSONException("Invalid avgPrice at index $index")

            val close = holdingJson.getDouble("close").takeIf { it >= 0 }
                ?: throw JSONException("Invalid close at index $index")

            UserHoldingDto(
                symbol = symbol,
                quantity = quantity,
                ltp = ltp,
                avgPrice = avgPrice,
                close = close
            )
        } catch (e: JSONException) {
            e.printStackTrace()
            Log.e(TAG, "Failed to parse holding object at index $index")
            throw e
        } catch (e: NumberFormatException) {
            e.printStackTrace()
            Log.e(TAG, "Number format error in holding at index $index")
            throw JSONException("Invalid number format at index $index: ${e.message}")
        }
    }

    fun parseHoldingsJsonWithDebug(jsonString: String): List<UserHoldingDto> {
        Log.d(TAG,"Raw JSON response: ${jsonString.take(500)}...")

        return try {
            val holdings = parseHoldingsJson(jsonString)
            Log.d(TAG,"Parsing successful. Holdings: ${holdings.size}")
            holdings.forEachIndexed { index, holding ->
                Log.d(TAG,"Holding $index: $holding")
            }
            holdings
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e(TAG, "Parsing failed with detailed debug info")
            throw e
        }
    }
}