package dev.hossain.devicecatalog.core.testing

import dev.hossain.android.catalogparser.models.AndroidDevice
import dev.hossain.android.catalogparser.models.FormFactor
import dev.hossain.devicecatalog.core.database.AndroidDeviceEntity
import dev.hossain.devicecatalog.core.database.AndroidDeviceWithRelations
import dev.hossain.devicecatalog.core.database.DeviceAbi
import dev.hossain.devicecatalog.core.database.DeviceOpenGl
import dev.hossain.devicecatalog.core.database.DeviceScreenDensity
import dev.hossain.devicecatalog.core.database.DeviceScreenSize
import dev.hossain.devicecatalog.core.database.DeviceSdkVersion
import dev.hossain.devicecatalog.core.model.DeviceInfo

/**
 * Factory for creating test device data.
 * Provides realistic sample devices for testing purposes.
 */
object TestDeviceFactory {
    
    /**
     * Creates a test AndroidDevice with customizable properties.
     * Note: FormFactor parameter removed from defaults to avoid Java version compatibility issues.
     */
    fun createAndroidDevice(
        brand: String = "Google",
        device: String = "redfin",
        manufacturer: String = "Google",
        modelName: String = "Pixel 5",
        ram: String = "8GB",
        formFactor: FormFactor? = null,
        processorName: String = "Qualcomm Snapdragon 765G",
        gpu: String = "Adreno 620",
        screenSizes: List<String> = listOf("normal", "long"),
        screenDensities: List<Int> = listOf(440),
        abis: List<String> = listOf("arm64-v8a", "armeabi-v7a"),
        sdkVersions: List<Int> = listOf(30, 31, 32, 33),
        openGlEsVersions: List<String> = listOf("3.2"),
    ): AndroidDevice = AndroidDevice(
        brand = brand,
        device = device,
        manufacturer = manufacturer,
        modelName = modelName,
        ram = ram,
        formFactor = formFactor ?: FormFactor.PHONE,
        processorName = processorName,
        gpu = gpu,
        screenSizes = screenSizes,
        screenDensities = screenDensities,
        abis = abis,
        sdkVersions = sdkVersions,
        openGlEsVersions = openGlEsVersions,
    )
    
    /**
     * Creates a test DeviceInfo with customizable properties.
     */
    fun createDeviceInfo(
        id: Long = 1,
        androidDevice: AndroidDevice? = null,
    ): DeviceInfo = DeviceInfo(
        id = id,
        androidDevice = androidDevice ?: createAndroidDevice(),
    )
    
    /**
     * Creates a test AndroidDeviceEntity with customizable properties.
     */
    fun createDeviceEntity(
        id: Long = 1,
        brand: String = "Google",
        device: String = "redfin",
        manufacturer: String = "Google",
        modelName: String = "Pixel 5",
        ram: String = "8GB",
        formFactor: String = "phone",
        processorName: String = "Qualcomm Snapdragon 765G",
        gpu: String = "Adreno 620",
    ): AndroidDeviceEntity = AndroidDeviceEntity(
        id = id,
        brand = brand,
        device = device,
        manufacturer = manufacturer,
        modelName = modelName,
        ram = ram,
        formFactor = formFactor,
        processorName = processorName,
        gpu = gpu,
    )
    
