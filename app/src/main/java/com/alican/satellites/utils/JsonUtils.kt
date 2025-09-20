package com.alican.satellites.utils

import android.content.Context
import com.alican.satellites.extensions.loadJSONFromAssets
import kotlinx.serialization.json.Json
import java.io.IOException

/**
 * A utility object for handling JSON operations, specifically for loading and parsing
 * JSON files from the app's assets directory.
 */
object JsonUtils {

    /**
     * Configured Json instance from kotlinx.serialization to be reused.
     * - ignoreUnknownKeys: Prevents crashing if the JSON has fields not present in the data class.
     * - isLenient: Allows for parsing of non-standard JSON, if needed.
     */
    val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    /**
     * Loads a JSON file from the assets folder and decodes it into a specified type <T>.
     *
     * This function is 'inline' and uses a 'reified' type parameter <T>, which allows it
     * to determine the data type to decode into at runtime without passing the class explicitly.
     *
     * @param T The target data class type for deserialization.
     * @param context The application context to access assets.
     * @param fileName The name of the JSON file in the assets folder (e.g., "satellites.json").
     * @return The parsed object of type T, or null if the file is not found or a parsing error occurs.
     */
    inline fun <reified T> loadAndParseAsset(context: Context, fileName: String): T? {
        return try {
            // 1. Load the JSON string from the asset file
            val jsonString = context.loadJSONFromAssets(fileName = fileName)

            // 2. Decode the JSON string into the provided generic type T
            json.decodeFromString<T>(jsonString)
        } catch (e: IOException) {
            // Handle file not found or read errors
            e.printStackTrace()
            null
        } catch (e: kotlinx.serialization.SerializationException) {
            // Handle JSON parsing errors
            e.printStackTrace()
            null
        }
    }
}