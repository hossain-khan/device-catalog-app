package dev.hossain.devicecatalog.core.data

import androidx.paging.PagingSource
import dev.hossain.devicecatalog.core.database.AndroidDeviceDao
import dev.hossain.devicecatalog.core.database.AndroidDeviceEntity
import dev.hossain.devicecatalog.core.database.AndroidDeviceWithRelations
import dev.hossain.devicecatalog.core.testing.FakeAndroidDeviceDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class AndroidDeviceRepositoryTest {
    @Test
    fun `searchDevicesLatestFirst builds FTS prefix query for multi-token input`() =
        runTest {
            val recordingDao = RecordingFakeDao()
            val repository = AndroidDeviceRepository(recordingDao)

            val results = repository.searchDevicesLatestFirst("  Pixel   11  ").first()

            assertEquals("Pixel* 11*", recordingDao.lastFtsQuery)
            assertNull(recordingDao.lastLikeQuery)
            assertEquals(1, results.size)
            assertEquals("Pixel 11 Pro", results.first().androidDevice.modelName)
        }

    @Test
    fun `searchDevicesLatestFirst strips punctuation from FTS tokens`() =
        runTest {
            val recordingDao = RecordingFakeDao()
            val repository = AndroidDeviceRepository(recordingDao)

            repository.searchDevicesLatestFirst("Pixel+ 11!").first()

            assertEquals("Pixel* 11*", recordingDao.lastFtsQuery)
            assertNull(recordingDao.lastLikeQuery)
        }

    @Test
    fun `searchDevicesLatestFirst falls back to LIKE for punctuation-only query`() =
        runTest {
            val recordingDao = RecordingFakeDao()
            val repository = AndroidDeviceRepository(recordingDao)

            repository.searchDevicesLatestFirst("---").first()

            assertNull(recordingDao.lastFtsQuery)
            assertEquals("%---%", recordingDao.lastLikeQuery)
        }

    private class RecordingFakeDao(
        private val delegate: FakeAndroidDeviceDao = FakeAndroidDeviceDao(),
    ) : AndroidDeviceDao by delegate {
        var lastFtsQuery: String? = null
        var lastLikeQuery: String? = null

        init {
            delegate.setDevices(
                listOf(
                    AndroidDeviceWithRelations(
                        device =
                            AndroidDeviceEntity(
                                id = 1L,
                                brand = "google",
                                device = "grizzly",
                                manufacturer = "Google",
                                modelName = "Pixel 11 Pro",
                                ram = "12212-16415MB",
                                formFactor = "Phone",
                                processorName = "Tensor G5",
                                gpu = "DXT",
                            ),
                        abis = emptyList(),
                        openGlVersions = emptyList(),
                        screenDensities = emptyList(),
                        screenSizes = emptyList(),
                        sdkVersions = emptyList(),
                    ),
                ),
            )
        }

        override fun searchDevicesWithRelationsByFtsLatestFirst(ftsQuery: String): Flow<List<AndroidDeviceWithRelations>> {
            lastFtsQuery = ftsQuery
            return delegate.searchDevicesWithRelationsByFtsLatestFirst(ftsQuery)
        }

        override fun searchDevicesWithRelationsLatestFirst(search: String): Flow<List<AndroidDeviceWithRelations>> {
            lastLikeQuery = search
            return delegate.searchDevicesWithRelationsLatestFirst(search)
        }

        override fun getPagedDevicesWithRelationsByFtsLatestFirst(ftsQuery: String): PagingSource<Int, AndroidDeviceWithRelations> {
            lastFtsQuery = ftsQuery
            return delegate.getPagedDevicesWithRelationsByFtsLatestFirst(ftsQuery)
        }

        override fun getPagedDevicesWithRelationsBySearchLatestFirst(search: String): PagingSource<Int, AndroidDeviceWithRelations> {
            lastLikeQuery = search
            return delegate.getPagedDevicesWithRelationsBySearchLatestFirst(search)
        }
    }
}
