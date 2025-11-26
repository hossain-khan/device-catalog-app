package dev.hossain.devicecatalog.feature.phonequiz.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Answer option card with visual feedback for selection and correctness.
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
    val containerColor =
        when {
            showResult && isCorrect -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            showResult && isSelected && !isCorrect -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
            !showResult && isSelected -> MaterialTheme.colorScheme.primaryContainer
            else -> MaterialTheme.colorScheme.surface
        }

    val contentColor =
        when {
            showResult && isCorrect -> MaterialTheme.colorScheme.onPrimaryContainer
            showResult && isSelected && !isCorrect -> MaterialTheme.colorScheme.onErrorContainer
            !showResult && isSelected -> MaterialTheme.colorScheme.onPrimaryContainer
            else -> MaterialTheme.colorScheme.onSurface
        }

    if (showResult && isCorrect) {
        // Use filled card for correct answer
        Card(
            onClick = onClick,
            modifier = modifier,
            colors =
                CardDefaults.cardColors(
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
    } else {
        // Use outlined card for other options
        OutlinedCard(
            onClick = onClick,
            modifier = modifier,
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

        if (showResult) {
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
