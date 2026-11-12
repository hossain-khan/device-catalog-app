package dev.hossain.devicecatalog.feature.phonequiz.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Answer option card with smooth animated visual feedback for selection and correctness.
 */
@Composable
fun AnswerOption(
    text: String,
    isSelected: Boolean,
    isCorrect: Boolean,
    showResult: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val targetContainerColor =
        when {
            showResult && isCorrect -> MaterialTheme.colorScheme.primaryContainer
            showResult && isSelected && !isCorrect -> MaterialTheme.colorScheme.errorContainer
            !showResult && isSelected -> MaterialTheme.colorScheme.primaryContainer
            else -> MaterialTheme.colorScheme.surfaceContainerLow
        }

    val targetContentColor =
        when {
            showResult && isCorrect -> MaterialTheme.colorScheme.onPrimaryContainer
            showResult && isSelected && !isCorrect -> MaterialTheme.colorScheme.onErrorContainer
            !showResult && isSelected -> MaterialTheme.colorScheme.onPrimaryContainer
            else -> MaterialTheme.colorScheme.onSurface
        }

    val targetBorderColor =
        when {
            showResult && isCorrect -> Color.Transparent
            showResult && isSelected && !isCorrect -> MaterialTheme.colorScheme.error
            !showResult && isSelected -> MaterialTheme.colorScheme.primary
            else -> MaterialTheme.colorScheme.outlineVariant
        }

    val containerColor by animateColorAsState(
        targetValue = targetContainerColor,
        label = "answer_container_color",
    )
    val contentColor by animateColorAsState(
        targetValue = targetContentColor,
        label = "answer_content_color",
    )
    val borderColor by animateColorAsState(
        targetValue = targetBorderColor,
        label = "answer_border_color",
    )

    OutlinedCard(
        onClick = onClick,
        modifier = modifier,
        border = BorderStroke(1.dp, borderColor),
        colors =
            CardDefaults.outlinedCardColors(
                containerColor = containerColor,
                contentColor = contentColor,
            ),
    ) {
        AnswerOptionContent(
            text = text,
            showResult = showResult,
            isCorrect = isCorrect,
            isSelected = isSelected,
        )
    }
}

@Composable
private fun AnswerOptionContent(
    text: String,
    showResult: Boolean,
    isCorrect: Boolean,
    isSelected: Boolean,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f),
        )

        val showFeedback = showResult && (isCorrect || isSelected)
        AnimatedVisibility(
            visible = showFeedback,
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut(),
        ) {
            if (isCorrect) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Correct",
                    tint = MaterialTheme.colorScheme.primary,
                )
            } else if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Incorrect",
                    tint = MaterialTheme.colorScheme.error,
                )
            }
        }
    }
}
