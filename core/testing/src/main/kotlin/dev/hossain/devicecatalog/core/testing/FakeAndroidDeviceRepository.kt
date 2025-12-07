package dev.hossain.devicecatalog.core.testing

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import dev.hossain.android.catalogparser.models.AndroidDevice
import dev.hossain.devicecatalog.core.data.AbiCount
import dev.hossain.devicecatalog.core.data.BrandManufacturerPair
import dev.hossain.devicecatalog.core.data.DeviceStats
import dev.hossain.devicecatalog.core.data.FormFactorCount
import dev.hossain.devicecatalog.core.data.GpuCount
import dev.hossain.devicecatalog.core.data.ManufacturerCount
import dev.hossain.devicecatalog.core.data.ManufacturerQuizInfo
import dev.hossain.devicecatalog.core.data.OpenGlCount
import dev.hossain.devicecatalog.core.data.ProcessorCount
import dev.hossain.devicecatalog.core.data.RamCount
import dev.hossain.devicecatalog.core.data.ScreenDensityCount
import dev.hossain.devicecatalog.core.data.SdkVersionCount
import dev.hossain.devicecatalog.core.database.AndroidDeviceDao
import dev.hossain.devicecatalog.core.database.AndroidDeviceEntity
import dev.hossain.devicecatalog.core.database.AndroidDeviceWithRelations
import dev.hossain.devicecatalog.core.model.DeviceInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

/**
 * Fake implementation of AndroidDeviceRepository for testing.
 * 
 * This fake uses a FakeAndroidDeviceDao internally to provide realistic
 * repository behavior without requiring a real database.
 * 
 * Following the pattern from Now in Android, this provides better test
 * coverage than mocks by exercising actual production code paths.
 */
