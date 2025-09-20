
package com.alican.satellites.domain.interactor

import com.alican.satellites.data.model.Position
import com.alican.satellites.domain.model.SatelliteCompleteData
import com.alican.satellites.domain.repository.SatelliteRepository
import com.alican.satellites.utils.AppConstants
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * The SatelliteDetailInteractor is responsible for providing detailed and positional
 * information about a specific satellite.
 */

interface SatelliteDetailInteractor {
    suspend fun getSatelliteCompleteData(satelliteId: Int): Result<SatelliteCompleteData>
    fun observePositionUpdates(satelliteId: Int): Flow<Result<Position?>>
}

/**
 * Implementation of the SatelliteDetailInteractor interface, responsible for handling operations
 * related to retrieving and observing the complete data and position updates of a satellite.
 *
 * @property repository The SatelliteRepository instance used to fetch satellite data such as basic
 * information, details, and positions.
 */

class SatelliteDetailInteractorImpl(
    private val repository: SatelliteRepository
) : SatelliteDetailInteractor {

    override suspend fun getSatelliteCompleteData(satelliteId: Int): Result<SatelliteCompleteData> {
        return try {
            // Get satellite basic info
            val satellites = repository.getSatellites()
            val satellite = satellites.find { it.id == satelliteId }
                ?: return Result.failure(SatelliteNotFoundException("Satellite with id $satelliteId not found"))

            // Get satellite detail (this handles caching)
            val satelliteDetail = repository.getSatelliteDetail(satelliteId)

            val completeData = SatelliteCompleteData(
                satellite = satellite,
                satelliteDetail = satelliteDetail
            )

            Result.success(completeData)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun observePositionUpdates(satelliteId: Int): Flow<Result<Position?>> = flow {
        try {
            val satellitePositions = repository.getSatellitePositions()
            val satellitePosition = satellitePositions.find { it.id == satelliteId.toString() }

            if (satellitePosition == null || satellitePosition.positions.isEmpty()) {
                emit(Result.success(null))
                return@flow
            }

            val positions = satellitePosition.positions
            var currentIndex = 0

            while (true) {
                val position = positions[currentIndex]
                emit(Result.success(position))

                currentIndex = (currentIndex + 1) % positions.size
                delay(AppConstants.POSITION_UPDATE_INTERVAL)
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
}

class SatelliteNotFoundException(message: String) : Exception(message)