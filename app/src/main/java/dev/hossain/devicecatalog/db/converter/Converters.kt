package dev.hossain.devicecatalog.db.converter

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import dev.hossain.devicecatalog.db.AndroidDeviceDao
import dev.hossain.devicecatalog.db.AndroidDeviceEntity
import kotlinx.serialization.json.Json

class StringListConverter {
    private val json = Json { ignoreUnknownKeys = true }

    @TypeConverter
    fun fromStringList(list: List<String>): String = json.encodeToString(list)

    @TypeConverter
    fun toStringList(jsonString: String): List<String> = json.decodeFromString(jsonString)
}

class IntListConverter {
    private val json = Json { ignoreUnknownKeys = true }

    @TypeConverter
    fun fromIntList(list: List<Int>): String = json.encodeToString(list)

    @TypeConverter
    fun toIntList(jsonString: String): List<Int> = json.decodeFromString(jsonString)
}

@Database(entities = [AndroidDeviceEntity::class], version = 1, exportSchema = false)
@TypeConverters(StringListConverter::class, IntListConverter::class)
abstract class DeviceCatalogDatabase : RoomDatabase() {
    abstract fun androidDeviceDao(): AndroidDeviceDao

    companion object {
        @Volatile
        private var instance: DeviceCatalogDatabase? = null

        fun getDatabase(context: Context): DeviceCatalogDatabase =
            instance ?: synchronized(this) {
                val instance =
                    Room
                        .databaseBuilder(
                            context.applicationContext,
                            DeviceCatalogDatabase::class.java,
                            "device_catalog_database",
                        ).build()
                Companion.instance = instance
                instance
            }
    }
}
