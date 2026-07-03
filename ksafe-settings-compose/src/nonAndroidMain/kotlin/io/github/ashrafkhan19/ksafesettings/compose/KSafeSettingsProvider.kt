package io.github.ashrafkhan19.ksafesettings.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import io.github.ashrafkhan19.ksafesettings.KSafeSettings

/**
 * Non-Android actual for [KSafeSettingsProvider] (iOS, JVM/Desktop, WASM/JS).
 *
 * Calls [KSafeSettings]`()` — the no-arg factory that initialises [eu.anifantakis.lib.ksafe.KSafe]
 * internally using the platform's native storage backend (Keychain on iOS, file on JVM,
 * localStorage on WASM/JS). The instance is `remember`-ed so it is created once per
 * composition.
 */
@Composable
actual fun KSafeSettingsProvider(content: @Composable () -> Unit) {
    val settings = remember { KSafeSettings() }
    CompositionLocalProvider(LocalKSafeSettings provides settings) {
        content()
    }
}
