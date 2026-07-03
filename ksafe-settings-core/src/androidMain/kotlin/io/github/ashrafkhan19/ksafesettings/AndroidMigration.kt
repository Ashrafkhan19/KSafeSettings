package io.github.ashrafkhan19.ksafesettings

import android.content.SharedPreferences

/**
 * Migrates a set of key-value pairs from a [SharedPreferences] instance into
 * this [KSafeSettings] as **plain (unencrypted)** entries.
 *
 * Call this once during app startup when migrating from a legacy
 * `SharedPreferences` store. After migration, the original preferences are
 * **not** automatically cleared — remove them yourself if needed.
 *
 * ### Example
 * ```kotlin
 * val legacyPrefs = getSharedPreferences("legacy", Context.MODE_PRIVATE)
 * settings.migrate(from = legacyPrefs, keys = listOf("user_id", "theme"))
 * ```
 *
 * @param from The [SharedPreferences] instance to read values from.
 * @param keys The list of keys to migrate. Keys not present in [from] are skipped.
 */
fun KSafeSettings.migrate(from: SharedPreferences, keys: List<String>) {
    val all = from.all
    keys.forEach { key ->
        val value = all[key] ?: return@forEach
        when (value) {
            is Boolean -> { var v by bool(key); v = value }
            is Int     -> { var v by int(key); v = value }
            is Long    -> { var v by long(key); v = value }
            is Float   -> { var v by string(key); v = value.toString() }
            is String  -> { var v by string(key); v = value }
            is Set<*>  -> { var v by string(key); v = value.joinToString(",") }
        }
    }
}
