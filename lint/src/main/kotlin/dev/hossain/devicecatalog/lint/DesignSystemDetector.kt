package dev.hossain.devicecatalog.lint

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.Category
import com.android.tools.lint.detector.api.Detector
import com.android.tools.lint.detector.api.Implementation
import com.android.tools.lint.detector.api.Issue
import com.android.tools.lint.detector.api.JavaContext
import com.android.tools.lint.detector.api.Scope
import com.android.tools.lint.detector.api.Severity
import org.jetbrains.uast.UCallExpression
import org.jetbrains.uast.UElement
import org.jetbrains.uast.UQualifiedReferenceExpression

/**
 * Checks for direct usages of Compose Material APIs where equivalents exist in `:core:designsystem`.
 */
class DesignSystemDetector :
    Detector(),
    Detector.UastScanner {
    override fun getApplicableUastTypes(): List<Class<out UElement>> =
        listOf(
            UCallExpression::class.java,
            UQualifiedReferenceExpression::class.java,
        )

    override fun createUastHandler(context: JavaContext): UElementHandler =
        object : UElementHandler() {
            override fun visitCallExpression(node: UCallExpression) {
                val name = node.methodName ?: return
                val preferredName = METHOD_NAMES[name] ?: return
                reportIssue(context, node, name, preferredName)
            }

            override fun visitQualifiedReferenceExpression(node: UQualifiedReferenceExpression) {
                val name = node.receiver.asRenderString()
                val preferredName = RECEIVER_NAMES[name] ?: return
                reportIssue(context, node, name, preferredName)
            }
        }

    companion object {
        @JvmField
        val ISSUE: Issue =
            Issue.create(
                id = "DesignSystem",
                briefDescription = "Design system",
                explanation =
                    "Highlights calls in code that use raw Compose Material composables " +
                        "instead of equivalents from the :core:designsystem module.",
                category = Category.CUSTOM_LINT_CHECKS,
                priority = 7,
                severity = Severity.WARNING,
                implementation =
                    Implementation(
                        DesignSystemDetector::class.java,
                        Scope.JAVA_FILE_SCOPE,
                    ),
            )

        val METHOD_NAMES =
            mapOf(
                "MaterialTheme" to "DeviceCatalogAppTheme",
                "TopAppBar" to "DeviceCatalogTopAppBar",
                "CenterAlignedTopAppBar" to "DeviceCatalogTopAppBar",
                "SmallTopAppBar" to "DeviceCatalogTopAppBar",
                "MediumTopAppBar" to "DeviceCatalogTabTopBar",
                "LargeTopAppBar" to "DeviceCatalogTabTopBar",
            )

        val RECEIVER_NAMES =
            mapOf(
                "Icons" to "DeviceCatalogIcons",
            )

        fun reportIssue(
            context: JavaContext,
            node: UElement,
            name: String,
            preferredName: String,
        ) {
            context.report(
                ISSUE,
                node,
                context.getLocation(node),
                "Using $name instead of $preferredName",
            )
        }
    }
}
