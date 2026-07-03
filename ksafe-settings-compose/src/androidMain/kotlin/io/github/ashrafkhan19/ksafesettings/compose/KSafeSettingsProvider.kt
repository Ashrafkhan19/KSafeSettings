package io.github.ashrafkhan19.ksafesettings.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import io.github.ashrafkhan19.ksafesettings.KSafeSettings

/**
 * Android actual for [KSafeSettingsProvider].
 *
 * Retrieves the Android [Context] from [LocalContext] and creates a [KSafeSettings]
 * instance backed by [eu.anifantakis.lib.ksafe.KSafe]. The instance is `remember`-ed
 * so it is created once per composition and shared across all recompositions.
 */
@Composable
actual fun KSafeSettingsProvider(content: @Composable () -> Unit) {
    val context = LocalContext.current
    val settings = remember(context) { KSafeSettings(context) }
    CompositionLocalProvider(LocalKSafeSettings provides settings) {
        content()
    }
}
