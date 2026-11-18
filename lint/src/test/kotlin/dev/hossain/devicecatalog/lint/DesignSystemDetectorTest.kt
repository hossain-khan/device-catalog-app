package dev.hossain.devicecatalog.lint

import com.android.tools.lint.checks.infrastructure.TestFile
import com.android.tools.lint.checks.infrastructure.TestFiles.kotlin
import com.android.tools.lint.checks.infrastructure.TestLintTask.lint
import dev.hossain.devicecatalog.lint.DesignSystemDetector.Companion.ISSUE
import dev.hossain.devicecatalog.lint.DesignSystemDetector.Companion.METHOD_NAMES
import dev.hossain.devicecatalog.lint.DesignSystemDetector.Companion.RECEIVER_NAMES
import org.junit.Test

class DesignSystemDetectorTest {
    @Test
    fun `detects Material composables with design system equivalents`() {
        lint()
            .issues(ISSUE)
            .allowMissingSdk()
            .files(
                COMPOSABLE_STUB,
                STUBS,
                @Suppress("LintImplTrimIndent")
                kotlin(
                    """
                    |import androidx.compose.runtime.Composable
                    |
                    |@Composable
                    |fun App() {
                    ${METHOD_NAMES.keys.joinToString("\n") { "|    $it()" }}
                    |}
                    """.trimMargin(),
                ).indented(),
            ).run()
            .expect(
                """
                src/test.kt:5: Warning: Using MaterialTheme instead of DeviceCatalogAppTheme [DesignSystem]
                    MaterialTheme()
                    ~~~~~~~~~~~~~~~
                src/test.kt:6: Warning: Using TopAppBar instead of DeviceCatalogTopAppBar [DesignSystem]
                    TopAppBar()
                    ~~~~~~~~~~~
                src/test.kt:7: Warning: Using CenterAlignedTopAppBar instead of DeviceCatalogTopAppBar [DesignSystem]
                    CenterAlignedTopAppBar()
                    ~~~~~~~~~~~~~~~~~~~~~~~~
                src/test.kt:8: Warning: Using SmallTopAppBar instead of DeviceCatalogTopAppBar [DesignSystem]
                    SmallTopAppBar()
                    ~~~~~~~~~~~~~~~~
                src/test.kt:9: Warning: Using MediumTopAppBar instead of DeviceCatalogTabTopBar [DesignSystem]
                    MediumTopAppBar()
                    ~~~~~~~~~~~~~~~~~
                src/test.kt:10: Warning: Using LargeTopAppBar instead of DeviceCatalogTabTopBar [DesignSystem]
                    LargeTopAppBar()
                    ~~~~~~~~~~~~~~~~
                0 errors, 6 warnings
                """.trimIndent(),
            )
    }

    @Test
    fun `detects raw Icons receiver usage`() {
        lint()
            .issues(ISSUE)
            .allowMissingSdk()
            .files(
                COMPOSABLE_STUB,
                STUBS,
                @Suppress("LintImplTrimIndent")
                kotlin(
                    """
                    |import androidx.compose.runtime.Composable
                    |
                    |@Composable
                    |fun App() {
                    ${RECEIVER_NAMES.keys.joinToString("\n") { "|    $it.Filled.Search" }}
                    |}
                    """.trimMargin(),
                ).indented(),
            ).run()
            .expect(
                """
                src/test.kt:5: Warning: Using Icons instead of DeviceCatalogIcons [DesignSystem]
                    Icons.Filled.Search
                    ~~~~~~~~~~~~
                0 errors, 1 warning
                """.trimIndent(),
            )
    }

    private companion object {
        private val COMPOSABLE_STUB: TestFile =
            kotlin(
                """
                package androidx.compose.runtime
                annotation class Composable
                """.trimIndent(),
            ).indented()

        private val STUBS: TestFile =
            kotlin(
                """
                |import androidx.compose.runtime.Composable
                |
                ${METHOD_NAMES.keys.joinToString("\n") { "|@Composable fun $it() = Unit" }}
                |object Icons {
                |    object Filled {
                |        val Search = Unit
                |    }
                |}
                """.trimMargin(),
            ).indented()
    }
}
