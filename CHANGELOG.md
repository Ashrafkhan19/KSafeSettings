# Changelog

All notable changes to KSafeSettings are documented here.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/).
This project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

---

## [Unreleased]

---

## [0.1.0-alpha] — 2026-07-03

Initial alpha release.

### Added

#### `ksafe-settings-core`
- `KSafeSettings` interface — unified typed settings API across all KMP targets.
- **Platform factories** — no KSafe import required:
  - Android: `KSafeSettings(context)` / `KSafeSettings(context, scope)`
  - iOS / JVM / Web: `KSafeSettings()` / `KSafeSettings(scope)`
  - Escape hatch: `KSafeSettings(ksafe)` / `KSafeSettings(ksafe, scope)`
- Internal `CoroutineScope(Dispatchers.IO + SupervisorJob())` — no scope parameter required for typical usage.
- Plain (unencrypted) property delegates: `bool`, `int`, `string`, `long`.
- Secure (AES-256-GCM encrypted, hardware-backed) delegates: `secureString`, `secureBool`, `secureInt`, `secureLong`.
- `biometricRequired` parameter on secure delegates — `HARDWARE_ISOLATED` key protection.
- `flow(key, default): StateFlow<T>` — reactive, scope-backed StateFlow updated on every write.
- `contains(key)` — key registry persisted in KSafe, survives app restarts.
- `remove(key)`, `clear()` — full key and data erasure.
- `migrate(from: SharedPreferences, keys)` — Android-only `androidMain` extension.
- Full KDoc on all public APIs.
- Maven Central publish config (vanniktech `0.33.0`).

#### `ksafe-settings-compose`
- `LocalKSafeSettings` — `CompositionLocal<KSafeSettings>` for zero-parameter access.
- `KSafeSettingsProvider { }` — `expect/actual` composable; initialises KSafe internally per platform:
  - Android actual: uses `LocalContext.current` — no Activity reference needed.
  - Non-Android actual (iOS / JVM / Web): calls `KSafe()` directly.
- `composeState(key, default)` — KSafe-backed Compose `MutableState` for ViewModel fields.
- `rememberComposeState(key, default)` — `@Composable`-body variant, survives process death.

#### `ksafe-settings-testing`
- `FakeKSafeSettings()` — in-memory, zero-encryption `KSafeSettings` for unit tests.
- Reactive `StateFlow` backed by `MutableStateFlow` — full reactive testing without real KSafe.
- 21 unit tests covering all API surface areas.

### Dependencies
- KSafe `2.1.3` (`eu.anifantakis:ksafe`, `ksafe-compose`, `ksafe-biometrics`) — Apache 2.0
- Kotlin `2.4.0`
- kotlinx-coroutines `1.11.0`
- Compose Multiplatform `1.11.1`
- vanniktech maven-publish `0.33.0`

### Platforms
Android · iOS · Desktop (JVM) · Web (WASM) · Web (JS)

---

[Unreleased]: https://github.com/Ashrafkhan19/KSafeSettings/compare/0.1.0-alpha...HEAD
[0.1.0-alpha]: https://github.com/Ashrafkhan19/KSafeSettings/releases/tag/0.1.0-alpha
