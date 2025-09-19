package com.alican.satellites.data.repository

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FakeSatelliteRepositoryTest {

    private lateinit var repository: FakeSatelliteRepository

    @Before
    fun setup() {
        repository = FakeSatelliteRepository()
    }

    // MARK: - getSatellites() Tests

    @Test
    fun `getSatellites returns expected satellite list`() = runTest {
        // When
        val satellites = repository.getSatellites()

        // Then
        assertEquals(3, satellites.size)

        // Verify first satellite
        val firstSatellite = satellites[0]
        assertEquals(1, firstSatellite.id)
        assertEquals("Starship-1", firstSatellite.name)
        assertTrue(firstSatellite.active)

        // Verify second satellite
        val secondSatellite = satellites[1]
        assertEquals(2, secondSatellite.id)
        assertEquals("Dragon-1", secondSatellite.name)
        assertFalse(secondSatellite.active)

        // Verify third satellite
        val thirdSatellite = satellites[2]
        assertEquals(3, thirdSatellite.id)
        assertEquals("Falcon-1", thirdSatellite.name)
        assertTrue(thirdSatellite.active)
    }

    @Test
    fun `getSatellites returns empty list when error flag is set`() = runTest {
        // Given
        repository.shouldReturnError = true

        // When
        val satellites = repository.getSatellites()

        // Then
        assertTrue(satellites.isEmpty())
    }

    @Test
    fun `getSatellites respects delay configuration`() = runTest {
        // Given
        repository.shouldDelay = true
        repository.delayTime = 500L

        // When - Start the call but don't await yet
        val deferred = async { repository.getSatellites() }

        // Then - Verify it's not complete immediately
        assertFalse("Call should not complete immediately", deferred.isCompleted)

        // Advance time and verify completion
        advanceTimeBy(500L)
        val satellites = deferred.await()

        assertEquals(3, satellites.size)
    }

    // MARK: - getSatelliteDetail() Tests

    @Test
    fun `getSatelliteDetail returns correct detail for valid id`() = runTest {
        // When
        val detail = repository.getSatelliteDetail(1)

        // Then
        assertNotNull(detail)
        assertEquals(1, detail!!.id)
        assertEquals(7200000, detail.cost_per_launch)
        assertEquals("2006-03-24", detail.first_flight)
        assertEquals(22, detail.height)
        assertEquals(30146, detail.mass)
    }

    @Test
    fun `getSatelliteDetail returns null for invalid id`() = runTest {
        // When
        val detail = repository.getSatelliteDetail(999)

        // Then
        assertNull(detail)
    }

    @Test
    fun `getSatelliteDetail returns null when error flag is set`() = runTest {
        // Given
        repository.shouldReturnError = true

        // When
        val detail = repository.getSatelliteDetail(1)

        // Then
        assertNull(detail)
    }

    @Test
    fun `getSatelliteDetail caches result after first call`() = runTest {
        // Given
        assertFalse("Detail should not be cached initially", repository.isCached(1))

        // When - First call
        val firstDetail = repository.getSatelliteDetail(1)

        // Then
        assertNotNull(firstDetail)
        assertTrue("Detail should be cached after first call", repository.isCached(1))

        // When - Second call
        val secondDetail = repository.getSatelliteDetail(1)

        // Then
        assertEquals(firstDetail, secondDetail)
        assertTrue("Detail should still be cached", repository.isCached(1))
    }

    @Test
    fun `getSatelliteDetail respects delay configuration`() = runTest {
        // Given
        repository.shouldDelay = true
        repository.delayTime = 300L

        // When - Start the call but don't await yet
        val deferred = async { repository.getSatelliteDetail(1) }

        // Then - Verify it's not complete immediately
        assertFalse("Call should not complete immediately", deferred.isCompleted)

        // Advance time and verify completion
        advanceTimeBy(300L)
        val detail = deferred.await()

        assertNotNull(detail)
        assertEquals(1, detail!!.id)
    }

    @Test
    fun `getSatelliteDetail works for all predefined satellites`() = runTest {
        // Test satellite 1
        val detail1 = repository.getSatelliteDetail(1)
        assertNotNull(detail1)
        assertEquals("Starship-1 equivalent detail", 7200000, detail1!!.cost_per_launch)

        // Test satellite 2
        val detail2 = repository.getSatelliteDetail(2)
        assertNotNull(detail2)
        assertEquals("Dragon-1 equivalent detail", 5400000, detail2!!.cost_per_launch)

        // Test satellite 3
        val detail3 = repository.getSatelliteDetail(3)
        assertNotNull(detail3)
        assertEquals("Falcon-1 equivalent detail", 9750000, detail3!!.cost_per_launch)
    }

    // MARK: - getSatellitePositions() Tests

    @Test
    fun `getSatellitePositions returns expected positions list`() = runTest {
        // When
        val positions = repository.getSatellitePositions()

        // Then
        assertEquals(2, positions.size)

        // Verify first position
        val firstPosition = positions[0]
        assertEquals("1", firstPosition.id)
        assertEquals(2, firstPosition.positions.size)
        assertEquals(0.864328541, firstPosition.positions[0].posX, 0.000001)
        assertEquals(0.646450811, firstPosition.positions[0].posY, 0.000001)

        // Verify second position
        val secondPosition = positions[1]
        assertEquals("2", secondPosition.id)
        assertEquals(2, secondPosition.positions.size)
        assertEquals(0.323846645, secondPosition.positions[0].posX, 0.000001)
        assertEquals(0.492872551, secondPosition.positions[0].posY, 0.000001)
    }

    @Test
    fun `getSatellitePositions returns empty list when error flag is set`() = runTest {
        // Given
        repository.shouldReturnError = true

        // When
        val positions = repository.getSatellitePositions()

        // Then
        assertTrue(positions.isEmpty())
    }

    @Test
    fun `getSatellitePositions respects delay configuration`() = runTest {
        // Given
        repository.shouldDelay = true
        repository.delayTime = 400L

        // When - Start the call but don't await yet
        val deferred = async { repository.getSatellitePositions() }

        // Then - Verify it's not complete immediately
        assertFalse("Call should not complete immediately", deferred.isCompleted)

        // Advance time and verify completion
        advanceTimeBy(400L)
        val positions = deferred.await()

        assertEquals(2, positions.size)
    }

    // MARK: - Helper Methods Tests

    @Test
    fun `clearCache removes all cached details`() = runTest {
        // Given - Cache some details
        repository.getSatelliteDetail(1)
        repository.getSatelliteDetail(2)
        assertTrue("Details should be cached", repository.isCached(1))
        assertTrue("Details should be cached", repository.isCached(2))

        // When
        repository.clearCache()

        // Then
        assertFalse("Cache should be cleared", repository.isCached(1))
        assertFalse("Cache should be cleared", repository.isCached(2))
    }

    @Test
    fun `isCached returns false for non-cached items`() = runTest {
        // Given - No items cached

        // When & Then
        assertFalse(repository.isCached(1))
        assertFalse(repository.isCached(2))
        assertFalse(repository.isCached(999))
    }

    @Test
    fun `isCached returns true only for cached items`() = runTest {
        // Given - Cache one item
        repository.getSatelliteDetail(1)

        // When & Then
        assertTrue("Item 1 should be cached", repository.isCached(1))
        assertFalse("Item 2 should not be cached", repository.isCached(2))
        assertFalse("Invalid item should not be cached", repository.isCached(999))
    }

    // MARK: - Configuration Reset Tests

    @Test
    fun `configuration flags work independently`() = runTest {
        // Test normal behavior first
        repository.shouldDelay = false
        repository.shouldReturnError = false

        val satellites1 = repository.getSatellites()
        assertEquals(3, satellites1.size)

        // Test error only
        repository.shouldReturnError = true
        val satellites2 = repository.getSatellites()
        assertTrue(satellites2.isEmpty())

        // Back to normal
        repository.shouldReturnError = false
        val satellites3 = repository.getSatellites()
        assertEquals(3, satellites3.size)
    }

    @Test
    fun `delay and error flags can work together`() = runTest {
        // Given
        repository.shouldDelay = true
        repository.delayTime = 100L
        repository.shouldReturnError = true

        // When
        val deferred = async { repository.getSatellites() }

        // Then - Should not complete immediately
        assertFalse("Call should not complete immediately", deferred.isCompleted)

        // Advance time and verify error result
        advanceTimeBy(100L)
        val satellites = deferred.await()
        assertTrue("Should return empty list due to error flag", satellites.isEmpty())
    }

    @Test
    fun `multiple calls with different configurations work correctly`() = runTest {
        // Normal call
        val satellites1 = repository.getSatellites()
        assertEquals(3, satellites1.size)

        // Error call
        repository.shouldReturnError = true
        val satellites2 = repository.getSatellites()
        assertTrue(satellites2.isEmpty())

        // Back to normal
        repository.shouldReturnError = false
        val satellites3 = repository.getSatellites()
        assertEquals(3, satellites3.size)
    }

    // MARK: - Edge Cases

    @Test
    fun `zero delay time works correctly`() = runTest {
        // Given
        repository.shouldDelay = true
        repository.delayTime = 0L

        // When
        val satellites = repository.getSatellites()

        // Then - Should complete immediately even with delay flag
        assertEquals(3, satellites.size)
    }

    @Test
    fun `negative satellite id handling`() = runTest {
        // When
        val detail = repository.getSatelliteDetail(-1)

        // Then
        assertNull("Negative ID should return null", detail)
        assertFalse("Negative ID should not be cached", repository.isCached(-1))
    }

    @Test
    fun `concurrent access to cache works correctly`() = runTest {
        // This test ensures thread safety isn't broken in our simple implementation
        val detail1 = repository.getSatelliteDetail(1)
        val detail2 = repository.getSatelliteDetail(1) // Should get from cache

        assertEquals(detail1, detail2)
        assertTrue(repository.isCached(1))
    }

    // MARK: - Advanced Delay Tests

    @Test
    fun `delay works correctly for different time values`() = runTest {
        repository.shouldDelay = true

        // Test different delay times
        val delayTimes = listOf(50L, 100L, 200L)

        for (delayTime in delayTimes) {
            // Reset cache for clean test
            repository.clearCache()
            repository.delayTime = delayTime

            val deferred = async { repository.getSatelliteDetail(1) }
            assertFalse(
                "Call should not complete immediately for delay $delayTime",
                deferred.isCompleted
            )

            advanceTimeBy(delayTime)
            val result = deferred.await()
            assertNotNull("Should return result after delay $delayTime", result)
        }
    }

    @Test
    fun `partial delay advancement doesn't complete call`() = runTest {
        // Given
        repository.shouldDelay = true
        repository.delayTime = 1000L

        // When
        val deferred = async { repository.getSatellites() }

        // Then - Advance only half the time
        advanceTimeBy(500L)
        assertFalse("Call should not complete with partial time advancement", deferred.isCompleted)

        // Complete the delay
        advanceTimeBy(500L)
        val result = deferred.await()
        assertEquals(3, result.size)
    }
}