package dev.hossain.devicecatalog.db.converter

import androidx.room.TypeConverter
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
