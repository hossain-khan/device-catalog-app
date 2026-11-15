[![Android CI](https://github.com/hossain-khan/device-catalog-app/actions/workflows/android.yml/badge.svg)](https://github.com/hossain-khan/device-catalog-app/actions/workflows/android.yml) [![Post Merge Check](https://github.com/hossain-khan/device-catalog-app/actions/workflows/android-lint.yml/badge.svg)](https://github.com/hossain-khan/device-catalog-app/actions/workflows/android-lint.yml) [![Android Release Build](https://github.com/hossain-khan/device-catalog-app/actions/workflows/android-release.yml/badge.svg)](https://github.com/hossain-khan/device-catalog-app/actions/workflows/android-release.yml) [![Downloads for dev.hossain.devicecatalog](https://playbadges.pavi2410.com/badge/downloads?id=dev.hossain.devicecatalog&pretty)](https://play.google.com/store/apps/details?id=dev.hossain.devicecatalog&pcampaignid=web_share)

# Android Device Universe

<img src="project-resources/app-icon/catalog-icon-vibrant.svg" width="180" height="180" align="right" alt="Android Device Universe App Icon">

An offline Android app for browsing, searching, comparing, and exploring technical specifications from the official Google Play Android Device Catalog.

## Features

- **Device Catalog**: Paged browsing with live search and filtering by manufacturer, form factor, RAM, and SDK version.
- **Device Specifications**: Hardware specs (ABI, OpenGL ES, screen densities/sizes, SDK targets, RAM) with shareable device summaries and deep links.
- **Statistics & Explorer**: Ecosystem breakdowns across manufacturers, form factors, RAM tiers, and Android versions.
- **Comparison & Quizzes**: Side-by-side device comparison, brand challenge, phone quiz, and preference-based device matcher.

## Architecture & Tech Stack

- **Modularization**: Multi-module Gradle setup (`:app`, `:core:*`, `:feature:*`, and `:build-logic` convention plugins).
- **UI**: Jetpack Compose with Material 3 (`:core:designsystem`), adaptive navigation (`NavigationBar` / `NavigationRail`), and Coil 3.
- **Screen Architecture**: [Circuit](https://slackhq.github.io/circuit/) (`Presenter`/`Ui` UDF, `@CircuitSerializable` screens, `rememberSaveableNavStack`, `circuit-test`).
- **Dependency Injection**: [Metro](https://zacsweers.github.io/metro/) (`metrox-android`, `@BindingContainer` aggregation, and native Circuit compiler-plugin codegen).
- **Data**: Pre-populated Room database with one-to-many entity relations, Paging 3, DataStore Preferences, and WorkManager.

## Build & Checks

```bash
# Format & lint Kotlin
./gradlew formatKotlin
./gradlew lintKotlin

# Run unit tests, Android lint, and debug APK build
./gradlew testDebugUnitTest lintDebug assembleDebug
```

Requires JDK 21+ and Android SDK 37 (`minSdk` 28).

## Related Projects

- [Device Catalog Data Parser](https://github.com/hossain-khan/android-device-catalog-parser) — CSV parser used to process the catalog dataset
- [Device Catalog Website](https://github.com/hossain-khan/device-catalog-webapp) — Web companion built on the same catalog data

## License

Licensed under the MIT License. See [LICENSE](LICENSE) for details.
