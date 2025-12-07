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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Timeline
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

// ==================== Previews ====================

@androidx.compose.ui.tooling.preview.Preview(
    name = "Available Quiz - Light",
    showBackground = true,
)
@Composable
private fun QuizTilePreviewAvailableLight() {
    dev.hossain.devicecatalog.core.designsystem.theme.DeviceCatalogAppTheme(
        darkTheme = false,
        dynamicColor = false,
    ) {
        QuizTile(
            quizInfo =
                QuizHubScreen.QuizTypeInfo(
                    type = QuizHubScreen.QuizType.CODENAME_GUESS,
                    title = "Codename Guess",
                    description = "Match device codenames to models",
                    icon = Icons.Default.Code,
                    isAvailable = true,
                ),
            onQuizSelected = {},
            modifier = Modifier.size(180.dp),
        )
    }
}

@androidx.compose.ui.tooling.preview.Preview(
    name = "Available Quiz - Dark",
    showBackground = true,
)
@Composable
private fun QuizTilePreviewAvailableDark() {
    dev.hossain.devicecatalog.core.designsystem.theme.DeviceCatalogAppTheme(
        darkTheme = true,
        dynamicColor = false,
    ) {
        QuizTile(
            quizInfo =
                QuizHubScreen.QuizTypeInfo(
                    type = QuizHubScreen.QuizType.BRAND_CHALLENGE,
                    title = "Brand Challenge",
                    description = "Test your brand ownership knowledge",
                    icon = Icons.Default.Psychology,
                    isAvailable = true,
                ),
            onQuizSelected = {},
            modifier = Modifier.size(180.dp),
        )
    }
}

@androidx.compose.ui.tooling.preview.Preview(
    name = "Coming Soon Quiz - Light",
    showBackground = true,
)
@Composable
private fun QuizTilePreviewComingSoonLight() {
    dev.hossain.devicecatalog.core.designsystem.theme.DeviceCatalogAppTheme(
        darkTheme = false,
        dynamicColor = false,
    ) {
        QuizTile(
            quizInfo =
                QuizHubScreen.QuizTypeInfo(
                    type = QuizHubScreen.QuizType.SPEC_MASTER,
                    title = "Spec Master",
                    description = "Guess specs from images",
                    icon = Icons.Default.Memory,
                    isAvailable = false,
                ),
            onQuizSelected = {},
            modifier = Modifier.size(180.dp),
        )
    }
}

@androidx.compose.ui.tooling.preview.Preview(
    name = "Coming Soon Quiz - Dark",
    showBackground = true,
)
@Composable
private fun QuizTilePreviewComingSoonDark() {
    dev.hossain.devicecatalog.core.designsystem.theme.DeviceCatalogAppTheme(
        darkTheme = true,
        dynamicColor = false,
    ) {
        QuizTile(
            quizInfo =
                QuizHubScreen.QuizTypeInfo(
                    type = QuizHubScreen.QuizType.TIMELINE_CHALLENGE,
                    title = "Timeline Challenge",
                    description = "Order devices by release date",
                    icon = Icons.Default.Timeline,
                    isAvailable = false,
                ),
            onQuizSelected = {},
            modifier = Modifier.size(180.dp),
        )
    }
}
