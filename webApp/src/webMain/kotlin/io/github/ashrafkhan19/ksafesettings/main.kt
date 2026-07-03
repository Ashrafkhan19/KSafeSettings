package io.github.ashrafkhan19.ksafesettings

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import io.github.ashrafkhan19.ksafesettings.compose.KSafeSettingsProvider

/**
 * Web (WASM/JS) entry point. [KSafeSettingsProvider] initialises KSafe internally
 * (localStorage-backed) — no context or scope wiring needed.
 */
@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    ComposeViewport {
        KSafeSettingsProvider { App() }
    }
}
