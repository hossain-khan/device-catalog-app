package dev.hossain.devicecatalog.db.converter

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.TypeConverter
import dev.hossain.devicecatalog.db.AndroidDeviceDao
import dev.hossain.devicecatalog.db.AndroidDeviceEntity
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class StringListConverter {
    private val json = Json { ignoreUnknownKeys = true }

    @TypeConverter
    fun fromStringList(list: List<String>): String {
        return json.encodeToString(list)
    }

    @TypeConverter
    fun toStringList(jsonString: String): List<String> {
        return json.decodeFromString(jsonString)
    }
}

class IntListConverter {
    private val json = Json { ignoreUnknownKeys = true }

    @TypeConverter
    fun fromIntList(list: List<Int>): String {
        return json.encodeToString(list)
    }

    @TypeConverter
    fun toIntList(jsonString: String): List<Int> {
        return json.decodeFromString(jsonString)
    }
}

@Database(entities = [AndroidDeviceEntity::class], version = 1, exportSchema = false)
@TypeConverters(StringListConverter::class, IntListConverter::class)
abstract class DeviceCatalogDatabase : RoomDatabase() {
    abstract fun androidDeviceDao(): AndroidDeviceDao

    companion object {
        @Volatile
        private var INSTANCE: DeviceCatalogDatabase? = null

        fun getDatabase(context: Context): DeviceCatalogDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DeviceCatalogDatabase::class.java,
                    "device_catalog_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}