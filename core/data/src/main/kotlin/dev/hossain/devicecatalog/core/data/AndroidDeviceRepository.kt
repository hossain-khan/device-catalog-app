package dev.hossain.devicecatalog.core.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import dev.hossain.android.catalogparser.models.AndroidDevice
import dev.hossain.android.catalogparser.models.FormFactor
import dev.hossain.devicecatalog.core.database.AndroidDeviceDao
import dev.hossain.devicecatalog.core.database.AndroidDeviceEntity
import dev.hossain.devicecatalog.core.database.AndroidDeviceWithRelations
import dev.hossain.devicecatalog.core.model.DeviceInfo
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
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
         *
         * Performance optimizations:
         * - Page size: 30 items for optimal balance between network/DB calls and memory
         * - Initial load size: 45 items (1.5x page size) for faster initial display
         * - Prefetch distance: 15 items to ensure smooth scrolling
         * - Max size: 150 items to prevent excessive memory usage on low-end devices
         * - Placeholders enabled for better perceived performance
         */
        fun getPagedDevices(): Flow<PagingData<AndroidDeviceWithRelations>> {
            Timber.d("Creating paged devices flow with optimized pagination config")
            return Pager(
                config =
                    PagingConfig(
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

        /**
         * Get paged list of devices filtered by search query with relationships.
         *
         * Performance optimizations for search results:
         * - Smaller page size (25) since search results are typically smaller
         * - Reduced max size (120) to conserve memory during search
         * - Optimized for quick result display
         */
        fun getPagedDevicesBySearch(query: String): Flow<PagingData<AndroidDeviceWithRelations>> {
            val searchQuery = "%$query%"
            Timber.d("Creating paged devices flow with search: $query (optimized pagination)")
            return Pager(
                config =
                    PagingConfig(
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
         * Get manufacturers with minimum device count for quiz eligibility.
         * Only returns manufacturers that have enough devices with valid codename and model.
         */
        suspend fun getManufacturersWithMinDevices(minCount: Int = 5): List<ManufacturerQuizInfo> {
            Timber.d("Getting manufacturers with at least $minCount devices")
            return try {
                val devices = deviceDao.getAllDevicesWithRelations().map { devicesWithRelations ->
                    devicesWithRelations.map { it.toModel() }
                }.first()

                // Filter devices with valid codename and model
                val validDevices = devices.filter { device ->
                    device.androidDevice.device.isNotBlank() && device.androidDevice.modelName.isNotBlank()
                }

                // Group by manufacturer and count
                val manufacturerCounts = validDevices
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

                Timber.i("Found ${manufacturerCounts.size} manufacturers with at least $minCount devices")
                manufacturerCounts
            } catch (e: Exception) {
                Timber.e(e, "Failed to get manufacturers with min devices")
                emptyList()
            }
        }

        /**
         * Get devices by manufacturer for quiz generation.
         * Only returns devices with valid codename and model fields.
         */
        suspend fun getDevicesByManufacturer(manufacturer: String): List<DeviceInfo> {
            Timber.d("Getting devices for manufacturer: $manufacturer")
            return try {
                val devices = deviceDao.getAllDevicesWithRelations().map { devicesWithRelations ->
                    devicesWithRelations
                        .filter { it.device.manufacturer == manufacturer }
                        .map { it.toModel() }
                }.first()

                // Filter devices with valid codename and model
                val validDevices = devices.filter { device ->
                    device.androidDevice.device.isNotBlank() && device.androidDevice.modelName.isNotBlank()
                }

                Timber.i("Found ${validDevices.size} valid devices for manufacturer: $manufacturer")
                validDevices
            } catch (e: Exception) {
                Timber.e(e, "Failed to get devices for manufacturer: $manufacturer")
                emptyList()
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

                // RAM distribution analysis
                val ramDistribution =
                    devices
                        .groupingBy { it.androidDevice.ram }
                        .eachCount()
                        .map { RamCount(it.key, it.value) }
                        .sortedByDescending { it.count }

                // SDK version adoption metrics - get all unique SDK versions across all devices
                val sdkVersions = mutableMapOf<Int, Int>()
                devices.forEach { device ->
                    device.androidDevice.sdkVersions.forEach { sdkVersion ->
                        sdkVersions[sdkVersion] = sdkVersions.getOrDefault(sdkVersion, 0) + 1
                    }
                }
                val sdkVersionDistribution =
                    sdkVersions
                        .map { SdkVersionCount(it.key, it.value) }
                        .sortedByDescending { it.sdkVersion }

                // Screen density distribution
                val screenDensities = mutableMapOf<Int, Int>()
                devices.forEach { device ->
                    device.androidDevice.screenDensities.forEach { density ->
                        screenDensities[density] = screenDensities.getOrDefault(density, 0) + 1
                    }
                }
                val screenDensityDistribution =
                    screenDensities
                        .map { ScreenDensityCount(it.key, it.value) }
                        .sortedByDescending { it.density }

                // ABI support statistics
                val abiSupport = mutableMapOf<String, Int>()
                devices.forEach { device ->
                    device.androidDevice.abis.forEach { abi ->
                        abiSupport[abi] = abiSupport.getOrDefault(abi, 0) + 1
                    }
                }
                val abiDistribution =
                    abiSupport
                        .map { AbiCount(it.key, it.value) }
                        .sortedByDescending { it.count }

                // GPU distribution (top 10)
                val gpuDistribution =
                    devices
                        .groupingBy { it.androidDevice.gpu }
                        .eachCount()
                        .toList()
                        .sortedByDescending { it.second }
                        .take(10)
                        .map { GpuCount(it.first, it.second) }

                Timber.d(
                    "Device stats - Total: $totalDevices, Form factors: $totalFormFactors, Top manufacturers: ${topManufacturers.size}, " +
                        "RAM types: ${ramDistribution.size}, SDK versions: ${sdkVersionDistribution.size}, ABIs: ${abiDistribution.size}",
                )

                DeviceStats(
                    totalDevices = totalDevices,
                    totalFormFactors = totalFormFactors,
                    formFactorBreakdown = formFactors.map { FormFactorCount(it.key, it.value) },
                    topManufacturers = topManufacturers,
                    ramDistribution = ramDistribution,
                    sdkVersionDistribution = sdkVersionDistribution,
                    screenDensityDistribution = screenDensityDistribution,
                    abiDistribution = abiDistribution,
                    gpuDistribution = gpuDistribution,
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
    val ramDistribution: List<RamCount>,
    val sdkVersionDistribution: List<SdkVersionCount>,
    val screenDensityDistribution: List<ScreenDensityCount>,
    val abiDistribution: List<AbiCount>,
    val gpuDistribution: List<GpuCount>,
)

/**
 * Data class for form factor count statistics.
 */
data class FormFactorCount(
    val formFactor: FormFactor,
    val count: Int,
) {
    /**
     * Calculate percentage of total devices.
     */
    fun percentage(totalDevices: Int): Float = if (totalDevices > 0) (count.toFloat() / totalDevices) * 100f else 0f
}

/**
 * Data class for manufacturer count statistics.
 */
data class ManufacturerCount(
    val manufacturer: String,
    val count: Int,
) {
    /**
     * Calculate percentage of total devices.
     */
    fun percentage(totalDevices: Int): Float = if (totalDevices > 0) (count.toFloat() / totalDevices) * 100f else 0f
}

/**
 * Data class for RAM distribution statistics.
 */
data class RamCount(
    val ram: String,
    val count: Int,
) {
    /**
     * Calculate percentage of total devices.
     */
    fun percentage(totalDevices: Int): Float = if (totalDevices > 0) (count.toFloat() / totalDevices) * 100f else 0f
}

/**
 * Data class for SDK version adoption statistics.
 */
data class SdkVersionCount(
    val sdkVersion: Int,
    val count: Int,
) {
    /**
     * Calculate percentage of total devices.
     */
    fun percentage(totalDevices: Int): Float = if (totalDevices > 0) (count.toFloat() / totalDevices) * 100f else 0f
}

/**
 * Data class for screen density distribution statistics.
 */
data class ScreenDensityCount(
    val density: Int,
    val count: Int,
) {
    /**
     * Calculate percentage of total devices.
     */
    fun percentage(totalDevices: Int): Float = if (totalDevices > 0) (count.toFloat() / totalDevices) * 100f else 0f
}

/**
 * Data class for ABI support statistics.
 */
data class AbiCount(
    val abi: String,
    val count: Int,
) {
    /**
     * Calculate percentage of total devices.
     */
    fun percentage(totalDevices: Int): Float = if (totalDevices > 0) (count.toFloat() / totalDevices) * 100f else 0f
}

/**
 * Data class for GPU distribution statistics.
 */
data class GpuCount(
    val gpu: String,
    val count: Int,
) {
    /**
     * Calculate percentage of total devices.
     */
    fun percentage(totalDevices: Int): Float = if (totalDevices > 0) (count.toFloat() / totalDevices) * 100f else 0f
}

/**
 * Data class for manufacturer quiz information.
 */
data class ManufacturerQuizInfo(
    val manufacturer: String,
    val deviceCount: Int,
    val isQuizAvailable: Boolean,
)
