# KSafeSettings 🔐

[![Maven Central](https://img.shields.io/maven-central/v/io.github.ashrafkhan19/ksafe-settings-core.svg?label=Maven%20Central)](https://central.sonatype.com/artifact/io.github.ashrafkhan19/ksafe-settings-core)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Kotlin](https://img.shields.io/badge/kotlin-2.4.0-blueviolet.svg)](https://kotlinlang.org/)
[![KMP](https://img.shields.io/badge/Kotlin%20Multiplatform-Android%20%7C%20iOS%20%7C%20Desktop%20%7C%20Web-green.svg)](https://kotlinlang.org/docs/multiplatform.html)

**The zero-boilerplate, encrypted settings SDK for Kotlin Multiplatform.**  
No `SharedPreferences`. No `DataStore` wiring. No platform `expect/actual` for preferences.  
Just clean, typed property delegates that persist and encrypt across every KMP target.

> Built on top of [**KSafe**](https://github.com/ioannisa/KSafe) (Apache 2.0) by **Android GDE [@ioannisa](https://github.com/ioannisa)**,
> which provides AES-256-GCM encryption, hardware-backed key storage, biometrics, and device security policies.

---

## The Problem

KMP has no standard encrypted settings API. Today you're forced to choose:

| Option | Multiplatform | Encrypted | Simple API | Type-safe |
|---|:---:|:---:|:---:|:---:|
| `SharedPreferences` | Android only | ❌ | ✅ | ❌ |
| `DataStore` | Android only | ❌ | ❌ | ✅ |
| `NSUserDefaults` | iOS only | ❌ | ✅ | ❌ |
| `multiplatform-settings` | ✅ | ❌ | ✅ | Partial |
| **KSafeSettings** | ✅ | ✅ AES-256-GCM | ✅ | ✅ |

---

## Platform Support

| Platform | Supported | Encryption backend |
|---|:---:|---|
| Android | ✅ | Android Keystore (hardware-backed) |
| iOS | ✅ | Secure Enclave / iOS Keychain |
| macOS | ✅ | macOS Keychain |
| Desktop (JVM) | ✅ | OS secret store (macOS Keychain / Linux Secret Service) |
| Web (WASM) | ✅ | IndexedDB + WebCrypto |
| Web (JS) | ✅ | IndexedDB + WebCrypto |

---

## Quick Start

### 1. Add dependencies

```kotlin
// build.gradle.kts
commonMain.dependencies {
    implementation("io.github.ashrafkhan19:ksafe-settings-core:0.1.0-alpha")
    implementation("io.github.ashrafkhan19:ksafe-settings-compose:0.1.0-alpha") // optional
}
testImplementation("io.github.ashrafkhan19:ksafe-settings-testing:0.1.0-alpha") // optional
```

### 2. Create a `KSafeSettings` instance

```kotlin
// Android (e.g. Application class or Koin module)
val ksafe = KSafe(context)
val settings = KSafeSettings(ksafe, applicationScope)

// iOS / JVM / Web
val ksafe = KSafe()
val settings = KSafeSettings(ksafe, applicationScope)
```

### 3. Use it

```kotlin
// Plain (unencrypted) — theme, font size, UI state
var isDarkMode by settings.bool("dark_mode", default = false)
var fontSize    by settings.int("font_size",  default = 16)
var username    by settings.string("username", default = "")
var lastSync    by settings.long("last_sync",  default = 0L)

// Secure (AES-256-GCM encrypted, hardware-backed)
var authToken   by settings.secureString("auth_token")
var accountPin  by settings.secureString("pin", biometricRequired = true)

// Reactive (StateFlow)
val theme: StateFlow<String> = settings.flow("theme", "system")

// Utilities
settings.remove("key")
settings.clear()
settings.contains("key")

// Migration from SharedPreferences (Android only)
settings.migrate(from = oldSharedPreferences, keys = listOf("user_id", "token"))
```

### 4. Compose integration

```kotlin
// ViewModel (persisted MutableState, encrypted)
class SettingsViewModel(settings: KSafeSettings) : ViewModel() {
    var isDarkMode by settings.composeState("dark_mode", false)
}

// Composable body (persisted, survives process death)
@Composable
fun HomeScreen(settings: KSafeSettings) {
    var currentTab by settings.rememberComposeState("tab", 0)
}
```

### 5. Testing

```kotlin
class MyViewModelTest {
    val settings: KSafeSettings = FakeKSafeSettings(TestScope())

    @Test
    fun `token is persisted`() {
        var token by settings.secureString("auth_token")
        token = "abc123"
        assertEquals("abc123", token)
    }
}
```

---

## Full API Reference

### `KSafeSettings` factory

```kotlin
fun KSafeSettings(ksafe: KSafe, scope: CoroutineScope): KSafeSettings
```

### Plain (unencrypted) delegates

| Function | Type |
|---|---|
| `settings.bool(key, default)` | `Boolean` |
| `settings.int(key, default)` | `Int` |
| `settings.string(key, default)` | `String` |
| `settings.long(key, default)` | `Long` |

### Secure (AES-256-GCM encrypted) delegates

| Function | Type | Notes |
|---|---|---|
| `settings.secureString(key, default, biometricRequired)` | `String` | |
| `settings.secureBool(key, default, biometricRequired)` | `Boolean` | |
| `settings.secureInt(key, default, biometricRequired)` | `Int` | |
| `settings.secureLong(key, default, biometricRequired)` | `Long` | |

`biometricRequired = true` stores the key with `HARDWARE_ISOLATED` protection and `requireUnlockedDevice = true`.
Use `ksafe-biometrics` (`eu.anifantakis:ksafe-biometrics`) to prompt biometric verification before access.

### Reactive

```kotlin
fun <T> flow(key: String, default: T): StateFlow<T>
```

### Compose (requires `ksafe-settings-compose`)

```kotlin
// ViewModel / class field
inline fun <reified T> KSafeSettings.composeState(key: String, default: T)

// @Composable function body
@Composable
inline fun <reified T> KSafeSettings.rememberComposeState(key: String, default: T)
```

### Utilities

```kotlin
fun remove(key: String)
fun clear()
fun contains(key: String): Boolean
// Android only:
fun KSafeSettings.migrate(from: SharedPreferences, keys: List<String>)
```

### Advanced: escape hatch

```kotlin
val ksafe: KSafe = settings.ksafe
// Use any KSafe API directly (mutableStateOf, asWritableFlow, put/get suspend, etc.)
```

---

## Modules

| Artifact | Description | Add to |
|---|---|---|
| `ksafe-settings-core` | Core interface + KSafe-backed implementation | `commonMain` |
| `ksafe-settings-compose` | `composeState` / `rememberComposeState` extensions | `commonMain` |
| `ksafe-settings-testing` | `FakeKSafeSettings` in-memory fake | `testImplementation` |

---

## Credits

KSafeSettings is built on top of [**KSafe**](https://github.com/ioannisa/KSafe) (Apache 2.0),
created by Android GDE **[Ioannis Anifantakis (@ioannisa)](https://github.com/ioannisa)**.
KSafe provides the AES-256-GCM encryption engine, hardware-backed key storage, biometric
authentication, and root/jailbreak detection. KSafeSettings adds the typed settings layer on top.

---

## License

```
Copyright 2026 Ashraf Khan

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    https://www.apache.org/licenses/LICENSE-2.0
```
