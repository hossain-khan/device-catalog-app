package dev.hossain.devicecatalog.feature.quizhub.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.hossain.devicecatalog.feature.quizhub.QuizHubScreen

@Composable
fun QuizTile(
    quizInfo: QuizHubScreen.QuizTypeInfo,
    onQuizSelected: (QuizHubScreen.QuizType) -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        onClick = {
            if (quizInfo.isAvailable) {
                onQuizSelected(quizInfo.type)
            }
        },
        modifier =
            modifier
                .fillMaxWidth()
                .aspectRatio(1f),
        colors =
            CardDefaults.elevatedCardColors(
                containerColor =
                    if (quizInfo.isAvailable) {
                        MaterialTheme.colorScheme.primaryContainer
                    } else {
                        MaterialTheme.colorScheme.surfaceVariant
                    },
            ),
        elevation =
            CardDefaults.elevatedCardElevation(
                defaultElevation = 4.dp,
                pressedElevation = 8.dp,
            ),
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Icon(
                    imageVector = quizInfo.icon,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint =
                        if (quizInfo.isAvailable) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        },
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = quizInfo.title,
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center,
                    color =
                        if (quizInfo.isAvailable) {
                            MaterialTheme.colorScheme.onPrimaryContainer
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        },
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = quizInfo.description,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color =
                        if (quizInfo.isAvailable) {
                            MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        },
                )
            }

            // Coming Soon badge in top-right corner
            if (!quizInfo.isAvailable) {
                ComingSoonBadge(
                    modifier =
                        Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp),
                )
            }
        }
    }
}
