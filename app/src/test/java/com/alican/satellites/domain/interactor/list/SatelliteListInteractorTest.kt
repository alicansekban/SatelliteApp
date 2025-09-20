
package com.alican.satellites.domain.interactor.list

import com.alican.satellites.data.model.Satellite
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*

class SatelliteListInteractorTest {
    
    private lateinit var fakeInteractor: FakeSatelliteListInteractor
    
    @Before
    fun setup() {
        fakeInteractor = FakeSatelliteListInteractor()
        fakeInteractor.reset() // Ensure clean state before each test
    }
    
    @Test
    fun `getSatellites returns success with satellite list`() = runTest {
        // When
        val result = fakeInteractor.getSatellites()
        
        // Then
        assertTrue("Expected success result", result.isSuccess)
        result.getOrNull()?.let { satellites ->
            assertEquals(6, satellites.size)
            assertEquals("Starship-1", satellites[0].name)
            assertEquals("Dragon", satellites[1].name)
            assertTrue(satellites[0].active)
            assertFalse(satellites[1].active)
        } ?: fail("Expected satellite list but got null")
    }
    
    @Test
    fun `getSatellites returns failure when exception occurs`() = runTest {
        // Given
        fakeInteractor.simulateNetworkError()
        
        // When
        val result = fakeInteractor.getSatellites()
        
        // Then
        assertTrue("Expected failure result", result.isFailure)
        result.exceptionOrNull()?.let { exception ->
            assertEquals("Network error", exception.message)
        } ?: fail("Expected network error exception but got null")
    }
    
    @Test
    fun `getSatellites returns empty list when no satellites available`() = runTest {
        // Given
        fakeInteractor.simulateEmptyResult()
        
        // When
        val result = fakeInteractor.getSatellites()
        
        // Then
        assertTrue("Expected success result", result.isSuccess)
        result.getOrNull()?.let { satellites ->
            assertTrue("Expected empty list", satellites.isEmpty())
        } ?: fail("Expected empty list but got null")
    }
    
    @Test
    fun `searchSatellites returns all satellites when query is blank`() = runTest {
        // Given
        val satellites = listOf(
            Satellite(id = 1, name = "Starship-1", active = true),
            Satellite(id = 2, name = "Dragon", active = false)
        )
        
        // When
        val result = fakeInteractor.searchSatellites(satellites, "")
        
        // Then
        assertEquals(2, result.size)
        assertEquals(satellites, result)
    }
    
    @Test
    fun `searchSatellites filters satellites by name case insensitive`() = runTest {
        // Given
        val satellites = listOf(
            Satellite(id = 1, name = "Starship-1", active = true),
            Satellite(id = 2, name = "Dragon", active = false),
            Satellite(id = 3, name = "starship-10", active = true)
        )
        
        // When
        val result = fakeInteractor.searchSatellites(satellites, "starship")
        
        // Then
        assertEquals(2, result.size)
        assertTrue("Should contain Starship-1", result.any { it.name == "Starship-1" })
        assertTrue("Should contain starship-10", result.any { it.name == "starship-10" })
        assertFalse("Should not contain Dragon", result.any { it.name == "Dragon" })
    }
    
    @Test
    fun `searchSatellites returns empty list when no matches found`() = runTest {
        // Given
        val satellites = listOf(
            Satellite(id = 1, name = "Starship-1", active = true),
            Satellite(id = 2, name = "Dragon", active = false)
        )
        
        // When
        val result = fakeInteractor.searchSatellites(satellites, "Tesla")
        
        // Then
        assertTrue("Expected empty result for non-matching query", result.isEmpty())
    }
    
    @Test
    fun `updateSearchQuery updates search query state`() = runTest {
        // Given
        val query = "Starship"
        
        // When
        fakeInteractor.updateSearchQuery(query)
        
        // Then
        val observedQuery = fakeInteractor.observeSearchQuery().first()
        assertEquals(query, observedQuery)
        assertEquals(query, fakeInteractor.getCurrentSearchQuery())
    }
    
    @Test
    fun `updateSatellitesList updates internal satellite list`() = runTest {
        // Given
        val satellites = listOf(
            Satellite(id = 1, name = "Test Satellite", active = true)
        )
        
        // When
        fakeInteractor.updateSatellitesList(satellites)
        
        // Then
        assertEquals(satellites, fakeInteractor.getCurrentSatellitesList())
    }
    
    @Test
    fun `getFilteredSatellites emits filtered satellites based on search query`() = runTest {
        // Given
        val satellites = listOf(
            Satellite(id = 1, name = "Starship-1", active = true),
            Satellite(id = 2, name = "Dragon", active = false),
            Satellite(id = 3, name = "Starship-10", active = true)
        )
        fakeInteractor.updateSatellitesList(satellites)
        
        // When
        fakeInteractor.updateSearchQuery("Starship")
        val filteredSatellites = fakeInteractor.getFilteredSatellites().first()
        
        // Then
        assertEquals(2, filteredSatellites.size)
        assertTrue("Should contain Starship-1", filteredSatellites.any { it.name == "Starship-1" })
        assertTrue("Should contain Starship-10", filteredSatellites.any { it.name == "Starship-10" })
    }
    
