package com.alican.satellites.data.repository

import com.alican.satellites.data.model.Position
import com.alican.satellites.data.model.Satellite
import com.alican.satellites.data.model.SatelliteDetail
import com.alican.satellites.data.model.SatellitePosition
import com.alican.satellites.domain.repository.SatelliteRepository
import kotlinx.coroutines.delay

class FakeSatelliteRepository : SatelliteRepository {

    private val satellites = listOf(
        Satellite(id = 1, active = true, name = "Starship-1"),
        Satellite(id = 2, active = false, name = "Dragon-1"),
        Satellite(id = 3, active = true, name = "Falcon-1")
    )

    private val satelliteDetails = listOf(
        SatelliteDetail(
            id = 1,
            cost_per_launch = 7200000,
            first_flight = "2006-03-24",
            height = 22,
            mass = 30146
        ),
        SatelliteDetail(
            id = 2,
            cost_per_launch = 5400000,
            first_flight = "2010-06-04",
            height = 9,
            mass = 6000
        ),
        SatelliteDetail(
            id = 3,
            cost_per_launch = 9750000,
            first_flight = "2008-09-28",
            height = 18,
            mass = 13150
        )
    )

    private val satellitePositions = listOf(
        SatellitePosition(
            id = "1",
            positions = listOf(
                Position(posX = 0.864328541, posY = 0.646450811),
                Position(posX = 0.874328541, posY = 0.656450811)
            )
        ),
        SatellitePosition(
            id = "2",
            positions = listOf(
                Position(posX = 0.323846645, posY = 0.492872551),
                Position(posX = 0.333846645, posY = 0.502872551)
            )
        )
    )

    private val cachedDetails = mutableMapOf<Int, SatelliteDetail>()

    // For testing delay scenarios
    var shouldDelay: Boolean = false
    var delayTime: Long = 1000L

    // For testing error scenarios
    var shouldReturnError: Boolean = false

    override suspend fun getSatellites(): List<Satellite> {
        if (shouldDelay) delay(delayTime)
        return if (shouldReturnError) emptyList() else satellites
    }

    override suspend fun getSatelliteDetail(satelliteId: Int): SatelliteDetail? {
        if (shouldDelay) delay(delayTime)
        if (shouldReturnError) return null

        // Check cache first (simulate caching behavior)
        cachedDetails[satelliteId]?.let { return it }

        // Find detail and cache it
        val detail = satelliteDetails.find { it.id == satelliteId }
        detail?.let { cachedDetails[satelliteId] = it }

        return detail
    }

    override suspend fun getSatellitePositions(): List<SatellitePosition> {
        if (shouldDelay) delay(delayTime)
        return if (shouldReturnError) emptyList() else satellitePositions
    }

    // Helper methods for testing
    fun clearCache() {
        cachedDetails.clear()
    }

    fun isCached(satelliteId: Int): Boolean {
        return cachedDetails.containsKey(satelliteId)
    }
}