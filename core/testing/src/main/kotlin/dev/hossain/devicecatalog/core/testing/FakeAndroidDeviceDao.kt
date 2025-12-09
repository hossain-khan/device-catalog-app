package dev.hossain.devicecatalog.core.testing

import androidx.paging.PagingSource
import androidx.paging.PagingState
import dev.hossain.devicecatalog.core.database.AndroidDeviceDao
import dev.hossain.devicecatalog.core.database.AndroidDeviceEntity
import dev.hossain.devicecatalog.core.database.AndroidDeviceWithRelations
import dev.hossain.devicecatalog.core.database.DeviceAbi
import dev.hossain.devicecatalog.core.database.DeviceOpenGl
import dev.hossain.devicecatalog.core.database.DeviceScreenDensity
import dev.hossain.devicecatalog.core.database.DeviceScreenSize
import dev.hossain.devicecatalog.core.database.DeviceSdkVersion
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

/**
 * Fake implementation of AndroidDeviceDao for testing.
 * 
 * This implementation stores data in-memory using MutableStateFlow,
 * making it suitable for unit tests without requiring a real database.
 * 
 * Following the pattern from Now in Android, this fake provides realistic
 * behavior and exercises more production code than mocks would.
 */
class FakeAndroidDeviceDao : AndroidDeviceDao {
    
    private var nextId = 1L
    private val devicesFlow = MutableStateFlow<List<AndroidDeviceWithRelations>>(emptyList())
    
    /**
     * Test utility to set the device list directly.
     */
    fun setDevices(devices: List<AndroidDeviceWithRelations>) {
        devicesFlow.value = devices
    }
    
    /**
     * Test utility to clear all data.
     */
    fun clear() {
        devicesFlow.value = emptyList()
        nextId = 1L
    }
    
    // ----------------------------------------------------------------
    // Basic device operations
    // ----------------------------------------------------------------
    
    override suspend fun insertDevice(device: AndroidDeviceEntity): Long {
        val deviceId = if (device.id == 0L) nextId++ else device.id
        val newDevice = device.copy(id = deviceId)
        
        // Check if device already exists
        val existingIndex = devicesFlow.value.indexOfFirst { it.device.id == deviceId }
        val deviceWithRelations = AndroidDeviceWithRelations(
            device = newDevice,
            abis = emptyList(),
            openGlVersions = emptyList(),
            screenDensities = emptyList(),
            screenSizes = emptyList(),
            sdkVersions = emptyList(),
        )
        
        devicesFlow.value = if (existingIndex >= 0) {
            devicesFlow.value.toMutableList().apply {
                set(existingIndex, deviceWithRelations)
            }
        } else {
            devicesFlow.value + deviceWithRelations
        }
        
        return deviceId
    }
    
    override suspend fun updateDevice(device: AndroidDeviceEntity) {
        val index = devicesFlow.value.indexOfFirst { it.device.id == device.id }
        if (index >= 0) {
            val existing = devicesFlow.value[index]
            devicesFlow.value = devicesFlow.value.toMutableList().apply {
                set(index, existing.copy(device = device))
            }
        }
    }
    
    override suspend fun getDeviceById(deviceId: Long): AndroidDeviceEntity? {
        return devicesFlow.value.find { it.device.id == deviceId }?.device
    }
    
    override suspend fun deleteDevice(deviceId: Long) {
        devicesFlow.value = devicesFlow.value.filter { it.device.id != deviceId }
    }
    
    override suspend fun deleteAllDevices() {
        devicesFlow.value = emptyList()
    }
    
    // ----------------------------------------------------------------
    // Relationship queries
    // ----------------------------------------------------------------
    
    override fun getAllDevicesWithRelations(): Flow<List<AndroidDeviceWithRelations>> {
        return devicesFlow.map { devices ->
            devices.sortedWith(compareBy({ it.device.manufacturer }, { it.device.modelName }))
        }
    }
    
    override suspend fun getDeviceWithRelationsById(deviceId: Long): AndroidDeviceWithRelations? {
        return devicesFlow.value.find { it.device.id == deviceId }
    }
    
    override suspend fun getDeviceByProperties(
        brand: String,
        device: String,
        manufacturer: String,
        modelName: String,
    ): AndroidDeviceWithRelations? {
        return devicesFlow.value.find {
            it.device.brand == brand &&
                it.device.device == device &&
                it.device.manufacturer == manufacturer &&
                it.device.modelName == modelName
        }
    }
    
    override fun searchDevicesWithRelations(search: String): Flow<List<AndroidDeviceWithRelations>> {
        return devicesFlow.map { devices ->
            devices.filter {
                it.device.manufacturer.contains(search.trim('%'), ignoreCase = true) ||
                    it.device.modelName.contains(search.trim('%'), ignoreCase = true) ||
                    it.device.brand.contains(search.trim('%'), ignoreCase = true)
            }.sortedWith(compareBy({ it.device.manufacturer }, { it.device.modelName }))
        }
    }

