package dev.hossain.devicecatalog.lint

import com.android.tools.lint.detector.api.AnnotationInfo
import com.android.tools.lint.detector.api.AnnotationUsageInfo
import com.android.tools.lint.detector.api.Category.Companion.TESTING
import com.android.tools.lint.detector.api.Detector
import com.android.tools.lint.detector.api.Implementation
import com.android.tools.lint.detector.api.Issue
import com.android.tools.lint.detector.api.JavaContext
import com.android.tools.lint.detector.api.LintFix
import com.android.tools.lint.detector.api.Scope.JAVA_FILE
import com.android.tools.lint.detector.api.Scope.TEST_SOURCES
import com.android.tools.lint.detector.api.Severity.WARNING
import com.android.tools.lint.detector.api.SourceCodeScanner
import com.android.tools.lint.detector.api.TextFormat.RAW
import com.intellij.psi.PsiMethod
import org.jetbrains.uast.UElement
import java.util.EnumSet
import kotlin.io.path.Path

/**
 * Checks for common naming patterns in test methods:
 * - [detectPrefix] flags unnecessary `test` prefix in unit tests.
 * - [detectFormat] checks `given_when_then` format in `androidTest` instrumented tests.
 */
class TestMethodNameDetector :
    Detector(),
    SourceCodeScanner {
    override fun applicableAnnotations() = listOf("org.junit.Test")

    override fun visitAnnotationUsage(
        context: JavaContext,
        element: UElement,
        annotationInfo: AnnotationInfo,
        usageInfo: AnnotationUsageInfo,
    ) {
        val method = usageInfo.referenced as? PsiMethod ?: return

        method.detectPrefix(context, usageInfo)
        method.detectFormat(context, usageInfo)
    }

    private fun JavaContext.isAndroidTest() = Path("androidTest") in file.toPath()

    private fun PsiMethod.detectPrefix(
        context: JavaContext,
        usageInfo: AnnotationUsageInfo,
    ) {
        if (!name.startsWith("test")) return
        context.report(
            issue = PREFIX,
            scope = usageInfo.usage,
            location = context.getNameLocation(this),
            message = PREFIX.getBriefDescription(RAW),
            quickfixData =
                LintFix
                    .create()
                    .name("Remove prefix")
                    .replace()
                    .pattern("""test[\s_]*""")
                    .with("")
                    .autoFix()
                    .build(),
        )
    }

    private fun PsiMethod.detectFormat(
        context: JavaContext,
        usageInfo: AnnotationUsageInfo,
    ) {
        if (!context.isAndroidTest()) return
        if ("""[^\W_]+(_[^\W_]+){1,2}""".toRegex().matches(name)) return
        context.report(
            issue = FORMAT,
            scope = usageInfo.usage,
            location = context.getNameLocation(this),
            message = FORMAT.getBriefDescription(RAW),
        )
    }

    companion object {
        private fun issue(
            id: String,
            briefDescription: String,
            explanation: String,
        ): Issue =
            Issue.create(
                id = id,
                briefDescription = briefDescription,
                explanation = explanation,
                category = TESTING,
                priority = 5,
                severity = WARNING,
                implementation =
                    Implementation(
                        TestMethodNameDetector::class.java,
                        EnumSet.of(JAVA_FILE, TEST_SOURCES),
                    ),
            )

        @JvmField
        val PREFIX: Issue =
            issue(
                id = "TestMethodPrefix",
                briefDescription = "Test method starts with `test`",
                explanation = "Test method should not start with `test`.",
            )

        @JvmField
        val FORMAT: Issue =
            issue(
                id = "TestMethodFormat",
                briefDescription = "Test method does not follow `given_when_then` format",
                explanation =
                    "Instrumented test methods should follow the `when_then` or `given_when_then` naming format.",
            )
    }
}
