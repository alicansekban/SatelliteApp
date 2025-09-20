package com.alican.satellites.domain.repository

import com.alican.satellites.data.model.Satellite
import com.alican.satellites.data.model.SatelliteDetail
import com.alican.satellites.data.model.SatellitePosition

/**
 * The SatelliteRepository interface provides methods for accessing and retrieving data
 * related to satellites. It serves as an abstraction layer between the application
 * logic and the data source, ensuring a clean separation of concerns.
 *
 * Methods in this interface include:
 * - Fetching a list of satellites
 * - Retrieving detailed information about a specific satellite
 * - Obtaining position updates for satellites
 *
 * This repository is designed to handle asynchronous operations, and all methods are
 * suspend functions, allowing them to be called within coroutine scopes.
 */
interface SatelliteRepository {
    suspend fun getSatellites(): List<Satellite>
    suspend fun getSatelliteDetail(satelliteId: Int): SatelliteDetail?
    suspend fun getSatellitePositions(): List<SatellitePosition>
}
