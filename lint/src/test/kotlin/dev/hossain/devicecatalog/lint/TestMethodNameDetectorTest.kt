package dev.hossain.devicecatalog.lint

import com.android.tools.lint.checks.infrastructure.TestFiles.kotlin
import com.android.tools.lint.checks.infrastructure.TestLintTask.lint
import org.junit.Test

class TestMethodNameDetectorTest {
    @Test
    fun `detects test prefix in unit test method`() {
        lint()
            .issues(TestMethodNameDetector.PREFIX)
            .allowMissingSdk()
            .files(
                JUNIT_TEST_STUB,
                kotlin(
                    """
                    import org.junit.Test

                    class SampleTest {
                        @Test
                        fun testSomething() = Unit
                    }
                    """.trimIndent(),
                ).indented(),
            ).run()
            .expect(
                """
                src/SampleTest.kt:5: Warning: Test method starts with test [TestMethodPrefix]
                    fun testSomething() = Unit
                        ~~~~~~~~~~~~~
                0 errors, 1 warning
                """.trimIndent(),
            )
    }

    @Test
    fun `allows descriptive backtick unit test names`() {
        lint()
            .issues(TestMethodNameDetector.PREFIX, TestMethodNameDetector.FORMAT)
            .allowMissingSdk()
            .files(
                JUNIT_TEST_STUB,
                kotlin(
                    """
                    import org.junit.Test

                    class SampleTest {
                        @Test
                        fun `renders devices sorted by score`() = Unit
                    }
                    """.trimIndent(),
                ).indented(),
            ).run()
            .expectClean()
    }

    private companion object {
        private val JUNIT_TEST_STUB =
            kotlin(
                """
                package org.junit
                annotation class Test
                """.trimIndent(),
            ).indented()
    }
}
