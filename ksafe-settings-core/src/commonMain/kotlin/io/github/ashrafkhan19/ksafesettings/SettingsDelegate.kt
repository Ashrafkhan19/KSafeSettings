package io.github.ashrafkhan19.ksafesettings

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * A [ReadWriteProperty] that persists a value of type [T] via the provided
 * [getter] and [setter] lambdas. Both lambdas are called on every property
 * access; caching is handled by KSafe's internal hot-cache layer.
 *
 * @param T The type of the stored value.
 */
internal class SettingsDelegate<T>(
    private val getter: () -> T,
    private val setter: (T) -> Unit,
) : ReadWriteProperty<Any?, T> {

    override fun getValue(thisRef: Any?, property: KProperty<*>): T = getter()

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) = setter(value)
}
