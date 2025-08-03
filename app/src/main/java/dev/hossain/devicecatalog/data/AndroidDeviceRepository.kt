package dev.hossain.devicecatalog.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import dev.hossain.android.catalogparser.models.AndroidDevice
import dev.hossain.devicecatalog.db.AndroidDeviceDao
import dev.hossain.devicecatalog.db.AndroidDeviceEntity
import dev.hossain.devicecatalog.db.AndroidDeviceWithRelations
import dev.hossain.devicecatalog.model.DeviceInfo
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber

@Inject
class AndroidDeviceRepository
    constructor(
        private val deviceDao: AndroidDeviceDao,
    ) {
        /**
         * Get paged list of devices with relationships.
         * Returns the entity with relations for UI display.
         */
        fun getPagedDevices(): Flow<PagingData<AndroidDeviceWithRelations>> {
            Timber.d("Creating paged devices flow")
            return Pager(
                config =
                    PagingConfig(
                        pageSize = 20,
                        enablePlaceholders = true,
                        maxSize = 100,
                    ),
            ) {
                deviceDao.getPagedDevicesWithRelations()
            }.flow
        }

        /**
         * Get paged list of devices filtered by search query with relationships.
         */
        fun getPagedDevicesBySearch(query: String): Flow<PagingData<AndroidDeviceWithRelations>> {
            val searchQuery = "%$query%"
            Timber.d("Creating paged devices flow with search: $query")
            return Pager(
                config =
                    PagingConfig(
                        pageSize = 20,
                        enablePlaceholders = true,
                        maxSize = 100,
                    ),
            ) {
                deviceDao.getPagedDevicesWithRelationsBySearch(searchQuery)
            }.flow
        }

        /**
         * Get all devices with relationships as domain models.
         */
        fun getAllDevices(): Flow<List<DeviceInfo>> {
            Timber.d("Getting all devices with relationships")
            return deviceDao.getAllDevicesWithRelations().map { devicesWithRelations ->
                Timber.d("Retrieved ${devicesWithRelations.size} devices from database")
                devicesWithRelations.map { it.toModel() }
            }
        }

        /**
         * Get a specific device by ID with all its relationships.
         */
        suspend fun getDeviceById(deviceId: Long): DeviceInfo? {
            Timber.d("Getting device with ID: $deviceId")
            return deviceDao
                .getDeviceWithRelationsById(deviceId)
                ?.also {
                    Timber.d("Found device: ${it.device.manufacturer} ${it.device.modelName}")
                }?.toModel()
        }

        /**
         * Search devices by manufacturer or model name.
         */
        fun searchDevices(query: String): Flow<List<DeviceInfo>> {
            val searchQuery = "%$query%"
            Timber.d("Searching devices with query: $query")
            return deviceDao.searchDevicesWithRelations(searchQuery).map { devicesWithRelations ->
                Timber.d("Search returned ${devicesWithRelations.size} devices")
                devicesWithRelations.map { it.toModel() }
            }
        }

        /**
         * Get a specific device by its properties (since AndroidDevice doesn't have an ID).
         */
        suspend fun getDeviceByProperties(
            brand: String,
            device: String,
            manufacturer: String,
            modelName: String,
        ): DeviceInfo? {
            Timber.d("Getting device by properties: brand=$brand, device=$device, manufacturer=$manufacturer, modelName=$modelName")
            return try {
                deviceDao
                    .getDeviceByProperties(brand, device, manufacturer, modelName)
                    ?.also {
                        Timber.d("Found device: ${it.device.manufacturer} ${it.device.modelName}")
                    }?.toModel()
            } catch (e: Exception) {
                Timber.e(e, "Failed to get device by properties")
                null
            }
        }

        /**
         * Insert a single device with all its related data.
         * @param device The domain model device to insert
         * @return The ID of the inserted device
         */
        suspend fun insertDevice(device: AndroidDevice): Long {
            Timber.d("Inserting device: ${device.manufacturer} ${device.modelName}")
            return try {
                val deviceWithRelations = AndroidDeviceWithRelations.fromModel(device)
                val deviceId = deviceDao.insertDeviceWithRelations(deviceWithRelations)
                Timber.i("Successfully inserted device with ID: $deviceId")
                deviceId
            } catch (e: Exception) {
                Timber.e(e, "Failed to insert device: ${device.manufacturer} ${device.modelName}")
                throw e
            }
        }

        /**
         * Insert a single device entity (for backwards compatibility).
         * Note: This only inserts the main device entity without relationships.
         */
        suspend fun insertDevice(device: AndroidDeviceEntity): Long {
            Timber.d("Inserting device entity: ${device.manufacturer} ${device.modelName}")
            return try {
                val deviceId = deviceDao.insertDevice(device)
                Timber.i("Successfully inserted device entity with ID: $deviceId")
                deviceId
            } catch (e: Exception) {
                Timber.e(e, "Failed to insert device entity: ${device.manufacturer} ${device.modelName}")
                throw e
            }
        }

        /**
         * Insert multiple devices with all their related data.
         * @param devices List of domain model devices to insert
         */
        suspend fun insertDevices(devices: List<DeviceInfo>) {
            if (devices.isEmpty()) {
                Timber.w("Attempted to insert empty device list")
                return
            }

            Timber.i("Inserting ${devices.size} devices with relationships")
            try {
                val devicesWithRelations = devices.map { AndroidDeviceWithRelations.fromModel(it.androidDevice) }
                deviceDao.insertDevicesWithRelations(devicesWithRelations)
                Timber.i("Successfully inserted ${devices.size} devices")
            } catch (e: Exception) {
                Timber.e(e, "Failed to insert ${devices.size} devices")
                throw e
            }
        }

        /**
         * Insert multiple device entities (for backwards compatibility).
         * Note: This only inserts the main device entities without relationships.
         */
        suspend fun insertDeviceEntities(deviceEntities: List<AndroidDeviceEntity>) {
            if (deviceEntities.isEmpty()) {
                Timber.w("Attempted to insert empty device entity list")
                return
            }

            Timber.i("Inserting ${deviceEntities.size} device entities")
            try {
                deviceEntities.forEach { device ->
                    deviceDao.insertDevice(device)
                }
                Timber.i("Successfully inserted ${deviceEntities.size} device entities")
            } catch (e: Exception) {
                Timber.e(e, "Failed to insert ${deviceEntities.size} device entities")
                throw e
            }
        }

        /**
         * Delete a specific device (cascade will delete related data).
         */
        suspend fun deleteDevice(deviceId: Long) {
            Timber.d("Deleting device with ID: $deviceId")
            try {
                deviceDao.deleteDevice(deviceId)
                Timber.i("Successfully deleted device with ID: $deviceId")
            } catch (e: Exception) {
                Timber.e(e, "Failed to delete device with ID: $deviceId")
                throw e
            }
        }

        /**
         * Delete all devices and their related data.
         */
        suspend fun deleteAllDevices() {
            Timber.w("Deleting all devices and related data")
            try {
                deviceDao.deleteAllDevices()
                Timber.i("Successfully deleted all devices")
            } catch (e: Exception) {
                Timber.e(e, "Failed to delete all devices")
                throw e
            }
        }

        /**
         * Get count of devices for statistics.
         */
        fun getDeviceCount(): Flow<Int> {
            Timber.d("Getting device count")
            return getAllDevices().map { devices ->
                val count = devices.size
                Timber.d("Current device count: $count")
                count
            }
        }

        /**
         * Get device statistics including counts and breakdowns.
         */
        fun getDeviceStats(): Flow<DeviceStats> {
            Timber.d("Getting device statistics")
            return getAllDevices().map { devices ->
                val totalDevices = devices.size

                // Count unique form factors
                val formFactors = devices.groupingBy { it.androidDevice.formFactor }.eachCount()
                val totalFormFactors = formFactors.size

                // Get top 10 manufacturers by device count
                val topManufacturers =
                    devices
                        .groupingBy { it.androidDevice.manufacturer }
                        .eachCount()
                        .toList()
                        .sortedByDescending { it.second }
                        .take(10)
                        .map { ManufacturerCount(it.first, it.second) }

                Timber.d(
                    "Device stats - Total: $totalDevices, Form factors: $totalFormFactors, Top manufacturers: ${topManufacturers.size}",
                )

                DeviceStats(
                    totalDevices = totalDevices,
                    totalFormFactors = totalFormFactors,
                    formFactorBreakdown = formFactors.map { FormFactorCount(it.key, it.value) },
                    topManufacturers = topManufacturers,
                )
            }
        }
    }

/**
 * Data class representing device statistics.
 */
data class DeviceStats(
    val totalDevices: Int,
    val totalFormFactors: Int,
    val formFactorBreakdown: List<FormFactorCount>,
    val topManufacturers: List<ManufacturerCount>,
)

/**
 * Data class for form factor count statistics.
 */
data class FormFactorCount(
    val formFactor: String,
    val count: Int,
)

/**
 * Data class for manufacturer count statistics.
 */
data class ManufacturerCount(
    val manufacturer: String,
    val count: Int,
)
