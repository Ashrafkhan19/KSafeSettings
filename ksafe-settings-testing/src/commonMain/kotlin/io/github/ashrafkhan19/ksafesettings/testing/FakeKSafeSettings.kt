package io.github.ashrafkhan19.ksafesettings.testing

import eu.anifantakis.lib.ksafe.KSafe
import io.github.ashrafkhan19.ksafesettings.KSafeSettings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * An in-memory, no-encryption implementation of [KSafeSettings] designed for
 * use in unit tests. All values are stored in a plain [MutableMap] and no
 * actual [KSafe] instance is required.
 *
 * ### Usage
 * ```kotlin
 * val settings: KSafeSettings = FakeKSafeSettings(scope = TestScope())
 *
 * var isDarkMode by settings.bool("dark_mode")
 * isDarkMode = true
 * assertThat(isDarkMode).isTrue()
 * ```
 *
 * @param scope A [CoroutineScope] used to back [flow] StateFlows (e.g., `TestScope()`).
 */
class FakeKSafeSettings(private val scope: CoroutineScope) : KSafeSettings {

    /** Not used in tests; access triggers [UnsupportedOperationException]. */
    override val ksafe: KSafe
        get() = throw UnsupportedOperationException(
            "FakeKSafeSettings does not have a real KSafe instance. " +
                "Use a real KSafeSettings in integration tests."
        )

    private val storage: MutableMap<String, Any?> = mutableMapOf()
    private val flows: MutableMap<String, MutableStateFlow<Any?>> = mutableMapOf()

    // ── Internal helpers ───────────────────────────────────────────────────

    @Suppress("UNCHECKED_CAST")
    private fun <T> rawGet(key: String, default: T): T =
        if (storage.containsKey(key)) storage[key] as T else default

    private fun <T> rawSet(key: String, value: T) {
        storage[key] = value
        flows[key]?.value = value
    }

    private fun <T> makeDelegate(key: String, default: T): ReadWriteProperty<Any?, T> =
        object : ReadWriteProperty<Any?, T> {
            override fun getValue(thisRef: Any?, property: KProperty<*>): T = rawGet(key, default)
            override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) = rawSet(key, value)
        }

    // ── Plain delegates ────────────────────────────────────────────────────

    override fun bool(key: String, default: Boolean): ReadWriteProperty<Any?, Boolean> =
        makeDelegate(key, default)

    override fun int(key: String, default: Int): ReadWriteProperty<Any?, Int> =
        makeDelegate(key, default)

    override fun string(key: String, default: String): ReadWriteProperty<Any?, String> =
        makeDelegate(key, default)

    override fun long(key: String, default: Long): ReadWriteProperty<Any?, Long> =
        makeDelegate(key, default)

    // ── Secure delegates (no-op encryption in tests) ───────────────────────

    override fun secureString(key: String, default: String, biometricRequired: Boolean): ReadWriteProperty<Any?, String> =
        makeDelegate(key, default)

    override fun secureBool(key: String, default: Boolean, biometricRequired: Boolean): ReadWriteProperty<Any?, Boolean> =
        makeDelegate(key, default)

    override fun secureInt(key: String, default: Int, biometricRequired: Boolean): ReadWriteProperty<Any?, Int> =
        makeDelegate(key, default)

    override fun secureLong(key: String, default: Long, biometricRequired: Boolean): ReadWriteProperty<Any?, Long> =
        makeDelegate(key, default)

    // ── Reactive ──────────────────────────────────────────────────────────

    @Suppress("UNCHECKED_CAST")
    override fun <T> flow(key: String, default: T): StateFlow<T> {
        val existing = flows[key]
        if (existing != null) return existing as StateFlow<T>

        val initial: T = rawGet(key, default)
        val mutable = MutableStateFlow<Any?>(initial)
        flows[key] = mutable
        // Return the MutableStateFlow directly so repeated calls return the same instance.
        return mutable as StateFlow<T>
    }

    // ── Utilities ─────────────────────────────────────────────────────────

    override fun remove(key: String) {
        storage.remove(key)
        flows[key]?.value = null
    }

    override fun clear() {
        storage.clear()
        flows.values.forEach { it.value = null }
    }

    override fun contains(key: String): Boolean = storage.containsKey(key)
}
