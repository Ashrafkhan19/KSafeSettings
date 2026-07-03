package io.github.ashrafkhan19.ksafesettings

import eu.anifantakis.lib.ksafe.KSafe
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import kotlin.properties.ReadWriteProperty

/**
 * The main entry point for KSafeSettings.
 *
 * Provides a unified, developer-friendly API for persisting settings across
 * all KMP targets (Android, iOS, Desktop, Web). Plain settings are stored
 * unencrypted; secure settings are encrypted with AES-256-GCM backed by
 * the platform's hardware keystore where available.
 *
 * ### Usage
 * ```kotlin
 * val ksafe = KSafe(context) // Android
 * val ksafe = KSafe()        // iOS / JVM / Web
 * val settings = KSafeSettings(ksafe, viewModelScope)
 *
 * var isDarkMode by settings.bool("dark_mode", default = false)
 * var authToken  by settings.secureString("auth_token")
 * val theme: StateFlow<String> = settings.flow("theme", "system")
 * ```
 *
 * @see KSafeSettingsImpl for the concrete implementation.
 */
interface KSafeSettings {

    /**
     * The underlying [KSafe] instance. Exposed as an escape hatch for advanced
     * use-cases not covered by the KSafeSettings API (e.g. Compose state via
     * [eu.anifantakis.lib.ksafe.KSafe.mutableStateOf]).
     */
    val ksafe: KSafe

    // ── Plain (unencrypted) delegates ──────────────────────────────────────

    /**
     * Returns a [ReadWriteProperty] delegate that stores a [Boolean] value
     * unencrypted under the given [key].
     *
     * @param key     Unique storage key.
     * @param default Value returned when no stored value exists for [key].
     */
    fun bool(key: String, default: Boolean = false): ReadWriteProperty<Any?, Boolean>

    /**
     * Returns a [ReadWriteProperty] delegate that stores an [Int] value
     * unencrypted under the given [key].
     */
    fun int(key: String, default: Int = 0): ReadWriteProperty<Any?, Int>

    /**
     * Returns a [ReadWriteProperty] delegate that stores a [String] value
     * unencrypted under the given [key].
     */
    fun string(key: String, default: String = ""): ReadWriteProperty<Any?, String>

    /**
     * Returns a [ReadWriteProperty] delegate that stores a [Long] value
     * unencrypted under the given [key].
     */
    fun long(key: String, default: Long = 0L): ReadWriteProperty<Any?, Long>

    // ── Secure (AES-256-GCM encrypted) delegates ──────────────────────────

    /**
     * Returns a [ReadWriteProperty] delegate that stores a [String] value
     * encrypted with AES-256-GCM under the given [key].
     *
     * @param key               Unique storage key.
     * @param default           Value returned when no stored value exists.
     * @param biometricRequired When `true`, the key is hardware-isolated and
     *                          device unlock is required before reading/writing.
     *                          Use `ksafe-biometrics` to prompt the user.
     */
    fun secureString(
        key: String,
        default: String = "",
        biometricRequired: Boolean = false,
    ): ReadWriteProperty<Any?, String>

    /**
     * Returns a [ReadWriteProperty] delegate that stores a [Boolean] value
     * encrypted with AES-256-GCM under the given [key].
     */
    fun secureBool(
        key: String,
        default: Boolean = false,
        biometricRequired: Boolean = false,
    ): ReadWriteProperty<Any?, Boolean>

    /**
     * Returns a [ReadWriteProperty] delegate that stores an [Int] value
     * encrypted with AES-256-GCM under the given [key].
     */
    fun secureInt(
        key: String,
        default: Int = 0,
        biometricRequired: Boolean = false,
    ): ReadWriteProperty<Any?, Int>

    /**
     * Returns a [ReadWriteProperty] delegate that stores a [Long] value
     * encrypted with AES-256-GCM under the given [key].
     */
    fun secureLong(
        key: String,
        default: Long = 0L,
        biometricRequired: Boolean = false,
    ): ReadWriteProperty<Any?, Long>

    // ── Reactive ──────────────────────────────────────────────────────────

    /**
     * Returns a [StateFlow] that emits the current value stored at [key] and
     * updates on every write. The flow is backed by the [CoroutineScope]
     * passed to [KSafeSettings] at construction time.
     *
     * @param key     Unique storage key.
     * @param default Initial / fallback value.
     */
    fun <T> flow(key: String, default: T): StateFlow<T>

    // ── Utilities ─────────────────────────────────────────────────────────

    /**
     * Removes the value stored at [key]. Both data and encryption key are
     * erased from secure storage.
     */
    fun remove(key: String)

    /**
     * Removes all values managed by this [KSafeSettings] instance.
     */
    fun clear()

    /**
     * Returns `true` if a value has been stored under [key].
     */
    fun contains(key: String): Boolean
}

/**
 * Factory function that creates a [KSafeSettings] backed by [ksafe].
 *
 * ### Android
 * ```kotlin
 * val settings = KSafeSettings(KSafe(context), viewModelScope)
 * ```
 *
 * ### iOS / JVM / Web
 * ```kotlin
 * val settings = KSafeSettings(KSafe(), applicationScope)
 * ```
 *
 * @param ksafe  A configured [KSafe] instance (creates the storage backend).
 * @param scope  A [CoroutineScope] used to back [KSafeSettings.flow] StateFlows.
 */
fun KSafeSettings(ksafe: KSafe, scope: CoroutineScope): KSafeSettings =
    KSafeSettingsImpl(ksafe, scope)
