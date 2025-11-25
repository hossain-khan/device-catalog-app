package dev.hossain.devicecatalog.repository

import androidx.paging.PagingSource
import dev.hossain.android.catalogparser.models.AndroidDevice
import dev.hossain.devicecatalog.db.AndroidDeviceDao
import dev.hossain.devicecatalog.db.AndroidDeviceWithRelations
import dev.hossain.devicecatalog.core.model.DeviceInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for managing Android device data with proper one-to-many relationships.
 * This class demonstrates the correct usage of the Room database with relationships.
 */
@Singleton
class AndroidDeviceRepository
    @Inject
    constructor(
        private val deviceDao: AndroidDeviceDao,
    ) {
        /**
         * Get all devices with their relationships as a Flow of domain models.
         */
        fun getAllDevices(): Flow<List<DeviceInfo>> {
            Timber.d("Getting all devices with relationships")
            return deviceDao
                .getAllDevicesWithRelations()
                .map { devicesWithRelations ->
                    Timber.d("Retrieved ${devicesWithRelations.size} devices from database")
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
         * Search devices by manufacturer or model name.
         */
        fun searchDevices(query: String): Flow<List<DeviceInfo>> {
            val searchQuery = "%$query%"
            Timber.d("Searching devices with query: $query")
            return deviceDao
                .searchDevicesWithRelations(searchQuery)
                .map { devicesWithRelations ->
                    Timber.d("Search returned ${devicesWithRelations.size} devices")
                    devicesWithRelations.map { it.toModel() }
                }
        }

        /**
         * Get paged devices for use with Jetpack Compose Paging.
         */
        fun getPagedDevices(): PagingSource<Int, AndroidDeviceWithRelations> {
            Timber.d("Creating paged devices source")
            return deviceDao.getPagedDevicesWithRelations()
        }

        /**
         * Get paged devices filtered by search query.
         */
        fun getPagedDevicesBySearch(query: String): PagingSource<Int, AndroidDeviceWithRelations> {
            val searchQuery = "%$query%"
            Timber.d("Creating paged devices source with search: $query")
            return deviceDao.getPagedDevicesWithRelationsBySearch(searchQuery)
        }

        /**
         * Insert a single device with all its related data.
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
         * Insert multiple devices with all their related data.
         * This uses transactions to ensure data consistency.
         */
        suspend fun insertDevices(devices: List<AndroidDevice>) {
            if (devices.isEmpty()) {
                Timber.w("Attempted to insert empty device list")
                return
            }

            Timber.i("Inserting ${devices.size} devices with relationships")
            try {
                val devicesWithRelations = devices.map { AndroidDeviceWithRelations.fromModel(it) }
                deviceDao.insertDevicesWithRelations(devicesWithRelations)
                Timber.i("Successfully inserted ${devices.size} devices")
            } catch (e: Exception) {
                Timber.e(e, "Failed to insert ${devices.size} devices")
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
                // Related data will be deleted automatically due to CASCADE foreign keys
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
    }
