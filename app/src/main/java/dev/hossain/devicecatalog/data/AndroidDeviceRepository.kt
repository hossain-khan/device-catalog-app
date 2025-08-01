package dev.hossain.devicecatalog.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import dev.hossain.android.catalogparser.models.AndroidDevice
import dev.hossain.devicecatalog.db.AndroidDeviceDao
import dev.hossain.devicecatalog.db.AndroidDeviceEntity
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Inject
class AndroidDeviceRepository
    constructor(
        private val deviceDao: AndroidDeviceDao,
    ) {
        // Get paged list of devices
        fun getPagedDevices(): Flow<PagingData<AndroidDeviceEntity>> =
            Pager(
                config =
                    PagingConfig(
                        pageSize = 20,
                        enablePlaceholders = true,
                        maxSize = 100,
                    ),
            ) {
                deviceDao.getPagedDevices()
            }.flow

        // Get paged list of devices filtered by search query
        fun getPagedDevicesBySearch(query: String): Flow<PagingData<AndroidDeviceEntity>> {
            val searchQuery = "%$query%"
            return Pager(
                config =
                    PagingConfig(
                        pageSize = 20,
                        enablePlaceholders = true,
                        maxSize = 100,
                    ),
            ) {
                deviceDao.getPagedDevicesBySearch(searchQuery)
            }.flow
        }

        fun getAllDevices(): Flow<List<AndroidDevice>> = deviceDao.getAllDevices().map { it.map { it.toModel() } }

        suspend fun insertDevice(device: AndroidDeviceEntity): Long = deviceDao.insertDevice(device)

        suspend fun insertDevices(devices: List<AndroidDeviceEntity>) {
            deviceDao.insertDevices(devices)
        }
    }
