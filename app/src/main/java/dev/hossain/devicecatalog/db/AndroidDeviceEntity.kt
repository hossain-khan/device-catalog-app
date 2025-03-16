package dev.hossain.devicecatalog.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import dev.hossain.android.catalogparser.models.AndroidDevice
import dev.hossain.devicecatalog.db.converter.IntListConverter
import dev.hossain.devicecatalog.db.converter.StringListConverter

@Entity(tableName = "android_devices")
@TypeConverters(StringListConverter::class, IntListConverter::class)
data class AndroidDeviceEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val brand: String,
    val device: String,
    val manufacturer: String,
    val modelName: String,
    val ram: String,
    val formFactor: String,
    val processorName: String,
    val gpu: String,
    val screenSizes: List<String>,
    val screenDensities: List<Int>,
    val abis: List<String>,
    val sdkVersions: List<Int>,
    val openGlEsVersions: List<String>,
) {
    fun toModel(): AndroidDevice =
        AndroidDevice(
            brand = brand,
            device = device,
            manufacturer = manufacturer,
            modelName = modelName,
            ram = ram,
            formFactor = formFactor,
            processorName = processorName,
            gpu = gpu,
            screenSizes = screenSizes,
            screenDensities = screenDensities,
            abis = abis,
            sdkVersions = sdkVersions,
            openGlEsVersions = openGlEsVersions,
        )

    companion object {
        fun fromModel(model: AndroidDevice): AndroidDeviceEntity =
            AndroidDeviceEntity(
                brand = model.brand,
                device = model.device,
                manufacturer = model.manufacturer,
                modelName = model.modelName,
                ram = model.ram,
                formFactor = model.formFactor,
                processorName = model.processorName,
                gpu = model.gpu,
                screenSizes = model.screenSizes,
                screenDensities = model.screenDensities,
                abis = model.abis,
                sdkVersions = model.sdkVersions,
                openGlEsVersions = model.openGlEsVersions,
            )
    }
}
