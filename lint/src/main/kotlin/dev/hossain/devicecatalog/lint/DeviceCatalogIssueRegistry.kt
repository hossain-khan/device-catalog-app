package dev.hossain.devicecatalog.lint

import com.android.tools.lint.client.api.IssueRegistry
import com.android.tools.lint.client.api.Vendor
import com.android.tools.lint.detector.api.CURRENT_API

/**
 * Registers custom Android Lint checks for the Device Catalog app.
 */
class DeviceCatalogIssueRegistry : IssueRegistry() {
    override val issues =
        listOf(
            DesignSystemDetector.ISSUE,
            TestMethodNameDetector.FORMAT,
            TestMethodNameDetector.PREFIX,
        )

    override val api: Int = CURRENT_API

    override val minApi: Int = 12

    override val vendor: Vendor =
        Vendor(
            vendorName = "Device Catalog App",
            feedbackUrl = "https://github.com/hossain-khan/device-catalog-app/issues",
            contact = "https://github.com/hossain-khan/device-catalog-app",
        )
}
