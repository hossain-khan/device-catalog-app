package dev.hossain.devicecatalog.feature.statistics

import dev.hossain.devicecatalog.core.data.AndroidDeviceRepository
import dev.hossain.devicecatalog.core.testing.FakeAndroidDeviceRepository
import dev.hossain.devicecatalog.core.testing.TestDeviceFactory
import dev.hossain.devicecatalog.core.testing.TestDispatcherRule
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Example test demonstrating how to use fakes from core:testing module
 * to test repository operations without requiring a real database.
 * 
 * This follows the Now in Android testing pattern using fakes instead of mocks.
 * 
 * Note: This is a simplified example focusing on repository logic.
 * For full presenter testing with Circuit, the circuit-test library would be needed.
 */
class DeviceStatsExampleTest {
    
    @get:Rule
    val dispatcherRule = TestDispatcherRule()
    
    private lateinit var repository: FakeAndroidDeviceRepository
    
    @Before
    fun setup() {
        repository = FakeAndroidDeviceRepository()
    }
    
    @Test
    fun `repository should return empty stats for no devices`() = runTest {
        // Given - empty repository (no devices set)
        
        // When
        val stats = repository.getDeviceStats().first()
        
        // Then
        assertEquals(0, stats.totalDevices)
        assertEquals(0, stats.totalFormFactors)
        assertTrue(stats.topManufacturers.isEmpty())
    }
    
    @Test
    fun `repository should calculate correct stats for sample devices`() = runTest {
        // Given - repository with sample devices
        repository.setDevices(TestDeviceFactory.createSampleDevices())
        
        // When
        val stats = repository.getDeviceStats().first()
        
        // Then - verify basic counts
        assertEquals(3, stats.totalDevices)
        assertTrue(stats.totalFormFactors > 0)
        
        // Verify manufacturers
        assertTrue(stats.topManufacturers.isNotEmpty())
        assertEquals(3, stats.topManufacturers.size)
        
        // Verify the manufacturers are from our sample data
        val manufacturers = stats.topManufacturers.map { it.manufacturer }
        assertTrue(manufacturers.contains("Google"))
        assertTrue(manufacturers.contains("Samsung"))
        assertTrue(manufacturers.contains("OnePlus"))
    }
    
    @Test
    fun `repository should provide RAM distribution stats`() = runTest {
        // Given
        repository.setDevices(TestDeviceFactory.createSampleDevices())
        
        // When
        val stats = repository.getDeviceStats().first()
        
        // Then
        assertTrue(stats.ramDistribution.isNotEmpty())
        // Sample devices have 8GB, 6GB, and 12GB
        assertEquals(3, stats.ramDistribution.size)
    }
    
    @Test
    fun `repository should provide SDK version distribution`() = runTest {
        // Given
        repository.setDevices(TestDeviceFactory.createSampleDevices())
        
        // When
        val stats = repository.getDeviceStats().first()
        
        // Then
        assertTrue(stats.sdkVersionDistribution.isNotEmpty())
        // Sample devices all support SDK 30, 31, 32, 33
        assertTrue(stats.sdkVersionDistribution.any { it.sdkVersion == 30 })
        assertTrue(stats.sdkVersionDistribution.any { it.sdkVersion == 33 })
    }
    
    @Test
    fun `repository should provide ABI distribution`() = runTest {
        // Given
        repository.setDevices(TestDeviceFactory.createSampleDevices())
        
        // When
        val stats = repository.getDeviceStats().first()
        
        // Then
        assertTrue(stats.abiDistribution.isNotEmpty())
        // All sample devices support arm64-v8a and armeabi-v7a
        assertTrue(stats.abiDistribution.any { it.abi == "arm64-v8a" })
        assertTrue(stats.abiDistribution.any { it.abi == "armeabi-v7a" })
    }
    
    @Test
    fun `repository should provide GPU distribution`() = runTest {
        // Given
        repository.setDevices(TestDeviceFactory.createSampleDevices())
        
        // When
        val stats = repository.getDeviceStats().first()
        
        // Then
        assertTrue(stats.gpuDistribution.isNotEmpty())
        // Sample devices have Adreno 620, Adreno 618, and Adreno 660
        assertEquals(3, stats.gpuDistribution.size)
    }
}
