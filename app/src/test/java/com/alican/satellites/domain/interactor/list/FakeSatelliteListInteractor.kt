package com.alican.satellites.domain.interactor.list

import com.alican.satellites.data.model.Satellite
import com.alican.satellites.domain.interactor.SatelliteListInteractor
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine

class FakeSatelliteListInteractor : SatelliteListInteractor {
    
    // Test data
    private val testSatellites = listOf(
        Satellite(id = 1, name = "Starship-1", active = true),
        Satellite(id = 2, name = "Dragon", active = false),
        Satellite(id = 3, name = "Falcon 9", active = true),
        Satellite(id = 4, name = "Starship-10", active = true),
        Satellite(id = 5, name = "Crew Dragon", active = false),
        Satellite(id = 6, name = "Falcon Heavy", active = true)
    )
    
    // State flows for reactive data
    private val _searchQuery = MutableStateFlow("")
    private val _allSatellites = MutableStateFlow<List<Satellite>>(emptyList())
    
    // Control flags for testing
    var shouldThrowException = false
    var exceptionToThrow: Exception = RuntimeException("Test exception")
    var delayMs = 0L
    var customSatellites2: List<Satellite>? = null
    
    override suspend fun getSatellites(): Result<List<Satellite>> {
        if (delayMs > 0) delay(delayMs)
        
        return try {
            if (shouldThrowException) {
                Result.failure(exceptionToThrow)
            } else {
                val satellites = customSatellites2 ?: testSatellites
                Result.success(satellites)
            }
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
            _searchQuery,
            _allSatellites
        ) { query, satellites ->
            searchSatellites(satellites, query)
        }
    }
    
    override fun updateSatellitesList(satellites: List<Satellite>) {
        _allSatellites.value = satellites
    }
    
    // Helper methods for testing
    fun reset() {
        shouldThrowException = false
        exceptionToThrow = RuntimeException("Test exception")
        delayMs = 0L
        customSatellites2 = null
        _searchQuery.value = ""
        _allSatellites.value = emptyList()
    }
    
    fun simulateNetworkError() {
        shouldThrowException = true
        exceptionToThrow = RuntimeException("Network error")
    }
    
    fun simulateEmptyResult() {
        customSatellites2 = emptyList()
    }
    
    fun setCustomSatellites(satellites: List<Satellite>) {
        customSatellites2 = satellites
    }
    
    // Getters for testing internal state
    fun getCurrentSearchQuery(): String = _searchQuery.value
    fun getCurrentSatellitesList(): List<Satellite> = _allSatellites.value
}