class FakeAndroidDeviceRepository(
    private val deviceDao: AndroidDeviceDao = FakeAndroidDeviceDao(),
) {
    
    /**
     * Test utility to set devices directly.
     */
    fun setDevices(devices: List<DeviceInfo>) {
        if (deviceDao is FakeAndroidDeviceDao) {
            val devicesWithRelations = devices.map { 
                AndroidDeviceWithRelations.fromModel(it.androidDevice, it.id)
            }
            deviceDao.setDevices(devicesWithRelations)
        }
    }
    
    /**
     * Test utility to clear all data.
     */
    fun clear() {
        if (deviceDao is FakeAndroidDeviceDao) {
            deviceDao.clear()
        }
    }
    
    fun getPagedDevices(): Flow<PagingData<AndroidDeviceWithRelations>> {
        return Pager(
            config = PagingConfig(
                pageSize = 30,
                initialLoadSize = 45,
                prefetchDistance = 15,
                enablePlaceholders = true,
                maxSize = 150,
            ),
        ) {
            deviceDao.getPagedDevicesWithRelations()
        }.flow
    }
    
    fun getPagedDevicesBySearch(query: String): Flow<PagingData<AndroidDeviceWithRelations>> {
        val searchQuery = "%$query%"
        return Pager(
            config = PagingConfig(
                pageSize = 25,
                initialLoadSize = 40,
                prefetchDistance = 12,
                enablePlaceholders = true,
                maxSize = 120,
            ),
        ) {
            deviceDao.getPagedDevicesWithRelationsBySearch(searchQuery)
        }.flow
    }
    
    fun getAllDevices(): Flow<List<DeviceInfo>> {
        return deviceDao.getAllDevicesWithRelations().map { devicesWithRelations ->
            devicesWithRelations.map { it.toModel() }
        }
    }
    
    suspend fun getDeviceById(deviceId: Long): DeviceInfo? {
        return deviceDao.getDeviceWithRelationsById(deviceId)?.toModel()
    }
    
    fun searchDevices(query: String): Flow<List<DeviceInfo>> {
        val searchQuery = "%$query%"
        return deviceDao.searchDevicesWithRelations(searchQuery).map { devicesWithRelations ->
            devicesWithRelations.map { it.toModel() }
        }
    }
    
    suspend fun getDeviceByProperties(
        brand: String,
        device: String,
        manufacturer: String,
        modelName: String,
    ): DeviceInfo? {
        return deviceDao.getDeviceByProperties(brand, device, manufacturer, modelName)?.toModel()
    }
    
    suspend fun insertDevice(device: AndroidDevice): Long {
        val deviceWithRelations = AndroidDeviceWithRelations.fromModel(device)
        return deviceDao.insertDeviceWithRelations(deviceWithRelations)
    }
    
    suspend fun insertDevice(device: AndroidDeviceEntity): Long {
        return deviceDao.insertDevice(device)
    }
    
    suspend fun insertDevices(devices: List<DeviceInfo>) {
        if (devices.isEmpty()) return
        
        val devicesWithRelations = devices.map { AndroidDeviceWithRelations.fromModel(it.androidDevice) }
        deviceDao.insertDevicesWithRelations(devicesWithRelations)
    }
    
    suspend fun insertDeviceEntities(deviceEntities: List<AndroidDeviceEntity>) {
        if (deviceEntities.isEmpty()) return
        
        deviceEntities.forEach { device ->
            deviceDao.insertDevice(device)
        }
    }
    
    suspend fun deleteDevice(deviceId: Long) {
        deviceDao.deleteDevice(deviceId)
    }
    
    suspend fun deleteAllDevices() {
        deviceDao.deleteAllDevices()
    }
    
    fun getDeviceCount(): Flow<Int> {
        return getAllDevices().map { devices -> devices.size }
    }
    
    suspend fun getManufacturersWithMinDevices(minCount: Int = 5): List<ManufacturerQuizInfo> {
        val devices = deviceDao.getAllDevicesWithRelations().map { devicesWithRelations ->
            devicesWithRelations.map { it.toModel() }
        }.first()
        
        val validDevices = devices.filter { device ->
            device.androidDevice.device.isNotBlank() && device.androidDevice.modelName.isNotBlank()
        }
        
        return validDevices
            .groupingBy { it.androidDevice.manufacturer }
            .eachCount()
            .filter { it.value >= minCount }
            .map { (manufacturer, count) ->
                ManufacturerQuizInfo(
                    manufacturer = manufacturer,
                    deviceCount = count,
                    isQuizAvailable = true
                )
            }
            .sortedByDescending { it.deviceCount }
    }
    
    suspend fun getDevicesByManufacturer(manufacturer: String): List<DeviceInfo> {
        val devices = deviceDao.getAllDevicesWithRelations().map { devicesWithRelations ->
            devicesWithRelations
                .filter { it.device.manufacturer == manufacturer }
                .map { it.toModel() }
        }.first()
        
        return devices.filter { device ->
            device.androidDevice.device.isNotBlank() && device.androidDevice.modelName.isNotBlank()
        }
    }
    
    suspend fun getDistinctBrandManufacturerPairs(): List<BrandManufacturerPair> {
        val devices = deviceDao.getAllDevicesWithRelations().map { devicesWithRelations ->
            devicesWithRelations.map { it.toModel() }
        }.first()
        
        val brandManufacturerMap = devices
            .filter { it.androidDevice.brand.isNotBlank() && it.androidDevice.manufacturer.isNotBlank() }
            .groupBy { it.androidDevice.brand }
            .mapValues { (_, devices) ->
                devices.map { it.androidDevice.manufacturer }.distinct()
            }
        
        return brandManufacturerMap.flatMap { (brand, manufacturers) ->
            manufacturers.map { manufacturer ->
                BrandManufacturerPair(brand = brand, manufacturer = manufacturer)
            }
        }.distinct()
    }
    
    suspend fun getBrandsByManufacturer(manufacturer: String): List<String> {
        val devices = deviceDao.getAllDevicesWithRelations().map { devicesWithRelations ->
            devicesWithRelations
                .filter { it.device.manufacturer == manufacturer }
                .map { it.toModel() }
        }.first()
        
        return devices
            .map { it.androidDevice.brand }
            .filter { it.isNotBlank() }
            .distinct()
    }
    
    suspend fun getManufacturerForBrand(brand: String): String? {
        val devices = deviceDao.getAllDevicesWithRelations().map { devicesWithRelations ->
            devicesWithRelations
                .filter { it.device.brand == brand }
                .map { it.toModel() }
        }.first()
        
        return devices
            .groupingBy { it.androidDevice.manufacturer }
            .eachCount()
            .maxByOrNull { it.value }
            ?.key
    }
    
    fun getDeviceStats(): Flow<DeviceStats> {
        return getAllDevices().map { devices ->
            val totalDevices = devices.size
            
            val formFactors = devices.groupingBy { it.androidDevice.formFactor }.eachCount()
            val totalFormFactors = formFactors.size
            
            val manufacturerCounts = devices
                .groupingBy { it.androidDevice.manufacturer }
                .eachCount()
            val totalManufacturers = manufacturerCounts.size
            val topManufacturers = manufacturerCounts
                .toList()
                .sortedByDescending { it.second }
                .take(10)
                .map { ManufacturerCount(it.first, it.second) }
            
            val ramDistribution = devices
                .groupingBy { it.androidDevice.ram }
                .eachCount()
                .map { RamCount(it.key, it.value) }
                .sortedByDescending { it.count }
            
            val sdkVersions = mutableMapOf<Int, Int>()
            devices.forEach { device ->
                device.androidDevice.sdkVersions.forEach { sdkVersion ->
                    sdkVersions[sdkVersion] = sdkVersions.getOrDefault(sdkVersion, 0) + 1
                }
            }
            val sdkVersionDistribution = sdkVersions
                .map { SdkVersionCount(it.key, it.value) }
                .sortedByDescending { it.sdkVersion }
            
            val screenDensities = mutableMapOf<Int, Int>()
            devices.forEach { device ->
                device.androidDevice.screenDensities.forEach { density ->
                    screenDensities[density] = screenDensities.getOrDefault(density, 0) + 1
                }
            }
            val screenDensityDistribution = screenDensities
                .map { ScreenDensityCount(it.key, it.value) }
                .sortedByDescending { it.density }
            
            val abiSupport = mutableMapOf<String, Int>()
            devices.forEach { device ->
                device.androidDevice.abis.forEach { abi ->
                    abiSupport[abi] = abiSupport.getOrDefault(abi, 0) + 1
                }
            }
            val abiDistribution = abiSupport
                .map { AbiCount(it.key, it.value) }
                .sortedByDescending { it.count }
            
            val gpuDistribution = devices
                .groupingBy { it.androidDevice.gpu }
                .eachCount()
                .toList()
                .sortedByDescending { it.second }
                .take(10)
                .map { GpuCount(it.first, it.second) }
            
            DeviceStats(
                totalDevices = totalDevices,
                totalFormFactors = totalFormFactors,
                totalManufacturers = totalManufacturers,
                formFactorBreakdown =
                    formFactors
                        .map { FormFactorCount(it.key, it.value) }
                        .sortedByDescending { it.count },
                topManufacturers = topManufacturers,
                ramDistribution = ramDistribution,
                sdkVersionDistribution = sdkVersionDistribution,
                screenDensityDistribution = screenDensityDistribution,
                abiDistribution = abiDistribution,
                gpuDistribution = gpuDistribution,
            )
        }
    }
    
    fun getProcessorDistribution(): Flow<List<ProcessorCount>> {
        return getAllDevices().map { devices ->
            devices
                .filter { it.androidDevice.processorName.isNotBlank() }
                .groupingBy { it.androidDevice.processorName }
                .eachCount()
                .toList()
                .sortedByDescending { it.second }
                .take(15)
                .map { ProcessorCount(it.first, it.second) }
        }
    }
    
    fun getOpenGlDistribution(): Flow<List<OpenGlCount>> {
        return getAllDevices().map { devices ->
            val openGlVersions = mutableMapOf<String, Int>()
            devices.forEach { device ->
                device.androidDevice.openGlEsVersions.forEach { version ->
                    openGlVersions[version] = openGlVersions.getOrDefault(version, 0) + 1
                }
            }
            openGlVersions
                .toList()
                .sortedByDescending { it.second }
                .map { OpenGlCount(it.first, it.second) }
        }
    }
}