    // ----------------------------------------------------------------
    // Latest First queries (sorted by SDK version descending)
    // ----------------------------------------------------------------

    override fun getAllDevicesWithRelationsLatestFirst(): Flow<List<AndroidDeviceWithRelations>> {
        // For testing, just return devices sorted alphabetically (fake doesn't need real SDK sorting)
        return getAllDevicesWithRelations()
    }

    override fun searchDevicesWithRelationsLatestFirst(search: String): Flow<List<AndroidDeviceWithRelations>> {
        // For testing, just return filtered devices sorted alphabetically
        return searchDevicesWithRelations(search)
    }

    override fun getPagedDevicesWithRelationsLatestFirst(): PagingSource<Int, AndroidDeviceWithRelations> {
        // For testing, just return paginated devices sorted alphabetically
        return getPagedDevicesWithRelations()
    }

    override fun getPagedDevicesWithRelationsBySearchLatestFirst(search: String): PagingSource<Int, AndroidDeviceWithRelations> {
        // For testing, just return filtered paginated devices sorted alphabetically
        return getPagedDevicesWithRelationsBySearch(search)
    }

    // ----------------------------------------------------------------
    // Paging queries
    // ----------------------------------------------------------------

    override fun getPagedDevicesWithRelations(): PagingSource<Int, AndroidDeviceWithRelations> {
        return FakePagingSource(devicesFlow.value.sortedWith(compareBy({ it.device.manufacturer }, { it.device.modelName })))
    }
    
    override fun getPagedDevicesWithRelationsBySearch(search: String): PagingSource<Int, AndroidDeviceWithRelations> {
        val filtered = devicesFlow.value.filter {
            it.device.manufacturer.contains(search.trim('%'), ignoreCase = true) ||
                it.device.modelName.contains(search.trim('%'), ignoreCase = true) ||
                it.device.brand.contains(search.trim('%'), ignoreCase = true)
        }.sortedWith(compareBy({ it.device.manufacturer }, { it.device.modelName }))
        return FakePagingSource(filtered)
    }
    
    // ----------------------------------------------------------------
    // Bulk insert operations
    // ----------------------------------------------------------------
    
    override suspend fun insertDeviceWithRelations(deviceWithRelations: AndroidDeviceWithRelations): Long {
        val deviceId = if (deviceWithRelations.device.id == 0L) nextId++ else deviceWithRelations.device.id
        val newDevice = deviceWithRelations.copy(
            device = deviceWithRelations.device.copy(id = deviceId),
            abis = deviceWithRelations.abis.map { it.copy(device_id = deviceId) },
            openGlVersions = deviceWithRelations.openGlVersions.map { it.copy(device_id = deviceId) },
            screenDensities = deviceWithRelations.screenDensities.map { it.copy(device_id = deviceId) },
            screenSizes = deviceWithRelations.screenSizes.map { it.copy(device_id = deviceId) },
            sdkVersions = deviceWithRelations.sdkVersions.map { it.copy(device_id = deviceId) },
        )
        
        val existingIndex = devicesFlow.value.indexOfFirst { it.device.id == deviceId }
        devicesFlow.value = if (existingIndex >= 0) {
            devicesFlow.value.toMutableList().apply {
                set(existingIndex, newDevice)
            }
        } else {
            devicesFlow.value + newDevice
        }
        
        return deviceId
    }
    
    override suspend fun insertDevicesWithRelations(devicesWithRelations: List<AndroidDeviceWithRelations>) {
        devicesWithRelations.forEach { insertDeviceWithRelations(it) }
    }
    
    // ----------------------------------------------------------------
    // Individual related entity operations
    // ----------------------------------------------------------------
    
    override suspend fun insertDeviceAbi(deviceAbi: DeviceAbi) {
        val index = devicesFlow.value.indexOfFirst { it.device.id == deviceAbi.device_id }
        if (index >= 0) {
            val existing = devicesFlow.value[index]
            devicesFlow.value = devicesFlow.value.toMutableList().apply {
                set(index, existing.copy(abis = existing.abis + deviceAbi))
            }
        }
    }
    
    override fun getDeviceAbis(deviceId: Long): Flow<List<DeviceAbi>> {
        return devicesFlow.map { devices ->
            devices.find { it.device.id == deviceId }?.abis ?: emptyList()
        }
    }
    
    override fun getAllDeviceAbis(): Flow<List<DeviceAbi>> {
        return devicesFlow.map { devices ->
            devices.flatMap { it.abis }
        }
    }
    
    override suspend fun deleteAllDeviceAbis() {
        devicesFlow.value = devicesFlow.value.map { it.copy(abis = emptyList()) }
    }
    
    override suspend fun insertDeviceOpenGlVersion(deviceOpenGl: DeviceOpenGl) {
        val index = devicesFlow.value.indexOfFirst { it.device.id == deviceOpenGl.device_id }
        if (index >= 0) {
            val existing = devicesFlow.value[index]
            devicesFlow.value = devicesFlow.value.toMutableList().apply {
                set(index, existing.copy(openGlVersions = existing.openGlVersions + deviceOpenGl))
            }
        }
    }
    
