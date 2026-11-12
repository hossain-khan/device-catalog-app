# Agent Guidelines

All AI coding agents (Copilot, Antigravity, Claude, Cursor, Codex, etc.) working on this repository should refer to the comprehensive project instructions, architecture conventions, and guidelines documented in:

👉 **[.github/copilot-instructions.md](.github/copilot-instructions.md)**

---

### Quick Reference

- **Architecture**: Multi-module Android architecture (Now in Android pattern) with `core:*`, `feature:*`, and `build-logic` convention plugins.
- **UI & Architecture**: Jetpack Compose + Slack's [Circuit](https://slackhq.github.io/circuit/) (Presenter/UI pattern).
- **Dependency Injection**: [Metro](https://github.com/ZacSweers/metro) (compile-time DI).
- **Database**: Room database with one-to-many relationship entities.
- **Verification Commands**:
  - Format / Lint check: `./gradlew lintKotlin`
  - Unit tests: `./gradlew testDebugUnitTest`
  - Android lint: `./gradlew lintDebug`
  - Build: `./gradlew assembleDebug`
