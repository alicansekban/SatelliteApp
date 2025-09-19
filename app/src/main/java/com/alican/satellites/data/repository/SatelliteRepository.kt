package com.alican.satellites.data.repository

import android.content.Context
import com.alican.satellites.data.local.dao.SatelliteDetailDao
import com.alican.satellites.data.local.entity.toEntity
import com.alican.satellites.data.local.entity.toSatelliteDetail
import com.alican.satellites.data.model.Satellite
import com.alican.satellites.data.model.SatelliteDetail
import com.alican.satellites.data.model.SatellitePosition
import com.alican.satellites.data.model.SatellitePositionResponse
import com.alican.satellites.extensions.loadJSONFromAssets
import com.alican.satellites.utils.AppConstants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

/**
 * created interface so we can use this interface to create fake repository for unit testing
 */
interface SatelliteRepository {
    suspend fun getSatellites(): List<Satellite>
    suspend fun getSatelliteDetail(satelliteId: Int): SatelliteDetail?
    suspend fun getSatellitePositions(): List<SatellitePosition>
}

class SatelliteRepositoryImpl(
    private val context: Context,
    private val satelliteDetailDao: SatelliteDetailDao
) : SatelliteRepository {

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    override suspend fun getSatellites(): List<Satellite> = withContext(Dispatchers.IO) {
        val jsonString = context.loadJSONFromAssets(AppConstants.SATELLITES_DATA_FILENAME)
        json.decodeFromString<List<Satellite>>(jsonString)
    }

    override suspend fun getSatelliteDetail(satelliteId: Int): SatelliteDetail? =
        withContext(Dispatchers.IO) {
            // First check cache
            val cachedDetail = satelliteDetailDao.getSatelliteDetail(satelliteId)
            if (cachedDetail != null) {
                return@withContext cachedDetail.toSatelliteDetail()
            }

            // If not in cache, load from assets
            val jsonString = context.loadJSONFromAssets(AppConstants.SATELLITE_DETAIL_FILENAME)
            val details = json.decodeFromString<List<SatelliteDetail>>(jsonString)
            val detail = details.find { it.id == satelliteId }

            // Cache the detail
            detail?.let {
                satelliteDetailDao.insertSatelliteDetail(it.toEntity())
            }

            detail
        }

    override suspend fun getSatellitePositions(): List<SatellitePosition> =
        withContext(Dispatchers.IO) {
            val jsonString = context.loadJSONFromAssets(AppConstants.SATELLITE_POSITIONS_FILENAME)
            val response = json.decodeFromString<SatellitePositionResponse>(jsonString)
            response.list
        }
}