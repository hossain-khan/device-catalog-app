package dev.hossain.devicecatalog.core.testing

import dev.hossain.android.catalogparser.models.AndroidDevice
import dev.hossain.android.catalogparser.models.FormFactor
import dev.hossain.devicecatalog.core.model.DeviceInfo
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Tests for FakeAndroidDeviceRepository.
 */
class FakeAndroidDeviceRepositoryTest {
    
    @get:Rule
    val dispatcherRule = TestDispatcherRule()
    
    private lateinit var repository: FakeAndroidDeviceRepository
    
    @Before
    fun setup() {
        repository = FakeAndroidDeviceRepository()
    }
    
    @Test
    fun `getAllDevices should return all devices as domain models`() = runTest {
        // Given
        val devices = TestDeviceFactory.createSampleDevices()
        repository.setDevices(devices)
        
        // When
        val result = repository.getAllDevices().first()
        
        // Then
        assertEquals(3, result.size)
        assertEquals("Pixel 5", result[0].androidDevice.modelName)
    }
    
    @Test
    fun `getDeviceById should return correct device`() = runTest {
        // Given
        val devices = TestDeviceFactory.createSampleDevices()
        repository.setDevices(devices)
        
        // When
        val result = repository.getDeviceById(2)
        
        // Then
        assertNotNull(result)
        assertEquals("Samsung", result?.androidDevice?.manufacturer)
        assertEquals("Galaxy A52", result?.androidDevice?.modelName)
    }
    
    @Test
    fun `searchDevices should filter devices by query`() = runTest {
        // Given
        val devices = TestDeviceFactory.createSampleDevices()
        repository.setDevices(devices)
        
        // When
        val result = repository.searchDevices("Samsung").first()
        
        // Then
        assertEquals(1, result.size)
        assertEquals("Samsung", result[0].androidDevice.manufacturer)
    }
    
    @Test
    fun `insertDevice should add new device with AndroidDevice type`() = runTest {
        // Given
        val androidDevice = AndroidDevice(
            brand = "Xiaomi",
            device = "mi11",
            manufacturer = "Xiaomi",
            modelName = "Mi 11",
            ram = "8GB",
            formFactor = FormFactor.PHONE,
            processorName = "Qualcomm Snapdragon 888",
            gpu = "Adreno 660",
            screenSizes = listOf("normal", "long"),
            screenDensities = listOf(440),
            abis = listOf("arm64-v8a", "armeabi-v7a"),
            sdkVersions = listOf(30, 31, 32, 33),
            openGlEsVersions = listOf("3.2"),
        )
        
        // When
        val deviceId = repository.insertDevice(androidDevice)
        
        // Then
        val retrieved = repository.getDeviceById(deviceId)
        assertNotNull(retrieved)
        assertEquals("Xiaomi", retrieved?.androidDevice?.manufacturer)
        assertEquals("Mi 11", retrieved?.androidDevice?.modelName)
    }
    
    @Test
    fun `deleteDevice should remove device`() = runTest {
        // Given
        val devices = TestDeviceFactory.createSampleDevices()
        repository.setDevices(devices)
        
        // When
        repository.deleteDevice(2)
        
        // Then
        val result = repository.getAllDevices().first()
        assertEquals(2, result.size)
        assertTrue(result.none { it.id == 2L })
    }
    
    @Test
    fun `deleteAllDevices should clear repository`() = runTest {
        // Given
        val devices = TestDeviceFactory.createSampleDevices()
        repository.setDevices(devices)
        
        // When
        repository.deleteAllDevices()
        
        // Then
        val result = repository.getAllDevices().first()
        assertEquals(0, result.size)
    }
    
    @Test
    fun `getDeviceCount should return correct count`() = runTest {
        // Given
        val devices = TestDeviceFactory.createSampleDevices()
        repository.setDevices(devices)
        
        // When
        val count = repository.getDeviceCount().first()
        
        // Then
        assertEquals(3, count)
    }
    
