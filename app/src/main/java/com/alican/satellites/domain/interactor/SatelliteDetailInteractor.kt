package com.alican.satellites.domain.interactor

import com.alican.satellites.data.model.Position
import com.alican.satellites.data.model.Satellite
import com.alican.satellites.data.model.SatelliteDetail
import com.alican.satellites.data.repository.SatelliteRepository
import com.alican.satellites.utils.AppConstants
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface SatelliteDetailInteractor {
    suspend fun getSatelliteById(satelliteId: Int): Result<Satellite>
    suspend fun getSatelliteDetail(satelliteId: Int): Result<SatelliteDetail?>
    fun observePositionUpdates(satelliteId: Int): Flow<Result<Position?>>
}

class SatelliteDetailInteractorImpl(
    private val repository: SatelliteRepository
) : SatelliteDetailInteractor {

    override suspend fun getSatelliteById(satelliteId: Int): Result<Satellite> {
        return try {
            val satellites = repository.getSatellites()
            val satellite = satellites.find { it.id == satelliteId }
                ?: return Result.failure(SatelliteNotFoundException("Satellite with id $satelliteId not found"))

            Result.success(satellite)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getSatelliteDetail(satelliteId: Int): Result<SatelliteDetail?> {
        return try {
            val detail = repository.getSatelliteDetail(satelliteId)
            Result.success(detail)
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