package dev.hossain.devicecatalog.db

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface AndroidDeviceDao {
    // ----------------------------------------------------------------
    // Basic device operations
    // ----------------------------------------------------------------

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDevice(device: AndroidDeviceEntity): Long

    @Update
    suspend fun updateDevice(device: AndroidDeviceEntity)

    @Query("SELECT * FROM device WHERE _id = :deviceId")
    suspend fun getDeviceById(deviceId: Long): AndroidDeviceEntity?

    @Query("DELETE FROM device WHERE _id = :deviceId")
    suspend fun deleteDevice(deviceId: Long)

    @Query("DELETE FROM device")
    suspend fun deleteAllDevices()

    // ----------------------------------------------------------------
    // Relationship queries - following Room one-to-many pattern
    // ----------------------------------------------------------------

    @Transaction
    @Query("SELECT * FROM device")
    fun getAllDevicesWithRelations(): Flow<List<AndroidDeviceWithRelations>>

    @Transaction
    @Query("SELECT * FROM device WHERE _id = :deviceId")
    suspend fun getDeviceWithRelationsById(deviceId: Long): AndroidDeviceWithRelations?

    @Transaction
    @Query("SELECT * FROM device WHERE brand = :brand AND device = :device AND manufacturer = :manufacturer AND model_name = :modelName LIMIT 1")
    suspend fun getDeviceByProperties(
        brand: String,
        device: String,
        manufacturer: String,
        modelName: String
    ): AndroidDeviceWithRelations?

    @Transaction
    @Query("SELECT * FROM device WHERE manufacturer LIKE :search OR model_name LIKE :search")
    fun searchDevicesWithRelations(search: String): Flow<List<AndroidDeviceWithRelations>>

    // ----------------------------------------------------------------
    // Paging queries with relationships
    // ----------------------------------------------------------------

    @Transaction
    @Query("SELECT * FROM device ORDER BY manufacturer ASC")
    fun getPagedDevicesWithRelations(): PagingSource<Int, AndroidDeviceWithRelations>

    @Transaction
    @Query("SELECT * FROM device WHERE manufacturer LIKE :search OR model_name LIKE :search ORDER BY manufacturer ASC")
    fun getPagedDevicesWithRelationsBySearch(search: String): PagingSource<Int, AndroidDeviceWithRelations>

    // ----------------------------------------------------------------
    // Bulk insert operations with transactions
    // ----------------------------------------------------------------

    @Transaction
    suspend fun insertDeviceWithRelations(deviceWithRelations: AndroidDeviceWithRelations): Long {
        val deviceId = insertDevice(deviceWithRelations.device)

        // Insert related entities with the correct device_id
        deviceWithRelations.abis.forEach { abi ->
            insertDeviceAbi(abi.copy(device_id = deviceId))
        }
        deviceWithRelations.openGlVersions.forEach { openGl ->
            insertDeviceOpenGlVersion(openGl.copy(device_id = deviceId))
        }
        deviceWithRelations.screenDensities.forEach { density ->
            insertDeviceScreenDensity(density.copy(device_id = deviceId))
        }
        deviceWithRelations.screenSizes.forEach { size ->
            insertDeviceScreenSize(size.copy(device_id = deviceId))
        }
        deviceWithRelations.sdkVersions.forEach { sdk ->
            insertDeviceSdkVersion(sdk.copy(device_id = deviceId))
        }

        return deviceId
    }

    @Transaction
    suspend fun insertDevicesWithRelations(devicesWithRelations: List<AndroidDeviceWithRelations>) {
        devicesWithRelations.forEach { deviceWithRelations ->
            insertDeviceWithRelations(deviceWithRelations)
        }
    }

    // ----------------------------------------------------------------
    // Individual related entity operations
    // ----------------------------------------------------------------

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDeviceAbi(deviceAbi: DeviceAbi)

    @Query("SELECT * FROM device_abi WHERE device_id = :deviceId")
    fun getDeviceAbis(deviceId: Long): Flow<List<DeviceAbi>>

    @Query("SELECT * FROM device_abi")
    fun getAllDeviceAbis(): Flow<List<DeviceAbi>>

    @Query("DELETE FROM device_abi")
    suspend fun deleteAllDeviceAbis()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDeviceOpenGlVersion(deviceOpenGl: DeviceOpenGl)

    @Query("SELECT * FROM device_opengl WHERE device_id = :deviceId")
    fun getDeviceOpenGlVersions(deviceId: Long): Flow<List<DeviceOpenGl>>

    @Query("SELECT * FROM device_opengl")
    fun getAllDeviceOpenGlVersions(): Flow<List<DeviceOpenGl>>

    @Query("DELETE FROM device_opengl")
    suspend fun deleteAllDeviceOpenGlVersions()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDeviceScreenDensity(deviceScreenDensity: DeviceScreenDensity)

    @Query("SELECT * FROM device_screen_density WHERE device_id = :deviceId")
    fun getDeviceScreenDensities(deviceId: Long): Flow<List<DeviceScreenDensity>>

    @Query("SELECT * FROM device_screen_density")
    fun getAllDeviceScreenDensities(): Flow<List<DeviceScreenDensity>>

    @Query("DELETE FROM device_screen_density")
    suspend fun deleteAllDeviceScreenDensities()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDeviceScreenSize(deviceScreenSize: DeviceScreenSize)

    @Query("SELECT * FROM device_screen_size WHERE device_id = :deviceId")
    fun getDeviceScreenSizes(deviceId: Long): Flow<List<DeviceScreenSize>>

    @Query("SELECT * FROM device_screen_size")
    fun getAllDeviceScreenSizes(): Flow<List<DeviceScreenSize>>

    @Query("DELETE FROM device_screen_size")
    suspend fun deleteAllDeviceScreenSizes()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDeviceSdkVersion(deviceSdkVersion: DeviceSdkVersion)

    @Query("SELECT * FROM device_sdk WHERE device_id = :deviceId")
    fun getDeviceSdkVersions(deviceId: Long): Flow<List<DeviceSdkVersion>>

    @Query("SELECT * FROM device_sdk")
    fun getAllDeviceSdkVersions(): Flow<List<DeviceSdkVersion>>

    @Query("DELETE FROM device_sdk")
    suspend fun deleteAllDeviceSdkVersions()
}
