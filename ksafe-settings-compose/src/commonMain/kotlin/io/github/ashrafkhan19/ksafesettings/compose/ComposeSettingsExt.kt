package io.github.ashrafkhan19.ksafesettings.compose

import androidx.compose.runtime.Composable
import eu.anifantakis.lib.ksafe.compose.mutableStateOf
import eu.anifantakis.lib.ksafe.compose.rememberKSafeState
import io.github.ashrafkhan19.ksafesettings.KSafeSettings

/**
 * Returns a persisted Compose state delegate for use in **ViewModel** class
 * fields or any class that is constructed once and lives as long as its owner.
 * The state is encrypted by default via KSafe.
 *
 * The delegate reads from KSafe storage on initialization and persists every
 * write automatically. Use [rememberComposeState] inside a `@Composable` body
 * instead — it is `remember`-scoped and recomposition-safe.
 *
 * ```kotlin
 * class SettingsViewModel(settings: KSafeSettings) : ViewModel() {
 *     var isDarkMode by settings.composeState("dark_mode", false)
 *     var fontSize   by settings.composeState("font_size", 16)
 * }
 * ```
 *
 * @param key     Explicit storage key (takes precedence over the property name).
 * @param default Initial value when no stored value exists for [key].
 */
inline fun <reified T> KSafeSettings.composeState(key: String, default: T) =
    ksafe.mutableStateOf(default, key = key)

/**
 * Returns a persisted Compose state delegate for use **directly inside a
 * `@Composable` function body**. The state survives recomposition and app
 * restarts. Use [composeState] in ViewModel / class fields instead.
 *
 * ```kotlin
 * @Composable
 * fun HomeScreen(settings: KSafeSettings) {
 *     var currentTab by settings.rememberComposeState("selected_tab", 0)
 *     // currentTab survives process death, no ViewModel needed
 * }
 * ```
 *
 * @param key     Explicit storage key (takes precedence over the property name).
 * @param default Initial value when no stored value exists for [key].
 */
@Composable
inline fun <reified T> KSafeSettings.rememberComposeState(key: String, default: T) =
    ksafe.rememberKSafeState(default, key = key)
