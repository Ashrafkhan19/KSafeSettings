# KSafeSettings 🔐

[![Maven Central](https://img.shields.io/maven-central/v/io.github.ashrafkhan19/ksafe-settings-core.svg?label=Maven%20Central)](https://central.sonatype.com/artifact/io.github.ashrafkhan19/ksafe-settings-core)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Kotlin](https://img.shields.io/badge/kotlin-2.4.0-blueviolet.svg)](https://kotlinlang.org)
[![KMP](https://img.shields.io/badge/KMP-Android%20%7C%20iOS%20%7C%20Desktop%20%7C%20Web-green.svg)](https://kotlinlang.org/docs/multiplatform.html)

**Zero-boilerplate, encrypted settings for Kotlin Multiplatform.**  
Typed property delegates. AES-256-GCM encryption. Zero-config Compose integration. No `expect/actual`. No `SharedPreferences`. Works identically on every platform.

> Built on [**KSafe**](https://github.com/ioannisa/KSafe) (Apache 2.0) by Android GDE **[@ioannisa](https://github.com/ioannisa)** —
> which provides AES-256-GCM encryption, hardware-backed key storage, biometrics, and device security policies.

---

## The Problem

KMP has no standard encrypted settings API. Your options today:

| Option | Multiplatform | Encrypted | Typed delegates | Compose | Test fake |
|---|:---:|:---:|:---:|:---:|:---:|
| `SharedPreferences` | Android only | ❌ | ❌ | ❌ | ❌ |
| `DataStore` | Android only | ❌ | ✅ | Partial | ❌ |
| `NSUserDefaults` | iOS only | ❌ | ❌ | ❌ | ❌ |
| `multiplatform-settings` | ✅ | ❌ | ✅ | ❌ | ✅ |
| **KSafeSettings** | ✅ | ✅ AES-256-GCM | ✅ | ✅ | ✅ |

---

## Platform Support

| Platform | Supported | Encryption backend |
|---|:---:|---|
| Android | ✅ | Android Keystore (hardware-backed) |
| iOS | ✅ | Secure Enclave / iOS Keychain |
| Desktop (JVM) | ✅ | OS secret store |
| Web (WASM) | ✅ | IndexedDB + WebCrypto |
| Web (JS) | ✅ | IndexedDB + WebCrypto |

---

## Quick Start

### 1. Add dependencies

```kotlin
// shared/build.gradle.kts or your KMP module
commonMain.dependencies {
    implementation("io.github.ashrafkhan19:ksafe-settings-core:0.1.0-alpha")
    implementation("io.github.ashrafkhan19:ksafe-settings-compose:0.1.0-alpha") // Compose only
}

// test source sets
commonTest.dependencies {
    implementation("io.github.ashrafkhan19:ksafe-settings-testing:0.1.0-alpha")
}
```

### 2. Wrap your root composable — one line, all platforms

```kotlin
// Android — Activity.onCreate
setContent { KSafeSettingsProvider { App() } }

// iOS — MainViewController.kt
fun MainViewController() = ComposeUIViewController { KSafeSettingsProvider { App() } }

// Desktop — main.kt
fun main() = application { Window(…) { KSafeSettingsProvider { App() } } }

// Web — main.kt
fun main() { ComposeViewport { KSafeSettingsProvider { App() } } }
```

No `KSafe` import. No `Context` plumbing. No `CoroutineScope` threading.  
`KSafeSettingsProvider` initialises everything internally per platform.

### 3. Access settings anywhere in the tree

```kotlin
@Composable
fun HomeScreen() {
    val settings = LocalKSafeSettings.current

    // Plain (unencrypted) — UI state, preferences
    var isDarkMode by settings.bool("dark_mode", default = false)
    var fontSize    by settings.int("font_size",  default = 16)
    var username    by settings.string("username", default = "")
    var lastSync    by settings.long("last_sync",  default = 0L)

    // Secure (AES-256-GCM encrypted, hardware-backed)
    var authToken  by settings.secureString("auth_token")
    var pin        by settings.secureString("pin", biometricRequired = true)

    // Reactive
    val theme: StateFlow<String> = settings.flow("theme", "system")
}
```

### 4. Compose state — survives process death

```kotlin
@Composable
fun TabBar() {
    val settings = LocalKSafeSettings.current
    // rememberComposeState = KSafe-backed MutableState, no ViewModel needed
    var selectedTab by settings.rememberComposeState("selected_tab", 0)
}
```

### 5. Testing with the in-memory fake

```kotlin
class SettingsViewModelTest {
    private val settings = FakeKSafeSettings()

    @Test
    fun `dark mode toggles`() {
        var isDarkMode by settings.bool("dark_mode", false)
        assertFalse(isDarkMode)
        isDarkMode = true
        assertTrue(isDarkMode)
    }

    @Test
    fun `flow emits on write`() = runTest {
        val themeFlow = settings.flow("theme", "system")
        assertEquals("system", themeFlow.value)

        var theme by settings.string("theme", "system")
        theme = "dark"
        assertEquals("dark", themeFlow.value)
    }
}
```

---

## Full API Reference

### Initialisation

```kotlin
// Compose (recommended) — zero config, handles platform differences internally
KSafeSettingsProvider { content() }
val settings = LocalKSafeSettings.current

// Android — manual (no Compose or custom scope needed)
val settings = KSafeSettings(context)
val settings = KSafeSettings(context, myScope)

// iOS / JVM / Web — manual
val settings = KSafeSettings()
val settings = KSafeSettings(myScope)

// Escape hatch — bring your own KSafe (custom encryption, biometrics config)
val settings = KSafeSettings(myKSafe)
val settings = KSafeSettings(myKSafe, myScope)
```

### Plain (unencrypted) delegates

| Function | Type | Default |
|---|---|---|
| `settings.bool(key, default)` | `Boolean` | `false` |
| `settings.int(key, default)` | `Int` | `0` |
| `settings.string(key, default)` | `String` | `""` |
| `settings.long(key, default)` | `Long` | `0L` |

```kotlin
var isDarkMode by settings.bool("dark_mode")         // default = false
var fontSize    by settings.int("font_size", 16)
var username    by settings.string("username")
```

### Secure (AES-256-GCM encrypted) delegates

| Function | Type | Biometric |
|---|---|---|
| `settings.secureString(key, default, biometricRequired)` | `String` | optional |
| `settings.secureBool(key, default, biometricRequired)` | `Boolean` | optional |
| `settings.secureInt(key, default, biometricRequired)` | `Int` | optional |
| `settings.secureLong(key, default, biometricRequired)` | `Long` | optional |

```kotlin
var authToken by settings.secureString("auth_token")
var pin       by settings.secureString("pin", biometricRequired = true)
```

`biometricRequired = true` uses `HARDWARE_ISOLATED` key protection. Pair with
`ksafe-biometrics` (`eu.anifantakis:ksafe-biometrics`) to prompt the user.

### Reactive StateFlow

```kotlin
val theme: StateFlow<String> = settings.flow("theme", "system")

// Pair with a plain delegate to write:
var themeValue by settings.string("theme", "system")
themeValue = "dark"   // → theme flow emits "dark"
```

### Compose extensions (`ksafe-settings-compose`)

```kotlin
// In a @Composable body — backed by KSafe, survives process death
var selectedTab by settings.rememberComposeState("tab", 0)

// In a ViewModel / class field — MutableState backed by KSafe
class AppViewModel(settings: KSafeSettings) : ViewModel() {
    var isDarkMode by settings.composeState("dark_mode", false)
}
```

### Utilities

```kotlin
settings.contains("key")               // true if ever written
settings.remove("key")                 // erases data + encryption key
settings.clear()                       // removes everything
```

### Android migration from SharedPreferences

```kotlin
// androidMain only
settings.migrate(
    from = context.getSharedPreferences("legacy_prefs", MODE_PRIVATE),
    keys = listOf("user_id", "theme", "font_size"),
)
```

---

## Modules

| Artifact | What it gives you | Add to |
|---|---|---|
| `ksafe-settings-core` | `KSafeSettings` interface, all delegates, `KSafeSettingsProvider` | `commonMain` |
| `ksafe-settings-compose` | `composeState`, `rememberComposeState`, `LocalKSafeSettings` | `commonMain` |
| `ksafe-settings-testing` | `FakeKSafeSettings` in-memory fake, no encryption overhead | `commonTest` |

---

## Design decisions

**Why not `expect/actual` for settings?**  
You'd end up writing platform code in every project. KSafeSettings does it once via
the `KSafeSettingsProvider` expect/actual internally, so you never have to.

**Why does `contains()` work across restarts?**  
KSafe has no `contains` API. KSafeSettings maintains a key registry serialised into
KSafe itself under a reserved key, so membership survives process death.

**Can I use it without Compose?**  
Yes — the `ksafe-settings-core` module has no Compose dependency. Create a settings
instance with `KSafeSettings(context)` or `KSafeSettings()` and use delegates directly.

**Can I customise the KSafe instance?**  
Use the escape-hatch factory: `KSafeSettings(myKSafe, myScope)`.
Access the underlying instance via `settings.ksafe`.

---

## Credits

KSafeSettings is built on top of [**KSafe**](https://github.com/ioannisa/KSafe) (Apache 2.0),
created by Android GDE **[Ioannis Anifantakis (@ioannisa)](https://github.com/ioannisa)**.
KSafe provides the AES-256-GCM encryption engine, hardware-backed key storage, biometric
authentication, and device security checks that power KSafeSettings under the hood.

---

## License

```
Copyright 2026 Ashraf Khan

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    https://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
