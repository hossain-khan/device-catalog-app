package dev.hossain.devicecatalog.db

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface AndroidDeviceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDevice(device: AndroidDeviceEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDevices(devices: List<AndroidDeviceEntity>)

    @Update
    suspend fun updateDevice(device: AndroidDeviceEntity)

    // Regular Flow query for non-paged results
    @Query("SELECT * FROM android_devices")
    fun getAllDevices(): Flow<List<AndroidDeviceEntity>>

    // Paged query that returns a PagingSource
    @Query("SELECT * FROM android_devices ORDER BY manufacturer ASC")
    fun getPagedDevices(): PagingSource<Int, AndroidDeviceEntity>

    // Paged query with search functionality
    @Query("SELECT * FROM android_devices WHERE manufacturer LIKE :search OR modelName LIKE :search ORDER BY manufacturer ASC")
    fun getPagedDevicesBySearch(search: String): PagingSource<Int, AndroidDeviceEntity>

    @Query("SELECT * FROM android_devices WHERE id = :deviceId")
    suspend fun getDeviceById(deviceId: Long): AndroidDeviceEntity?

    @Query("SELECT * FROM android_devices WHERE manufacturer LIKE :search OR modelName LIKE :search")
    fun searchDevices(search: String): Flow<List<AndroidDeviceEntity>>

    @Query("DELETE FROM android_devices WHERE id = :deviceId")
    suspend fun deleteDevice(deviceId: Long)

    @Query("DELETE FROM android_devices")
    suspend fun deleteAllDevices()
}