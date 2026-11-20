package androidx.test.uiautomator

import androidx.test.uiautomator.HasChildrenOp.AT_LEAST
import androidx.test.uiautomator.HasChildrenOp.AT_MOST
import androidx.test.uiautomator.HasChildrenOp.EXACTLY

// These helpers live in the androidx.test.uiautomator package because
// UiObject2Condition has a package-private constructor.

/**
 * Condition satisfied when a [UiObject2] has the specified number of children.
 */
fun untilHasChildren(
    childCount: Int = 1,
    op: HasChildrenOp = AT_LEAST,
): UiObject2Condition<Boolean> =
    object : UiObject2Condition<Boolean>() {
        override fun apply(element: UiObject2): Boolean =
            when (op) {
                AT_LEAST -> element.childCount >= childCount
                EXACTLY -> element.childCount == childCount
                AT_MOST -> element.childCount <= childCount
            }
    }

enum class HasChildrenOp {
    AT_LEAST,
    EXACTLY,
    AT_MOST,
}
