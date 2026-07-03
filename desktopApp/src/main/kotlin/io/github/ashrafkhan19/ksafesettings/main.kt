package io.github.ashrafkhan19.ksafesettings

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import io.github.ashrafkhan19.ksafesettings.compose.KSafeSettingsProvider

/**
 * Desktop (JVM) entry point. [KSafeSettingsProvider] initialises KSafe internally
 * (file-backed) — no context or scope wiring needed.
 */
fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "KSafeSettings",
    ) {
        KSafeSettingsProvider { App() }
    }
}
