package io.github.ashrafkhan19.ksafesettings

import eu.anifantakis.lib.ksafe.KSafe
import eu.anifantakis.lib.ksafe.KSafeWriteMode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * The KSafe-backed implementation of [KSafeSettings].
 *
 * Instantiate via the [KSafeSettings] factory function rather than
 * constructing this class directly.
 *
 * @param ksafe  The KSafe instance providing encrypted/plain persistence.
 * @param scope  The [CoroutineScope] used to keep [flow] StateFlows active and
 *               to dispatch the suspend [KSafe.clearAll] call from [clear].
 */
internal class KSafeSettingsImpl(
    override val ksafe: KSafe,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Default + SupervisorJob()),
) : KSafeSettings {

    // ── Key registry (for contains / remove / clear tracking) ─────────────

    private val REGISTRY_KEY = "\$__ksafe_settings_registry__\$"
    private val KEY_SEPARATOR = "|"

    /** Lazily loaded from KSafe on first access. */
    private val keyRegistry: MutableSet<String> by lazy {
        ksafe.getDirect(REGISTRY_KEY, "")
            .split(KEY_SEPARATOR)
            .filter { it.isNotEmpty() }
            .toMutableSet()
    }

    private fun persistRegistry() {
        ksafe.putDirect(REGISTRY_KEY, keyRegistry.joinToString(KEY_SEPARATOR), mode = KSafeWriteMode.Plain)
    }

    private fun trackKey(key: String) {
        if (key !in keyRegistry) {
            keyRegistry.add(key)
            persistRegistry()
        }
    }

    private fun untrackKey(key: String) {
        if (key in keyRegistry) {
            keyRegistry.remove(key)
            persistRegistry()
        }
    }

    // ── StateFlow registry (for flow() reactive API) ──────────────────────

    private val stateFlows: MutableMap<String, MutableStateFlow<Any?>> = mutableMapOf()

    private fun updateFlows(key: String, value: Any?) {
        stateFlows[key]?.value = value
    }

    /**
     * Loads the initial persisted value for [key] from KSafe without requiring
     * a reified type parameter. Supports the four primitive settings types.
     *
     * @param default Used both as the fallback value and to infer the type at
     *                compile time via the [when] branches (each branch calls an
     *                inline `getDirect` that is expanded with a concrete type).
     */
    @Suppress("UNCHECKED_CAST")
    private fun <T> loadInitialValue(key: String, default: T): T = when (default) {
        is Boolean -> ksafe.getDirect<Boolean>(key, default) as T
        is Int     -> ksafe.getDirect<Int>(key, default) as T
        is String  -> ksafe.getDirect<String>(key, default) as T
        is Long    -> ksafe.getDirect<Long>(key, default) as T
        else       -> default
    }

    /** Wraps a delegate to register the key and update reactive flows on write. */
    private fun <T> tracked(key: String, delegate: ReadWriteProperty<Any?, T>): ReadWriteProperty<Any?, T> =
        object : ReadWriteProperty<Any?, T> {
            override fun getValue(thisRef: Any?, property: KProperty<*>): T =
                delegate.getValue(thisRef, property)

            override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
                delegate.setValue(thisRef, property, value)
                trackKey(key)
                updateFlows(key, value)
            }
        }

    // ── Plain (unencrypted) delegates ──────────────────────────────────────

    override fun bool(key: String, default: Boolean): ReadWriteProperty<Any?, Boolean> =
        tracked(key, PlainSettings.bool(ksafe, key, default))

    override fun int(key: String, default: Int): ReadWriteProperty<Any?, Int> =
        tracked(key, PlainSettings.int(ksafe, key, default))

    override fun string(key: String, default: String): ReadWriteProperty<Any?, String> =
        tracked(key, PlainSettings.string(ksafe, key, default))

    override fun long(key: String, default: Long): ReadWriteProperty<Any?, Long> =
        tracked(key, PlainSettings.long(ksafe, key, default))

    // ── Secure (AES-256-GCM encrypted) delegates ──────────────────────────

    override fun secureString(
        key: String,
        default: String,
        biometricRequired: Boolean,
    ): ReadWriteProperty<Any?, String> =
        tracked(key, SecureSettings.string(ksafe, key, default, biometricRequired))

    override fun secureBool(
        key: String,
        default: Boolean,
        biometricRequired: Boolean,
    ): ReadWriteProperty<Any?, Boolean> =
        tracked(key, SecureSettings.bool(ksafe, key, default, biometricRequired))

    override fun secureInt(
        key: String,
        default: Int,
        biometricRequired: Boolean,
    ): ReadWriteProperty<Any?, Int> =
        tracked(key, SecureSettings.int(ksafe, key, default, biometricRequired))

    override fun secureLong(
        key: String,
        default: Long,
        biometricRequired: Boolean,
    ): ReadWriteProperty<Any?, Long> =
        tracked(key, SecureSettings.long(ksafe, key, default, biometricRequired))

    // ── Reactive ──────────────────────────────────────────────────────────

    /**
     * Returns a [StateFlow] backed by an in-memory [MutableStateFlow] whose
     * initial value is loaded from KSafe storage.
     *
     * The flow is updated automatically on every write made through any
     * [KSafeSettings] delegate. External writes made directly to the underlying
     * [KSafe] instance are NOT reflected — use [ksafe] for those scenarios.
     *
     * Supported types for correct initial-value loading: [Boolean], [Int],
     * [String], [Long]. Other types start at [default].
     */
    @Suppress("UNCHECKED_CAST")
    override fun <T> flow(key: String, default: T): StateFlow<T> {
        val existing = stateFlows[key]
        if (existing != null) return existing as StateFlow<T>

        val initial = loadInitialValue(key, default)
        val mutable = MutableStateFlow<Any?>(initial)
        stateFlows[key] = mutable
        return mutable as StateFlow<T>
    }

    // ── Utilities ─────────────────────────────────────────────────────────

    override fun remove(key: String) {
        ksafe.deleteDirect(key)
        untrackKey(key)
        stateFlows[key]?.value = null
    }

    /**
     * Removes all values from KSafe storage and clears the key registry.
     * The underlying [KSafe.clearAll] is `suspend` and is launched on the
     * injected [scope].
     */
    override fun clear() {
        keyRegistry.clear()
        persistRegistry()
        stateFlows.values.forEach { it.value = null }
        scope.launch { ksafe.clearAll() }
    }

    override fun contains(key: String): Boolean = key in keyRegistry
}