    @Test
    fun `getManufacturersWithMinDevices should filter by minimum count`() = runTest {
        // Given - create devices with same manufacturer to meet minimum
        val devices = listOf(
            DeviceInfo(id = 1, androidDevice = AndroidDevice("Google", "redfin", "Google", "Pixel 5", "8GB", FormFactor.PHONE, "Snapdragon 765G", "Adreno 620", listOf("normal"), listOf(440), listOf("arm64-v8a"), listOf(30), listOf("3.2"))),
            DeviceInfo(id = 2, androidDevice = AndroidDevice("Google", "barbet", "Google", "Pixel 6", "8GB", FormFactor.PHONE, "Snapdragon 765G", "Adreno 620", listOf("normal"), listOf(440), listOf("arm64-v8a"), listOf(31), listOf("3.2"))),
            DeviceInfo(id = 3, androidDevice = AndroidDevice("Google", "cheetah", "Google", "Pixel 7", "8GB", FormFactor.PHONE, "Snapdragon 765G", "Adreno 620", listOf("normal"), listOf(440), listOf("arm64-v8a"), listOf(32), listOf("3.2"))),
            DeviceInfo(id = 4, androidDevice = AndroidDevice("Google", "husky", "Google", "Pixel 8", "8GB", FormFactor.PHONE, "Snapdragon 765G", "Adreno 620", listOf("normal"), listOf(440), listOf("arm64-v8a"), listOf(33), listOf("3.2"))),
            DeviceInfo(id = 5, androidDevice = AndroidDevice("Google", "shiba", "Google", "Pixel 9", "12GB", FormFactor.PHONE, "Snapdragon 765G", "Adreno 620", listOf("normal"), listOf(440), listOf("arm64-v8a"), listOf(34), listOf("3.2"))),
            DeviceInfo(id = 6, androidDevice = AndroidDevice("Samsung", "a52xq", "Samsung", "Galaxy A52", "6GB", FormFactor.PHONE, "Snapdragon 720G", "Adreno 618", listOf("normal"), listOf(440), listOf("arm64-v8a"), listOf(30), listOf("3.2"))),
        )
        repository.setDevices(devices)
        
        // When
        val result = repository.getManufacturersWithMinDevices(minCount = 5)
        
        // Then
        assertEquals(1, result.size)
        assertEquals("Google", result[0].manufacturer)
        assertEquals(5, result[0].deviceCount)
        assertTrue(result[0].isQuizAvailable)
    }
    
    @Test
    fun `getDevicesByManufacturer should return devices for manufacturer`() = runTest {
        // Given
        val devices = TestDeviceFactory.createSampleDevices()
        repository.setDevices(devices)
        
        // When
        val result = repository.getDevicesByManufacturer("Google")
        
        // Then
        assertEquals(1, result.size)
        assertEquals("Google", result[0].androidDevice.manufacturer)
    }
    
    @Test
    fun `getDeviceStats should calculate statistics correctly`() = runTest {
        // Given
        val devices = TestDeviceFactory.createSampleDevices()
        repository.setDevices(devices)
        
        // When
        val stats = repository.getDeviceStats().first()
        
        // Then
        assertEquals(3, stats.totalDevices)
        assertTrue(stats.topManufacturers.isNotEmpty())
        assertTrue(stats.ramDistribution.isNotEmpty())
        assertTrue(stats.sdkVersionDistribution.isNotEmpty())
        assertTrue(stats.abiDistribution.isNotEmpty())
    }
    
    @Test
    fun `getDistinctBrandManufacturerPairs should return unique pairs`() = runTest {
        // Given
        val devices = TestDeviceFactory.createSampleDevices()
        repository.setDevices(devices)
        
        // When
        val pairs = repository.getDistinctBrandManufacturerPairs()
        
        // Then
        assertTrue(pairs.isNotEmpty())
        assertEquals(3, pairs.size) // Google/Google, Samsung/Samsung, OnePlus/OnePlus
    }
    
    @Test
    fun `clear should reset repository state`() = runTest {
        // Given
        val devices = TestDeviceFactory.createSampleDevices()
        repository.setDevices(devices)
        
        // When
        repository.clear()
        
        // Then
        val result = repository.getAllDevices().first()
        assertEquals(0, result.size)
    }
}
