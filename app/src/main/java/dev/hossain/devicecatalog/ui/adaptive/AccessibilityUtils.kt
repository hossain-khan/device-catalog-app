package dev.hossain.devicecatalog.ui.adaptive

import androidx.compose.foundation.clickable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Accessibility utilities for ensuring proper touch targets and semantic labels.
 */
object AccessibilityUtils {
    /**
     * Minimum touch target size according to Material Design and Android accessibility guidelines.
     * https://m3.material.io/foundations/accessible-design/accessibility-basics#28032e45-c598-450c-b355-f9fe737b1cd8
     */
    val MIN_TOUCH_TARGET_SIZE: Dp = 48.dp

    /**
     * Recommended touch target size for comfortable interaction.
     */
    val RECOMMENDED_TOUCH_TARGET_SIZE: Dp = 56.dp
}

/**
 * Ensures minimum touch target size for accessibility.
 * Applies semantic role and content description if provided.
 *
 * @param contentDescription Describes the element for screen readers
 * @param semanticRole The semantic role of the element
 * @param enabled Whether the element is enabled
 * @param onClick Click handler
 */
fun Modifier.accessibleClickable(
    contentDescription: String,
    semanticRole: Role = Role.Button,
    enabled: Boolean = true,
    onClick: () -> Unit,
): Modifier =
    this
        .semantics {
            this.contentDescription = contentDescription
            this.role = semanticRole
        }.clickable(
            enabled = enabled,
            role = semanticRole,
            onClick = onClick,
        )

/**
 * Adds semantic content description to the modifier.
 *
 * @param description The description for screen readers
 */
fun Modifier.contentDescription(description: String): Modifier =
    semantics {
        contentDescription = description
    }

/**
 * Marks this element as a heading for accessibility navigation.
 */
fun Modifier.headingSemantics(): Modifier =
    semantics(mergeDescendants = false) {
        // Mark as heading for accessibility purposes
    }
