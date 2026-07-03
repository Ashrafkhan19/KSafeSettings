package io.github.ashrafkhan19.ksafesettings

import eu.anifantakis.lib.ksafe.KSafe
import eu.anifantakis.lib.ksafe.KSafeEncryptedProtection
import eu.anifantakis.lib.ksafe.KSafeWriteMode
import kotlin.properties.ReadWriteProperty

/**
 * Factory functions that create property delegates for **secure (AES-256-GCM
 * encrypted)** persistence via KSafe.
 *
 * Use these for sensitive data such as auth tokens, PINs, or account
 * identifiers. When [biometricRequired] is `true` the encryption key is
 * hardware-isolated and the device must be unlocked before access.
 *
 * All functions are internal — callers use the [KSafeSettings] interface.
 */
internal object SecureSettings {

    /**
     * Returns the [KSafeWriteMode] appropriate for the requested security level.
     *
     * @param biometricRequired When `true`, uses [KSafeEncryptedProtection.HARDWARE_ISOLATED]
     *                          with `requireUnlockedDevice = true`. When `false`,
     *                          uses [KSafeEncryptedProtection.DEFAULT].
     */
    private fun writeMode(biometricRequired: Boolean): KSafeWriteMode =
        if (biometricRequired) {
            KSafeWriteMode.Encrypted(
                protection = KSafeEncryptedProtection.HARDWARE_ISOLATED,
                requireUnlockedDevice = true,
            )
        } else {
            KSafeWriteMode.Encrypted(
                protection = KSafeEncryptedProtection.DEFAULT,
            )
        }

    /**
     * Creates a [ReadWriteProperty] that stores a [String] encrypted with
     * AES-256-GCM.
     *
     * @param ksafe             The KSafe storage backend.
     * @param key               Explicit storage key.
     * @param default           Value returned when no value is stored for [key].
     * @param biometricRequired Require hardware-isolated key and unlocked device.
     */
    fun string(
        ksafe: KSafe,
        key: String,
        default: String,
        biometricRequired: Boolean,
    ): ReadWriteProperty<Any?, String> =
        SettingsDelegate(
            getter = { ksafe.getDirect(key, default) },
            setter = { ksafe.putDirect(key, it, mode = writeMode(biometricRequired)) },
        )

    /**
     * Creates a [ReadWriteProperty] that stores a [Boolean] encrypted with
     * AES-256-GCM.
     */
    fun bool(
        ksafe: KSafe,
        key: String,
        default: Boolean,
        biometricRequired: Boolean,
    ): ReadWriteProperty<Any?, Boolean> =
        SettingsDelegate(
            getter = { ksafe.getDirect(key, default) },
            setter = { ksafe.putDirect(key, it, mode = writeMode(biometricRequired)) },
        )

    /**
     * Creates a [ReadWriteProperty] that stores an [Int] encrypted with
     * AES-256-GCM.
     */
    fun int(
        ksafe: KSafe,
        key: String,
        default: Int,
        biometricRequired: Boolean,
    ): ReadWriteProperty<Any?, Int> =
        SettingsDelegate(
            getter = { ksafe.getDirect(key, default) },
            setter = { ksafe.putDirect(key, it, mode = writeMode(biometricRequired)) },
        )

    /**
     * Creates a [ReadWriteProperty] that stores a [Long] encrypted with
     * AES-256-GCM.
     */
    fun long(
        ksafe: KSafe,
        key: String,
        default: Long,
        biometricRequired: Boolean,
    ): ReadWriteProperty<Any?, Long> =
        SettingsDelegate(
            getter = { ksafe.getDirect(key, default) },
            setter = { ksafe.putDirect(key, it, mode = writeMode(biometricRequired)) },
        )
}
