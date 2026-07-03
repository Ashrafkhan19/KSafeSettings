package io.github.ashrafkhan19.ksafesettings

import eu.anifantakis.lib.ksafe.KSafe
import eu.anifantakis.lib.ksafe.KSafeWriteMode
import kotlin.properties.ReadWriteProperty

/**
 * Factory functions that create property delegates for **plain (unencrypted)**
 * persistence via KSafe. Use these for non-sensitive user preferences such as
 * theme, font size, or UI state.
 *
 * All functions are internal — callers use the [KSafeSettings] interface.
 */
internal object PlainSettings {

    /**
     * Creates a [ReadWriteProperty] that stores a [Boolean] in plain text.
     *
     * @param ksafe   The KSafe storage backend.
     * @param key     Explicit storage key.
     * @param default Value returned when no value is stored for [key].
     */
    fun bool(ksafe: KSafe, key: String, default: Boolean): ReadWriteProperty<Any?, Boolean> =
        SettingsDelegate(
            getter = { ksafe.getDirect(key, default) },
            setter = { ksafe.putDirect(key, it, mode = KSafeWriteMode.Plain) },
        )

    /**
     * Creates a [ReadWriteProperty] that stores an [Int] in plain text.
     */
    fun int(ksafe: KSafe, key: String, default: Int): ReadWriteProperty<Any?, Int> =
        SettingsDelegate(
            getter = { ksafe.getDirect(key, default) },
            setter = { ksafe.putDirect(key, it, mode = KSafeWriteMode.Plain) },
        )

    /**
     * Creates a [ReadWriteProperty] that stores a [String] in plain text.
     */
    fun string(ksafe: KSafe, key: String, default: String): ReadWriteProperty<Any?, String> =
        SettingsDelegate(
            getter = { ksafe.getDirect(key, default) },
            setter = { ksafe.putDirect(key, it, mode = KSafeWriteMode.Plain) },
        )

    /**
     * Creates a [ReadWriteProperty] that stores a [Long] in plain text.
     */
    fun long(ksafe: KSafe, key: String, default: Long): ReadWriteProperty<Any?, Long> =
        SettingsDelegate(
            getter = { ksafe.getDirect(key, default) },
            setter = { ksafe.putDirect(key, it, mode = KSafeWriteMode.Plain) },
        )
}
