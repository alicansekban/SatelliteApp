package com.alican.satellites.domain.interactor.detail

import com.alican.satellites.data.model.Position
import com.alican.satellites.data.model.Satellite
import com.alican.satellites.data.model.SatelliteDetail
import com.alican.satellites.domain.interactor.SatelliteDetailInteractor
import com.alican.satellites.domain.interactor.SatelliteNotFoundException
import com.alican.satellites.domain.model.SatelliteCompleteData
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.isActive

class FakeSatelliteDetailInteractor : SatelliteDetailInteractor {

    // Test data
    private val testSatellites = listOf(
        Satellite(id = 1, name = "Starship-1", active = true),
        Satellite(id = 2, name = "Dragon", active = false),
        Satellite(id = 3, name = "Falcon 9", active = true)
    )

    private val testSatelliteDetails = mapOf(
        1 to SatelliteDetail(
            id = 1,
            cost_per_launch = 7200000L,
            first_flight = "2006-03-24",
            height = 22,
            mass = 30146
        ),
        2 to SatelliteDetail(
            id = 2,
            cost_per_launch = 5400000L,
            first_flight = "2010-12-08",
            height = 18,
            mass = 25000
        )
    )

    private val testPositions = mapOf(
        1 to listOf(
            Position(posX = 0.864328541, posY = 0.646450811),
            Position(posX = 0.874328541, posY = 0.656450811),
            Position(posX = 0.884328541, posY = 0.666450811)
        ),
        2 to listOf(
            Position(posX = 0.123456789, posY = 0.987654321),
            Position(posX = 0.133456789, posY = 0.997654321)
        )
    )

    // Control flags for testing
    var shouldThrowException = false
    var exceptionToThrow: Exception = RuntimeException("Test exception")
    var shouldReturnEmptyPositions = false
    var delayMs = 0L

    override suspend fun getSatelliteCompleteData(satelliteId: Int): Result<SatelliteCompleteData> {
        if (delayMs > 0) delay(delayMs)

        return try {
            if (shouldThrowException) {
                return Result.failure(exceptionToThrow)
            }

            val satellite = testSatellites.find { it.id == satelliteId }
                ?: return Result.failure(SatelliteNotFoundException("Satellite with id $satelliteId not found"))

            val satelliteDetail = testSatelliteDetails[satelliteId]
                ?: return Result.failure(RuntimeException("Detail not found for satellite $satelliteId"))

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
        if (shouldThrowException) {
            emit(Result.failure(exceptionToThrow))
            return@flow
        }

        // Handle the case when we want to simulate empty positions
        if (shouldReturnEmptyPositions) {
            emit(Result.success(null))
            return@flow
        }

        val positions = testPositions[satelliteId]

        // If satellite doesn't exist in our test data, return null
        if (positions.isNullOrEmpty()) {
            emit(Result.success(null))
            return@flow
        }

        // Emit positions in a cycle with proper cancellation handling
        var currentIndex = 0
        while (currentCoroutineContext().isActive) {
            val position = positions[currentIndex]
            emit(Result.success(position))

            currentIndex = (currentIndex + 1) % positions.size
            if (delayMs > 0) {
                delay(delayMs)
            }

            // Check if coroutine is still active before continuing
            currentCoroutineContext().ensureActive()
        }
    }

    // Helper methods for testing
    fun reset() {
        shouldThrowException = false
        shouldReturnEmptyPositions = false
        delayMs = 0L
        exceptionToThrow = RuntimeException("Test exception")
    }

    fun simulateNetworkError() {
        shouldThrowException = true
        exceptionToThrow = RuntimeException("Network error")
    }
}