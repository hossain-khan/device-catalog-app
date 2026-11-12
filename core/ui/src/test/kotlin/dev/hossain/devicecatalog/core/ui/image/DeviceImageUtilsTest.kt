package dev.hossain.devicecatalog.core.ui.image

import dev.hossain.android.catalogparser.models.AndroidDevice
import dev.hossain.android.catalogparser.models.FormFactor
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DeviceImageUtilsTest {

    private val mockDevice = AndroidDevice(
        brand = "samsung",
        device = "herolte",
        manufacturer = "Samsung",
        modelName = "Galaxy S7",
        ram = "3709MB",
        formFactor = FormFactor.PHONE,
        processorName = "Samsung Exynos 8890",
        gpu = "ARM Mali T880",
        screenSizes = listOf("1080x1920"),
        screenDensities = listOf(480),
        abis = listOf("arm64-v8a"),
        sdkVersions = listOf(26),
        openGlEsVersions = listOf("3.2"),
    )

    @Test
    fun sanitizeFilenamePart_replacesSpecialCharactersWithUnderscores() {
        assertEquals(
            "foo_bar_baz_qux_test_demo",
            DeviceImageUtils.sanitizeFilenamePart("foo/bar\\baz:qux?test demo"),
        )
        assertEquals("", DeviceImageUtils.sanitizeFilenamePart(null))
        assertEquals("", DeviceImageUtils.sanitizeFilenamePart(""))
    }

    @Test
    fun getDeviceImageUrl_buildsStandardUrl() {
        assertEquals(
            "https://android-device-catalog.gohk.xyz/images/samsung_herolte.png",
            DeviceImageUtils.getDeviceImageUrl(mockDevice),
        )
    }

    @Test
    fun getDeviceImageCandidates_providesFallbackCandidates() {
        val candidates = DeviceImageUtils.getDeviceImageCandidates(mockDevice)

        assertTrue(candidates.contains("https://android-device-catalog.gohk.xyz/images/samsung_herolte.png"))
        assertTrue(candidates.contains("https://android-device-catalog.gohk.xyz/images/Samsung_herolte.png"))
        assertTrue(candidates.contains("https://android-device-catalog.gohk.xyz/images/samsung_Galaxy_S7.png"))
        assertTrue(candidates.contains("https://android-device-catalog.gohk.xyz/images/samsung_Galaxy_S7.webp"))
    }
}
