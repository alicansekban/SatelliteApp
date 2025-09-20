package com.alican.satellites.domain.interactor

import com.alican.satellites.data.model.Satellite
import com.alican.satellites.domain.repository.SatelliteRepository
import com.alican.satellites.utils.AppConstants
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged

/**
 * Interface for managing and interacting with a list of satellites.
 * Provides methods for fetching, searching, updating, and observing satellite data.
 */
interface SatelliteListInteractor {
    suspend fun getSatellites(): Result<List<Satellite>>
    fun searchSatellites(satellites: List<Satellite>, query: String): List<Satellite>
    fun observeSearchQuery(): Flow<String>
    fun updateSearchQuery(query: String)
    fun getFilteredSatellites(): Flow<List<Satellite>>
    fun updateSatellitesList(satellites: List<Satellite>)

}

/**
 * Implementation of the SatelliteListInteractor interface responsible for managing and interacting
 * with a list of satellites. Handles operations such as fetching, searching, updating, and observing
 * satellite data.
 *
 * @property repository The SatelliteRepository instance used for accessing satellite data from a
 * data source.
 */

@OptIn(FlowPreview::class)
class SatelliteListInteractorImpl(
    private val repository: SatelliteRepository
) : SatelliteListInteractor {

    private val _searchQuery = MutableStateFlow("")
    private val _allSatellites = MutableStateFlow<List<Satellite>>(emptyList())

    override suspend fun getSatellites(): Result<List<Satellite>> {
        return try {
            val satellites = repository.getSatellites()
            Result.success(satellites)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun searchSatellites(satellites: List<Satellite>, query: String): List<Satellite> {
        return if (query.isBlank()) {
            satellites
        } else {
            satellites.filter { satellite ->
                satellite.name.contains(query, ignoreCase = true)
            }
        }
    }

    override fun observeSearchQuery(): Flow<String> = _searchQuery

    override fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    override fun getFilteredSatellites(): Flow<List<Satellite>> {
        return combine(
            _searchQuery
                .debounce(AppConstants.SEARCH_DELAY)
                .distinctUntilChanged(),
            _allSatellites
        ) { query, satellites ->
            searchSatellites(satellites, query)
        }
    }

    override fun updateSatellitesList(satellites: List<Satellite>) {
        _allSatellites.value = satellites
    }
}