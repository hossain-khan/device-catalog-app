# Benchmarks & Baseline Profiles (`:benchmarks`)

This module contains **Macrobenchmarks** and **Baseline Profile generators** for Android Device Universe (`:app`).

## What's Included

- **Baseline Profile Generators** (`baselineprofile/`):
  - `StartupBaselineProfile`: Generates a Startup Profile (`includeInStartupProfile = true`) for cold app launch and initial device catalog rendering, enabling Dex Layout Optimizations in release builds.
  - `DeviceCatalogBaselineProfile`: Profiles critical user journeys including device catalog `LazyColumn` scrolling and top-level tab navigation (`Quiz`, `Stats`, `About`, `Devices`).
- **Macrobenchmarks**:
  - `StartupBenchmark` (`startup/`): Measures cold startup latency (`StartupTimingMetric`, JIT compilation, and class initialization) across `CompilationMode.None`, `CompilationMode.Partial` (Baseline Profile), and `CompilationMode.Full`.
  - `ScrollDevicesListBenchmark` (`devices/`): Measures `FrameTimingMetric` while flinging the paginated device catalog list.

## Generating Baseline Profiles

With a connected Android device/emulator (API 28+, or using the configured `pixel6Api33` Gradle Managed Device):

```bash
./gradlew :app:generateReleaseBaselineProfile
```

The generated profiles are automatically copied into `app/src/release/generated/baselineProfiles/` and packaged into release APKs/AABs via `androidx.profileinstaller`.

## Running Macrobenchmarks

On a physical Android device (recommended for accurate timing measurements):

```bash
./gradlew :benchmarks:connectedBenchmarkReleaseAndroidTest
```

Or using the Gradle Managed Device:

```bash
./gradlew :benchmarks:pixel6Api33BenchmarkReleaseAndroidTest
```
