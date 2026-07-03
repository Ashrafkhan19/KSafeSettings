package io.github.ashrafkhan19.ksafesettings

import androidx.compose.ui.window.ComposeUIViewController
import io.github.ashrafkhan19.ksafesettings.compose.KSafeSettingsProvider

/**
 * iOS entry point. [KSafeSettingsProvider] initialises KSafe internally
 * (Keychain-backed) — no context or scope wiring needed.
 */
fun MainViewController() = ComposeUIViewController {
    KSafeSettingsProvider { App() }
}
