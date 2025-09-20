package com.alican.satellites.domain.interactor.detail

import com.alican.satellites.domain.interactor.SatelliteNotFoundException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*

class SatelliteDetailInteractorTest {

    private lateinit var fakeInteractor: FakeSatelliteDetailInteractor

    @Before
    fun setup() {
        fakeInteractor = FakeSatelliteDetailInteractor()
        fakeInteractor.reset() // Ensure clean state before each test
    }

    @Test
    fun `getSatelliteCompleteData returns success when satellite exists`() = runTest {
        // Given
        val satelliteId = 1

        // When
        val result = fakeInteractor.getSatelliteCompleteData(satelliteId)

        // Then
        assertTrue(result.isSuccess)
        result.getOrNull()?.let { completeData ->
            assertEquals(satelliteId, completeData.satellite.id)
            assertEquals("Starship-1", completeData.satellite.name)
            assertTrue(completeData.satellite.active)
            assertEquals(satelliteId, completeData.satelliteDetail?.id)
            assertEquals(7200000L, completeData.satelliteDetail?.cost_per_launch)
        } ?: fail("Expected success result but got null")
    }

    @Test
    fun `getSatelliteCompleteData returns failure when satellite not found`() = runTest {
        // Given
        val nonExistentSatelliteId = 999

        // When
        val result = fakeInteractor.getSatelliteCompleteData(nonExistentSatelliteId)

        // Then
        assertTrue(result.isFailure)
        result.exceptionOrNull()?.let { exception ->
            assertTrue(exception is SatelliteNotFoundException)
            assertTrue(exception.message?.contains("999") == true)
        } ?: fail("Expected SatelliteNotFoundException but got null")
    }

    @Test
    fun `getSatelliteCompleteData returns failure when exception occurs`() = runTest {
        // Given
        fakeInteractor.simulateNetworkError()

        // When
        val result = fakeInteractor.getSatelliteCompleteData(1)

        // Then
        assertTrue(result.isFailure)
        result.exceptionOrNull()?.let { exception ->
            assertEquals("Network error", exception.message)
        } ?: fail("Expected network error exception but got null")
    }

    @Test
    fun `observePositionUpdates emits positions successfully`() = runTest {
        // Given
        val satelliteId = 1

        // When
        val positions = fakeInteractor.observePositionUpdates(satelliteId)
            .take(3)
            .toList()

        // Then
        assertEquals(3, positions.size)
        positions.forEach { result ->
            assertTrue("Expected success result", result.isSuccess)
            assertNotNull("Expected non-null position", result.getOrNull())
        }

        // Check first position
        val firstPosition = positions[0].getOrNull()
        assertNotNull("First position should not be null", firstPosition)
        assertEquals(0.864328541, firstPosition?.posX ?: 0.0, 0.0001)
        assertEquals(0.646450811, firstPosition?.posY ?: 0.0, 0.0001)
    }

    @Test
    fun `observePositionUpdates emits null when no positions available`() = runTest {
        // Given
        val nonExistentSatelliteId = 999

        // When
        val result = fakeInteractor.observePositionUpdates(nonExistentSatelliteId).first()

        // Then
        assertTrue("Expected success result", result.isSuccess)
        assertNull("Expected null position for non-existent satellite", result.getOrNull())
    }

    @Test
    fun `observePositionUpdates emits null when positions are empty`() = runTest {
        // Given
        fakeInteractor.shouldReturnEmptyPositions = true

        // When
        val result = fakeInteractor.observePositionUpdates(1).first()

        // Then
        assertTrue("Expected success result", result.isSuccess)
        assertNull("Expected null position when empty positions flag is set", result.getOrNull())
    }

    @Test
    fun `observePositionUpdates emits failure when exception occurs`() = runTest {
        // Given
        fakeInteractor.simulateNetworkError()

        // When
        val result = fakeInteractor.observePositionUpdates(1).first()

        // Then
        assertTrue("Expected failure result", result.isFailure)
        result.exceptionOrNull()?.let { exception ->
            assertEquals("Network error", exception.message)
        } ?: fail("Expected network error exception but got null")
    }

    @Test
    fun `observePositionUpdates cycles through positions correctly`() = runTest {
        // Given
        val satelliteId = 1

        // When
        val positions = fakeInteractor.observePositionUpdates(satelliteId)
            .take(6) // Take 6 positions to test cycling (we have 3 test positions)
            .toList()

        // Then
        assertEquals(6, positions.size)

        // Check that positions cycle correctly
        val firstPosition = positions[0].getOrNull()
        val fourthPosition = positions[3].getOrNull() // Should be same as first due to cycling

        assertNotNull("First position should not be null", firstPosition)
        assertNotNull("Fourth position should not be null", fourthPosition)
        assertEquals("X positions should match due to cycling", firstPosition?.posX, fourthPosition?.posX)
        assertEquals("Y positions should match due to cycling", firstPosition?.posY, fourthPosition?.posY)
    }

    @Test
    fun `reset method resets all test flags`() = runTest {
        // Given
        fakeInteractor.shouldThrowException = true
        fakeInteractor.shouldReturnEmptyPositions = true
        fakeInteractor.delayMs = 1000L

        // When
        fakeInteractor.reset()

        // Then
        assertFalse(fakeInteractor.shouldThrowException)
        assertFalse(fakeInteractor.shouldReturnEmptyPositions)
        assertEquals(0L, fakeInteractor.delayMs)
    }

    @Test
    fun `multiple satellite IDs work correctly`() = runTest {
        // Test satellite 1
        val result1 = fakeInteractor.getSatelliteCompleteData(1)
        assertTrue(result1.isSuccess)
        assertEquals("Starship-1", result1.getOrNull()?.satellite?.name)

        // Test satellite 2
        val result2 = fakeInteractor.getSatelliteCompleteData(2)
        assertTrue(result2.isSuccess)
        assertEquals("Dragon", result2.getOrNull()?.satellite?.name)
        assertFalse(result2.getOrNull()?.satellite?.active == true)
    }
}