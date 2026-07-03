package io.github.ashrafkhan19.ksafesettings.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import io.github.ashrafkhan19.ksafesettings.KSafeSettings

/**
 * Composition local that provides the app-wide [KSafeSettings] instance
 * anywhere in the Compose tree.
 *
 * Set via [KSafeSettingsProvider]. Access via `LocalKSafeSettings.current`.
 *
 * ```kotlin
 * val settings = LocalKSafeSettings.current
 * var isDarkMode by settings.bool("dark_mode", false)
 * ```
 */
val LocalKSafeSettings = compositionLocalOf<KSafeSettings> {
    error(
        "No KSafeSettings provided. " +
            "Wrap your root composable with KSafeSettingsProvider { … }."
    )
}

/**
 * Provides a [KSafeSettings] instance to all composables in [content] via
 * [LocalKSafeSettings].
 *
 * **Platform behaviour (no setup required):**
 * - **Android** — uses `LocalContext.current` to initialise KSafe internally.
 * - **iOS / JVM / Web** — calls `KSafe()` directly (no context needed).
 *
 * Place this at the root of your composition (inside `setContent`, `Window`,
 * `ComposeUIViewController`, or `ComposeViewport`):
 *
 * ```kotlin
 * // Android — Activity.onCreate
 * setContent { KSafeSettingsProvider { App() } }
 *
 * // iOS
 * fun MainViewController() = ComposeUIViewController { KSafeSettingsProvider { App() } }
 *
 * // Desktop
 * Window(…) { KSafeSettingsProvider { App() } }
 *
 * // Web
 * ComposeViewport { KSafeSettingsProvider { App() } }
 * ```
 */
@Composable
expect fun KSafeSettingsProvider(content: @Composable () -> Unit)
