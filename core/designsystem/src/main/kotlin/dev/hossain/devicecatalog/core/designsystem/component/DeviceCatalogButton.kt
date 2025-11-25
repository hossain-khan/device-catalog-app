package dev.hossain.devicecatalog.core.designsystem.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.hossain.devicecatalog.core.designsystem.theme.DeviceCatalogAppTheme

/**
 * Device Catalog primary button component.
 * Uses Material 3 filled button style for primary actions.
 */
@Composable
fun DeviceCatalogButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable () -> Unit,
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        colors =
            ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
            ),
    ) {
        content()
    }
}

/**
 * Device Catalog secondary button component.
 * Uses Material 3 filled tonal button style for secondary actions.
 */
@Composable
fun DeviceCatalogSecondaryButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable () -> Unit,
) {
    FilledTonalButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        colors =
            ButtonDefaults.filledTonalButtonColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
            ),
    ) {
        content()
    }
}

/**
 * Device Catalog outlined button component.
 * Uses Material 3 outlined button style for tertiary actions.
 */
@Composable
fun DeviceCatalogOutlinedButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable () -> Unit,
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        border =
            BorderStroke(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline,
            ),
        colors =
            ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.primary,
            ),
    ) {
        content()
    }
}

/**
 * Device Catalog text button component.
 * Uses Material 3 text button style for low-emphasis actions.
 */
@Composable
fun DeviceCatalogTextButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable () -> Unit,
) {
    TextButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        colors =
            ButtonDefaults.textButtonColors(
                contentColor = MaterialTheme.colorScheme.primary,
            ),
    ) {
        content()
    }
}

// Previews
@Preview
@Composable
private fun DeviceCatalogButtonPreview() {
    DeviceCatalogAppTheme {
        DeviceCatalogButton(onClick = { }) {
            Text("Primary Button")
        }
    }
}

@Preview
@Composable
private fun DeviceCatalogSecondaryButtonPreview() {
    DeviceCatalogAppTheme {
        DeviceCatalogSecondaryButton(onClick = { }) {
            Text("Secondary Button")
        }
    }
}

@Preview
@Composable
private fun DeviceCatalogOutlinedButtonPreview() {
    DeviceCatalogAppTheme {
        DeviceCatalogOutlinedButton(onClick = { }) {
            Text("Outlined Button")
        }
    }
}

@Preview
@Composable
private fun DeviceCatalogTextButtonPreview() {
    DeviceCatalogAppTheme {
        DeviceCatalogTextButton(onClick = { }) {
            Text("Text Button")
        }
    }
}
