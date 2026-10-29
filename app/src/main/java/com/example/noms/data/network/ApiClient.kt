package com.example.noms.data.network

import com.example.noms.data.model.RecipeResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

object ApiClient {
    private const val BASE_URL = "https://dummyjson.com/recipes"
    private const val CONNECT_TIMEOUT = 10000
    private const val READ_TIMEOUT = 10000

    suspend fun getRecipes(limit: Int = 10, skip: Int = 0): Result<RecipeResponse> = withContext(Dispatchers.IO) {
        try {
            val urlString = "$BASE_URL?limit=$limit&skip=$skip"
            val jsonString = executeHttpGet(urlString)
            val jsonObject = JSONObject(jsonString)
            val response = RecipeResponse.fromJsonObject(jsonObject)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun searchRecipes(query: String, limit: Int = 10, skip: Int = 0): Result<RecipeResponse> = withContext(Dispatchers.IO) {
        try {
            val encodedQuery = URLEncoder.encode(query.trim(), "UTF-8")
            val urlString = "$BASE_URL/search?q=$encodedQuery&limit=$limit&skip=$skip"
            val jsonString = executeHttpGet(urlString)
            val jsonObject = JSONObject(jsonString)
            val response = RecipeResponse.fromJsonObject(jsonObject)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun executeHttpGet(urlString: String): String {
        val url = URL(urlString)
        val connection = url.openConnection() as HttpURLConnection
        try {
            connection.apply {
                requestMethod = "GET"
                connectTimeout = CONNECT_TIMEOUT
                readTimeout = READ_TIMEOUT
                setRequestProperty("Accept", "application/json")
                doInput = true
            }

            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val reader = BufferedReader(InputStreamReader(connection.inputStream))
                val stringBuilder = StringBuilder()
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    stringBuilder.append(line)
                }
                reader.close()
                return stringBuilder.toString()
            } else {
                throw Exception("HTTP Error: $responseCode ${connection.responseMessage}")
            }
        } finally {
            connection.disconnect()
        }
    }
}
