# Design System Module

The `:core:designsystem` module provides a centralized design system for the Device Catalog app, following Material 3 Design guidelines.

## Overview

This module contains:
- **Theme**: Colors, typography, and theme configuration
- **Icons**: Centralized icon management
- **Components**: Reusable UI components with consistent styling
- **Previews**: Preview annotations for all components

## Structure

```
:core:designsystem/
├── src/main/kotlin/dev/hossain/devicecatalog/core/designsystem/
│   ├── component/       # Reusable UI components
│   ├── icon/            # Icon management
│   └── theme/           # Theme configuration
└── src/test/kotlin/     # Unit tests
```

## Theme

### Colors
Material 3 color scheme with blue as the primary color:

```kotlin
// Dark theme colors (80 variants - lighter)
val Blue80 = Color(0xFFB3C5F7)
val BlueGrey80 = Color(0xFFBFC6DC)
val Teal80 = Color(0xFFB0F2F2)

// Light theme colors (40 variants - darker)
val Blue40 = Color(0xFF4285F4)  // Google Blue
val BlueGrey40 = Color(0xFF5F6368)
val Teal40 = Color(0xFF26A69A)
```

### Typography
Uses Lato font family from Google Fonts for both display and body text.

### Theme Usage

```kotlin
@Composable
fun MyScreen() {
    DeviceCatalogAppTheme {
        // Your content here
    }
}
```

Options:
- `darkTheme`: Boolean - Use dark theme (defaults to system setting)
- `dynamicColor`: Boolean - Use Android 12+ dynamic colors (defaults to true)

## Icons

The `DeviceCatalogIcons` object provides centralized access to all icons used in the app.

### Usage

```kotlin
import dev.hossain.devicecatalog.core.designsystem.icon.DeviceCatalogIcons

Icon(
    imageVector = DeviceCatalogIcons.Search,
    contentDescription = "Search"
)
```

### Available Icons

**Navigation**
- `ArrowBack`, `List`

**Actions**
- `Clear`, `Close`, `ContentCopy`, `FilterList`, `Search`, `Share`

**Status**
- `BugReport`, `Info`, `Warning`, `Star`

**UI**
- `KeyboardArrowDown`, `KeyboardArrowUp`

**Features**
- `Person`, `Settings`

## Components

### Buttons

#### Primary Button
```kotlin
DeviceCatalogButton(onClick = { /* action */ }) {
    Text("Primary Action")
}
```

#### Secondary Button
```kotlin
DeviceCatalogSecondaryButton(onClick = { /* action */ }) {
    Text("Secondary Action")
}
```

#### Outlined Button
```kotlin
DeviceCatalogOutlinedButton(onClick = { /* action */ }) {
    Text("Tertiary Action")
}
```

#### Text Button
```kotlin
DeviceCatalogTextButton(onClick = { /* action */ }) {
    Text("Low Emphasis")
}
```

### Cards

#### Standard Card
```kotlin
DeviceCatalogCard(
    modifier = Modifier.padding(16.dp),
    onClick = { /* optional click handler */ }
) {
    Text("Card Title", style = MaterialTheme.typography.titleMedium)
    Text("Card content", style = MaterialTheme.typography.bodyMedium)
}
```

#### Elevated Card
```kotlin
DeviceCatalogElevatedCard(
    modifier = Modifier.padding(16.dp)
) {
    // Content with higher elevation
}
```

### Top App Bar

```kotlin
DeviceCatalogTopAppBar(
    title = "Screen Title",
    navigationIcon = DeviceCatalogIcons.ArrowBack,
    navigationIconContentDescription = "Navigate back",
    onNavigationClick = { /* handle back */ },
    actions = {
        IconButton(onClick = { /* action */ }) {
            Icon(DeviceCatalogIcons.Search, "Search")
        }
    }
)
```

### Loading Indicators

#### Standard Loading Wheel
```kotlin
DeviceCatalogLoadingWheel()
```

#### Small Loading Wheel
```kotlin
DeviceCatalogLoadingWheelSmall()
```

#### Animated Loading Wheel
```kotlin
DeviceCatalogLoadingWheelAnimated()
```

#### Loading Box
```kotlin
DeviceCatalogLoadingBox(
    modifier = Modifier.fillMaxSize()
)
```

### Background

#### Standard Background
```kotlin
DeviceCatalogBackground {
    // Your content
}
```

#### Gradient Background
```kotlin
DeviceCatalogGradientBackground {
    // Your content with gradient
}
```

## Design Guidelines

### Material 3 Compliance

All components follow Material 3 design guidelines:

1. **Use Theme Colors**: Always use `MaterialTheme.colorScheme.*`
2. **Never Hardcode Colors**: Avoid `Color(0xFF...)` in favor of theme colors
3. **Dynamic Color Support**: Components work with Android 12+ dynamic colors
4. **Edge-to-Edge**: Respect system bar insets with proper padding
5. **Typography**: Use `MaterialTheme.typography.*` for all text

### Accessibility

- Proper content descriptions for icons
- Minimum touch target sizes (48dp)
- Sufficient color contrast
- Semantic roles for interactive elements

### Preview Annotations

All components include preview annotations for easy design verification:

```kotlin
@Preview
@Composable
private fun ComponentPreview() {
    DeviceCatalogAppTheme {
        YourComponent()
    }
}
```

## Testing

The module includes unit tests for theme colors and Material 3 compliance.

Run tests:
```bash
./gradlew :core:designsystem:test
```

## Adding New Components

When adding new components:

1. Create the component in `component/` package
2. Follow Material 3 guidelines
3. Use theme colors and typography
4. Add preview annotations
5. Include KDoc documentation
6. Add unit tests if applicable
7. Update this README

## References

- [Material 3 Design System](https://m3.material.io/)
- [Material 3 Compose Components](https://developer.android.com/jetpack/compose/designsystems/material3)
- [Now in Android Design System](https://github.com/android/nowinandroid/tree/main/core/designsystem)
- [Android Dynamic Colors](https://developer.android.com/develop/ui/views/theming/dynamic-colors)