    @Test
    fun `getFilteredSatellites emits all satellites when search query is empty`() = runTest {
        // Given
        val satellites = listOf(
            Satellite(id = 1, name = "Starship-1", active = true),
            Satellite(id = 2, name = "Dragon", active = false)
        )
        fakeInteractor.updateSatellitesList(satellites)
        
        // When
        fakeInteractor.updateSearchQuery("")
        val filteredSatellites = fakeInteractor.getFilteredSatellites().first()
        
        // Then
        assertEquals(2, filteredSatellites.size)
        assertEquals(satellites, filteredSatellites)
    }
    
    @Test
    fun `getFilteredSatellites reacts to satellite list changes`() = runTest {
        // Given
        val initialSatellites = listOf(
            Satellite(id = 1, name = "Starship-1", active = true)
        )
        val updatedSatellites = listOf(
            Satellite(id = 1, name = "Starship-1", active = true),
            Satellite(id = 2, name = "Dragon", active = false)
        )
        
        fakeInteractor.updateSatellitesList(initialSatellites)
        fakeInteractor.updateSearchQuery("")
        
        // When
        val initialFiltered = fakeInteractor.getFilteredSatellites().first()
        fakeInteractor.updateSatellitesList(updatedSatellites)
        val updatedFiltered = fakeInteractor.getFilteredSatellites().first()
        
        // Then
        assertEquals(1, initialFiltered.size)
        assertEquals(2, updatedFiltered.size)
    }
    
    @Test
    fun `getFilteredSatellites reacts to search query changes`() = runTest {
        // Given
        val satellites = listOf(
            Satellite(id = 1, name = "Starship-1", active = true),
            Satellite(id = 2, name = "Dragon", active = false),
            Satellite(id = 3, name = "Starship-10", active = true)
        )
        fakeInteractor.updateSatellitesList(satellites)
        
        // When
        fakeInteractor.updateSearchQuery("")
        val allSatellites = fakeInteractor.getFilteredSatellites().first()
        
        fakeInteractor.updateSearchQuery("Dragon")
        val dragonSatellites = fakeInteractor.getFilteredSatellites().first()
        
        fakeInteractor.updateSearchQuery("Starship")
        val starshipSatellites = fakeInteractor.getFilteredSatellites().first()
        
        // Then
        assertEquals(3, allSatellites.size)
        assertEquals(1, dragonSatellites.size)
        assertEquals("Dragon", dragonSatellites[0].name)
        assertEquals(2, starshipSatellites.size)
        assertTrue("Should contain both Starship satellites", 
            starshipSatellites.all { it.name.contains("Starship", ignoreCase = true) })
    }
    
    @Test
    fun `reset method resets all test flags and state`() = runTest {
        // Given
        fakeInteractor.shouldThrowException = true
        fakeInteractor.delayMs = 1000L
        fakeInteractor.updateSearchQuery("test")
        fakeInteractor.updateSatellitesList(listOf(Satellite(1, true, "test")))
        fakeInteractor.setCustomSatellites(listOf(Satellite(2, false, "custom")))
        
        // When
        fakeInteractor.reset()
        
        // Then
        assertFalse(fakeInteractor.shouldThrowException)
        assertEquals(0L, fakeInteractor.delayMs)
        assertEquals("", fakeInteractor.getCurrentSearchQuery())
        assertTrue(fakeInteractor.getCurrentSatellitesList().isEmpty())
        assertNull(fakeInteractor.customSatellites2)
    }
    
    @Test
    fun `setCustomSatellites allows overriding test data`() = runTest {
        // Given
        val customSatellites = listOf(
            Satellite(id = 99, name = "Custom Satellite", active = true)
        )
        
        // When
        fakeInteractor.setCustomSatellites(customSatellites)
        val result = fakeInteractor.getSatellites()
        
        // Then
        assertTrue(result.isSuccess)
        result.getOrNull()?.let { satellites ->
            assertEquals(1, satellites.size)
            assertEquals("Custom Satellite", satellites[0].name)
            assertEquals(99, satellites[0].id)
        } ?: fail("Expected custom satellites but got null")
    }
    
    @Test
    fun `flow operations work with multiple emissions`() = runTest {
        // Given
        val satellites = listOf(
            Satellite(id = 1, name = "Starship-1", active = true),
            Satellite(id = 2, name = "Dragon", active = false)
        )
        fakeInteractor.updateSatellitesList(satellites)
        
        // When & Then - collect multiple emissions
        val emissions = mutableListOf<List<Satellite>>()
        
        fakeInteractor.updateSearchQuery("")
        emissions.add(fakeInteractor.getFilteredSatellites().first())
        
        fakeInteractor.updateSearchQuery("Starship")
        emissions.add(fakeInteractor.getFilteredSatellites().first())
        
        fakeInteractor.updateSearchQuery("Dragon")
        emissions.add(fakeInteractor.getFilteredSatellites().first())
        
        // Verify emissions
        assertEquals(3, emissions.size)
        assertEquals(2, emissions[0].size) // All satellites
        assertEquals(1, emissions[1].size) // Only Starship
        assertEquals(1, emissions[2].size) // Only Dragon
        assertEquals("Starship-1", emissions[1][0].name)
        assertEquals("Dragon", emissions[2][0].name)
    }
}