# Changelog

All notable changes to KSafeSettings are documented here.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/).
This project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

---

## [0.1.0-alpha] — 2026-07-03

### Added

#### `ksafe-settings-core`
- `KSafeSettings` interface — unified API for persistent settings across Android, iOS, Desktop, and Web.
- `KSafeSettings(ksafe, scope)` factory function.
- Plain (unencrypted) property delegates: `bool`, `int`, `string`, `long`.
- Secure (AES-256-GCM encrypted) property delegates: `secureString`, `secureBool`, `secureInt`, `secureLong`.
- `biometricRequired` parameter on secure delegates — uses `HARDWARE_ISOLATED` key protection.
- `flow(key, default): StateFlow<T>` — reactive, scope-backed state flow.
- `remove(key)`, `clear()`, `contains(key)` utility functions.
- Internal key registry persisted in KSafe — `contains()` survives app restarts.
- `AndroidMigration.kt` — `migrate(from: SharedPreferences, keys)` Android extension.
- Full KDoc on all public APIs.
- Maven Central publish config via vanniktech `0.33.0`.

#### `ksafe-settings-compose`
- `composeState(key, default)` — KSafe-backed Compose `MutableState` for ViewModel class fields.
- `rememberComposeState(key, default)` — `@Composable`-body variant using `rememberKSafeState`.

#### `ksafe-settings-testing`
- `FakeKSafeSettings(scope)` — in-memory, no-encryption implementation of `KSafeSettings`.
- Reactive `StateFlow` backed by `MutableStateFlow` — test flows without real KSafe.
- 21 unit tests covering plain, secure, reactive, and utility operations.

### Dependencies
- KSafe `2.1.3` (eu.anifantakis:ksafe, ksafe-compose, ksafe-biometrics) — Apache 2.0
- Kotlin `2.4.0`
- kotlinx-coroutines `1.11.0`
- Compose Multiplatform `1.11.1`
- vanniktech maven-publish `0.33.0`

### Platforms
Android · iOS · macOS · Desktop (JVM) · Web (WASM) · Web (JS)

---

[0.1.0-alpha]: https://github.com/Ashrafkhan19/KSafeSettings/releases/tag/0.1.0-alpha