    /**
     * Creates a test AndroidDeviceWithRelations with customizable properties.
     */
    fun createDeviceWithRelations(
        id: Long = 1,
        brand: String = "Google",
        device: String = "redfin",
        manufacturer: String = "Google",
        modelName: String = "Pixel 5",
        ram: String = "8GB",
        formFactor: String = "phone",
        processorName: String = "Qualcomm Snapdragon 765G",
        gpu: String = "Adreno 620",
        abis: List<String> = listOf("arm64-v8a", "armeabi-v7a"),
        openGlVersions: List<String> = listOf("3.2"),
        screenDensities: List<Int> = listOf(440),
        screenSizes: List<String> = listOf("normal", "long"),
        sdkVersions: List<Int> = listOf(30, 31, 32, 33),
    ): AndroidDeviceWithRelations = AndroidDeviceWithRelations(
        device = createDeviceEntity(
            id = id,
            brand = brand,
            device = device,
            manufacturer = manufacturer,
            modelName = modelName,
            ram = ram,
            formFactor = formFactor,
            processorName = processorName,
            gpu = gpu,
        ),
        abis = abis.map { DeviceAbi(device_id = id, abi = it) },
        openGlVersions = openGlVersions.map { DeviceOpenGl(device_id = id, opengl_version = it) },
        screenDensities = screenDensities.map { DeviceScreenDensity(device_id = id, screen_density = it) },
        screenSizes = screenSizes.map { DeviceScreenSize(device_id = id, screen_size = it) },
        sdkVersions = sdkVersions.map { DeviceSdkVersion(device_id = id, sdk_version = it) },
    )
    
    /**
     * Creates a list of sample devices for testing.
     */
    fun createSampleDevices(): List<DeviceInfo> = listOf(
        DeviceInfo(
            id = 1,
            androidDevice = AndroidDevice(
                brand = "Google",
                device = "redfin",
                manufacturer = "Google",
                modelName = "Pixel 5",
                ram = "8GB",
                formFactor = FormFactor.PHONE,
                processorName = "Qualcomm Snapdragon 765G",
                gpu = "Adreno 620",
                screenSizes = listOf("normal", "long"),
                screenDensities = listOf(440),
                abis = listOf("arm64-v8a", "armeabi-v7a"),
                sdkVersions = listOf(30, 31, 32, 33),
                openGlEsVersions = listOf("3.2"),
            ),
        ),
        DeviceInfo(
            id = 2,
            androidDevice = AndroidDevice(
                brand = "Samsung",
                device = "a52xq",
                manufacturer = "Samsung",
                modelName = "Galaxy A52",
                ram = "6GB",
                formFactor = FormFactor.PHONE,
                processorName = "Qualcomm Snapdragon 720G",
                gpu = "Adreno 618",
                screenSizes = listOf("normal", "long"),
                screenDensities = listOf(440),
                abis = listOf("arm64-v8a", "armeabi-v7a"),
                sdkVersions = listOf(30, 31, 32, 33),
                openGlEsVersions = listOf("3.2"),
            ),
        ),
        DeviceInfo(
            id = 3,
            androidDevice = AndroidDevice(
                brand = "OnePlus",
                device = "OnePlus9",
                manufacturer = "OnePlus",
                modelName = "OnePlus 9",
                ram = "12GB",
                formFactor = FormFactor.PHONE,
                processorName = "Qualcomm Snapdragon 888",
                gpu = "Adreno 660",
                screenSizes = listOf("normal", "long"),
                screenDensities = listOf(440),
                abis = listOf("arm64-v8a", "armeabi-v7a"),
                sdkVersions = listOf(30, 31, 32, 33, 34),
                openGlEsVersions = listOf("3.2"),
            ),
        ),
    )
    
    /**
     * Creates a list of sample devices with relations for testing.
     */
    fun createSampleDevicesWithRelations(): List<AndroidDeviceWithRelations> = listOf(
        createDeviceWithRelations(
            id = 1,
            brand = "Google",
            device = "redfin",
            manufacturer = "Google",
            modelName = "Pixel 5",
        ),
        createDeviceWithRelations(
            id = 2,
            brand = "Samsung",
            device = "a52xq",
            manufacturer = "Samsung",
            modelName = "Galaxy A52",
            ram = "6GB",
            processorName = "Qualcomm Snapdragon 720G",
            gpu = "Adreno 618",
        ),
        createDeviceWithRelations(
            id = 3,
            brand = "OnePlus",
            device = "OnePlus9",
            manufacturer = "OnePlus",
            modelName = "OnePlus 9",
            ram = "12GB",
            processorName = "Qualcomm Snapdragon 888",
            gpu = "Adreno 660",
            sdkVersions = listOf(30, 31, 32, 33, 34),
        ),
    )
}
