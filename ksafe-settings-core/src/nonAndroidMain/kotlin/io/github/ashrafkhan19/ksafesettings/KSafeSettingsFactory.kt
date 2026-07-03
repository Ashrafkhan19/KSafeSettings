package io.github.ashrafkhan19.ksafesettings

import eu.anifantakis.lib.ksafe.KSafe
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

/**
 * Creates a [KSafeSettings] instance for **iOS, JVM/Desktop, and Web** targets.
 *
 * [KSafe] is initialised internally — no import or configuration of the underlying
 * library is required. Storage backends:
 * - **iOS** — Keychain
 * - **JVM/Desktop** — local file
 * - **WASM/JS** — localStorage
 *
 * An internal [CoroutineScope] is created automatically. If you need to control
 * the scope lifetime, use the escape-hatch factory in `commonMain` instead:
 * `KSafeSettings(KSafe(), yourScope)`.
 *
 * ### Compose — prefer [KSafeSettingsProvider] instead
 * ```kotlin
 * // In any Composable entry-point (iOS / Desktop / Web):
 * KSafeSettingsProvider { App() }
 * ```
 */
fun KSafeSettings(): KSafeSettings =
    KSafeSettingsImpl(KSafe())

/**
 * Creates a [KSafeSettings] instance for **iOS, JVM/Desktop, and Web** targets
 * with an explicit [CoroutineScope].
 *
 * Prefer the no-arg overload or [KSafeSettingsProvider] for typical use.
 */
fun KSafeSettings(scope: CoroutineScope): KSafeSettings =
    KSafeSettingsImpl(KSafe(), scope)
