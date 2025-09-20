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
import com.alican.satellites.utils.JsonUtils
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

    override suspend fun getSatellites(): List<Satellite> = withContext(Dispatchers.IO) {
        // Single call to load and parse the asset. Provide a default empty list if it fails.
        JsonUtils.loadAndParseAsset<List<Satellite>>(context, AppConstants.SATELLITES_DATA_FILENAME) ?: emptyList()
    }

    override suspend fun getSatelliteDetail(satelliteId: Int): SatelliteDetail? =
        withContext(Dispatchers.IO) {
            // First check cache
            val cachedDetail = satelliteDetailDao.getSatelliteDetail(satelliteId)
            if (cachedDetail != null) {
                return@withContext cachedDetail.toSatelliteDetail()
            }

            // If not in cache, load from assets using the utility
            val details = JsonUtils.loadAndParseAsset<List<SatelliteDetail>>(context, AppConstants.SATELLITE_DETAIL_FILENAME)
            val detail = details?.find { it.id == satelliteId }

            // Cache the detail if found
            detail?.let {
                satelliteDetailDao.insertSatelliteDetail(it.toEntity())
            }

            detail
        }

    override suspend fun getSatellitePositions(): List<SatellitePosition> =
        withContext(Dispatchers.IO) {
            // Load and parse, then access the 'list' property.
            val response = JsonUtils.loadAndParseAsset<SatellitePositionResponse>(context, AppConstants.SATELLITE_POSITIONS_FILENAME)
            response?.list ?: emptyList()
        }
}