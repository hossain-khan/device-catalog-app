package dev.hossain.devicecatalog.core.testing

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Tests for FakeAndroidDeviceDao.
 */
class FakeAndroidDeviceDaoTest {
    
    @get:Rule
    val dispatcherRule = TestDispatcherRule()
    
    private lateinit var dao: FakeAndroidDeviceDao
    
    @Before
    fun setup() {
        dao = FakeAndroidDeviceDao()
    }
    
    @Test
    fun `insertDeviceWithRelations should store device with all relations`() = runTest {
        // Given
        val device = TestDeviceFactory.createDeviceWithRelations(id = 0)
        
        // When
        val deviceId = dao.insertDeviceWithRelations(device)
        
        // Then
        val retrieved = dao.getDeviceWithRelationsById(deviceId)
        assertNotNull(retrieved)
        assertEquals("Google", retrieved?.device?.manufacturer)
        assertEquals("Pixel 5", retrieved?.device?.modelName)
        assertEquals(2, retrieved?.abis?.size)
        assertEquals(1, retrieved?.openGlVersions?.size)
        assertEquals(1, retrieved?.screenDensities?.size)
        assertEquals(2, retrieved?.screenSizes?.size)
        assertEquals(4, retrieved?.sdkVersions?.size)
    }
    
    @Test
    fun `getAllDevicesWithRelations should return sorted devices`() = runTest {
        // Given
        val devices = TestDeviceFactory.createSampleDevicesWithRelations()
        dao.setDevices(devices)
        
        // When
        val result = dao.getAllDevicesWithRelations().first()
        
        // Then
        assertEquals(3, result.size)
        // Verify sorting by manufacturer then model name
        assertEquals("Google", result[0].device.manufacturer)
        assertEquals("OnePlus", result[1].device.manufacturer)
        assertEquals("Samsung", result[2].device.manufacturer)
    }
    
    @Test
    fun `searchDevicesWithRelations should filter by manufacturer`() = runTest {
        // Given
        val devices = TestDeviceFactory.createSampleDevicesWithRelations()
        dao.setDevices(devices)
        
        // When
        val result = dao.searchDevicesWithRelations("%Google%").first()
        
        // Then
        assertEquals(1, result.size)
        assertEquals("Google", result[0].device.manufacturer)
        assertEquals("Pixel 5", result[0].device.modelName)
    }
    
    @Test
    fun `searchDevicesWithRelations should filter by model name`() = runTest {
        // Given
        val devices = TestDeviceFactory.createSampleDevicesWithRelations()
        dao.setDevices(devices)
        
        // When
        val result = dao.searchDevicesWithRelations("%Galaxy%").first()
        
        // Then
        assertEquals(1, result.size)
        assertEquals("Samsung", result[0].device.manufacturer)
        assertEquals("Galaxy A52", result[0].device.modelName)
    }
    
    @Test
    fun `searchDevicesWithRelations should be case insensitive`() = runTest {
        // Given
        val devices = TestDeviceFactory.createSampleDevicesWithRelations()
        dao.setDevices(devices)
        
        // When
        val result = dao.searchDevicesWithRelations("%google%").first()
        
        // Then
        assertEquals(1, result.size)
        assertEquals("Google", result[0].device.manufacturer)
    }
    
    @Test
    fun `deleteDevice should remove device and its relations`() = runTest {
        // Given
        val device = TestDeviceFactory.createDeviceWithRelations(id = 0)
        val deviceId = dao.insertDeviceWithRelations(device)
        
        // When
        dao.deleteDevice(deviceId)
        
        // Then
        val retrieved = dao.getDeviceWithRelationsById(deviceId)
        assertNull(retrieved)
    }
    
    @Test
    fun `deleteAllDevices should clear all data`() = runTest {
        // Given
        val devices = TestDeviceFactory.createSampleDevicesWithRelations()
        dao.setDevices(devices)
        
        // When
        dao.deleteAllDevices()
        
        // Then
        val result = dao.getAllDevicesWithRelations().first()
        assertEquals(0, result.size)
    }
    
    @Test
    fun `getDeviceByProperties should find matching device`() = runTest {
        // Given
        val devices = TestDeviceFactory.createSampleDevicesWithRelations()
        dao.setDevices(devices)
        
        // When
        val result = dao.getDeviceByProperties(
            brand = "Google",
            device = "redfin",
            manufacturer = "Google",
            modelName = "Pixel 5"
        )
        
        // Then
        assertNotNull(result)
        assertEquals("Google", result?.device?.manufacturer)
        assertEquals("Pixel 5", result?.device?.modelName)
    }
    
    @Test
    fun `insertDevicesWithRelations should insert multiple devices`() = runTest {
        // Given
        val devices = TestDeviceFactory.createSampleDevicesWithRelations()
        
        // When
        dao.insertDevicesWithRelations(devices)
        
        // Then
        val result = dao.getAllDevicesWithRelations().first()
        assertEquals(3, result.size)
    }
    
    @Test
    fun `updateDevice should modify existing device`() = runTest {
        // Given
        val device = TestDeviceFactory.createDeviceWithRelations(id = 0)
        val deviceId = dao.insertDeviceWithRelations(device)
        
        // When
        val updatedEntity = dao.getDeviceById(deviceId)!!.copy(ram = "12GB")
        dao.updateDevice(updatedEntity)
        
        // Then
        val retrieved = dao.getDeviceById(deviceId)
        assertEquals("12GB", retrieved?.ram)
    }
    
    @Test
    fun `clear should reset all data`() = runTest {
        // Given
        val devices = TestDeviceFactory.createSampleDevicesWithRelations()
        dao.setDevices(devices)
        
        // When
        dao.clear()
        
        // Then
        val result = dao.getAllDevicesWithRelations().first()
        assertEquals(0, result.size)
    }
}
