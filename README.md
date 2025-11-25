# device-catalog-app
Repository for device catalog app that allows you to browse Android devices and see additional details.


## Development

### Compose Compiler Metrics and Reports

To analyze Compose performance and stability, you can enable Compose compiler metrics and reports:

```bash
# Generate both metrics and reports
./gradlew assembleRelease -PenableComposeCompilerMetrics=true -PenableComposeCompilerReports=true

# Or generate only metrics
./gradlew assembleRelease -PenableComposeCompilerMetrics=true

# Or generate only reports
./gradlew assembleRelease -PenableComposeCompilerReports=true
```

Output locations:
- **Metrics**: `app/build/compose-metrics/`
- **Reports**: `app/build/compose-reports/`

#### Generated Files

- `*-composables.txt` - List of all composables and their stability
- `*-composables.csv` - CSV format of composable data
- `*-classes.txt` - Stability of classes used in composables
- `*-module.json` - Module-level statistics

#### Interpreting Results

Look for:
- **Unstable parameters** - Can cause unnecessary recompositions
- **Non-skippable composables** - Will always recompose when parent recomposes
- **Classes marked as unstable** - Consider adding `@Immutable` or `@Stable` annotations

For more information, see:
- [Jetpack Compose Stability Explained](https://medium.com/androiddevelopers/jetpack-compose-stability-explained-79c10db270c8)
- [Compose Compiler Reports](https://developer.android.com/develop/ui/compose/performance/stability/diagnose#compose-compiler)


## Related
* 📋 Catalog CSV Parser: https://github.com/hossain-khan/android-device-catalog-parser