    override fun getDeviceOpenGlVersions(deviceId: Long): Flow<List<DeviceOpenGl>> {
        return devicesFlow.map { devices ->
            devices.find { it.device.id == deviceId }?.openGlVersions ?: emptyList()
        }
    }
    
    override fun getAllDeviceOpenGlVersions(): Flow<List<DeviceOpenGl>> {
        return devicesFlow.map { devices ->
            devices.flatMap { it.openGlVersions }
        }
    }
    
    override suspend fun deleteAllDeviceOpenGlVersions() {
        devicesFlow.value = devicesFlow.value.map { it.copy(openGlVersions = emptyList()) }
    }
    
    override suspend fun insertDeviceScreenDensity(deviceScreenDensity: DeviceScreenDensity) {
        val index = devicesFlow.value.indexOfFirst { it.device.id == deviceScreenDensity.device_id }
        if (index >= 0) {
            val existing = devicesFlow.value[index]
            devicesFlow.value = devicesFlow.value.toMutableList().apply {
                set(index, existing.copy(screenDensities = existing.screenDensities + deviceScreenDensity))
            }
        }
    }
    
    override fun getDeviceScreenDensities(deviceId: Long): Flow<List<DeviceScreenDensity>> {
        return devicesFlow.map { devices ->
            devices.find { it.device.id == deviceId }?.screenDensities ?: emptyList()
        }
    }
    
    override fun getAllDeviceScreenDensities(): Flow<List<DeviceScreenDensity>> {
        return devicesFlow.map { devices ->
            devices.flatMap { it.screenDensities }
        }
    }
    
    override suspend fun deleteAllDeviceScreenDensities() {
        devicesFlow.value = devicesFlow.value.map { it.copy(screenDensities = emptyList()) }
    }
    
    override suspend fun insertDeviceScreenSize(deviceScreenSize: DeviceScreenSize) {
        val index = devicesFlow.value.indexOfFirst { it.device.id == deviceScreenSize.device_id }
        if (index >= 0) {
            val existing = devicesFlow.value[index]
            devicesFlow.value = devicesFlow.value.toMutableList().apply {
                set(index, existing.copy(screenSizes = existing.screenSizes + deviceScreenSize))
            }
        }
    }
    
    override fun getDeviceScreenSizes(deviceId: Long): Flow<List<DeviceScreenSize>> {
        return devicesFlow.map { devices ->
            devices.find { it.device.id == deviceId }?.screenSizes ?: emptyList()
        }
    }
    
    override fun getAllDeviceScreenSizes(): Flow<List<DeviceScreenSize>> {
        return devicesFlow.map { devices ->
            devices.flatMap { it.screenSizes }
        }
    }
    
    override suspend fun deleteAllDeviceScreenSizes() {
        devicesFlow.value = devicesFlow.value.map { it.copy(screenSizes = emptyList()) }
    }
    
    override suspend fun insertDeviceSdkVersion(deviceSdkVersion: DeviceSdkVersion) {
        val index = devicesFlow.value.indexOfFirst { it.device.id == deviceSdkVersion.device_id }
        if (index >= 0) {
            val existing = devicesFlow.value[index]
            devicesFlow.value = devicesFlow.value.toMutableList().apply {
                set(index, existing.copy(sdkVersions = existing.sdkVersions + deviceSdkVersion))
            }
        }
    }
    
    override fun getDeviceSdkVersions(deviceId: Long): Flow<List<DeviceSdkVersion>> {
        return devicesFlow.map { devices ->
            devices.find { it.device.id == deviceId }?.sdkVersions ?: emptyList()
        }
    }
    
    override fun getAllDeviceSdkVersions(): Flow<List<DeviceSdkVersion>> {
        return devicesFlow.map { devices ->
            devices.flatMap { it.sdkVersions }
        }
    }
    
    override suspend fun deleteAllDeviceSdkVersions() {
        devicesFlow.value = devicesFlow.value.map { it.copy(sdkVersions = emptyList()) }
    }
}

/**
 * Fake PagingSource for testing pagination.
 */
private class FakePagingSource<T : Any>(
    private val data: List<T>,
) : PagingSource<Int, T>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, T> {
        val page = params.key ?: 0
        val pageSize = params.loadSize
        val startIndex = page * pageSize
        val endIndex = minOf(startIndex + pageSize, data.size)
        
        return if (startIndex >= data.size) {
            LoadResult.Page(
                data = emptyList(),
                prevKey = if (page > 0) page - 1 else null,
                nextKey = null,
            )
        } else {
            LoadResult.Page(
                data = data.subList(startIndex, endIndex),
                prevKey = if (page > 0) page - 1 else null,
                nextKey = if (endIndex < data.size) page + 1 else null,
            )
        }
    }
    
    override fun getRefreshKey(state: PagingState<Int, T>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}
