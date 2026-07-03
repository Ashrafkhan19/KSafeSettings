package io.github.ashrafkhan19.ksafesettings

import android.content.Context
import eu.anifantakis.lib.ksafe.KSafe
import kotlinx.coroutines.CoroutineScope

/**
 * Creates a [KSafeSettings] instance for **Android**.
 *
 * [KSafe] is initialised internally using [context] — no import or configuration
 * of the underlying library is required. The application context is extracted
 * automatically so it is safe to pass an [Activity] or [Application] instance.
 *
 * An internal `IO`-dispatcher [CoroutineScope] is created automatically. If you
 * need to control the scope lifetime, use the escape-hatch factory in `commonMain`:
 * `KSafeSettings(KSafe(context), yourScope)`.
 *
 * ### Compose — prefer [KSafeSettingsProvider] instead
 * ```kotlin
 * // In Activity.onCreate — no KSafe import needed:
 * setContent { KSafeSettingsProvider { App() } }
 * ```
 *
 * @param context Any Android [Context]; the application context is used internally.
 */
fun KSafeSettings(context: Context): KSafeSettings =
    KSafeSettingsImpl(KSafe(context.applicationContext))

/**
 * Creates a [KSafeSettings] instance for **Android** with an explicit [CoroutineScope].
 *
 * Prefer the single-arg overload or [KSafeSettingsProvider] for typical use.
 *
 * @param context Any Android [Context]; the application context is used internally.
 * @param scope   The [CoroutineScope] to back reactive [KSafeSettings.flow] StateFlows.
 */
fun KSafeSettings(context: Context, scope: CoroutineScope): KSafeSettings =
    KSafeSettingsImpl(KSafe(context.applicationContext), scope)
