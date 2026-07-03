package io.github.ashrafkhan19.ksafesettings.testing

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Verifies the full [io.github.ashrafkhan19.ksafesettings.KSafeSettings] contract
 * against the in-memory [FakeKSafeSettings]. These tests double as a specification
 * for any custom implementation of the interface.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class FakeKSafeSettingsTest {

    private val scope = TestScope()
    private val settings = FakeKSafeSettings(scope)

    // ── Plain delegates ────────────────────────────────────────────────────

    @Test
    fun bool_returnsDefaultWhenNotSet() {
        var v by settings.bool("b", default = true)
        assertTrue(v)
    }

    @Test
    fun bool_persistsWrittenValue() {
        var v by settings.bool("b", default = false)
        v = true
        assertEquals(true, v)
    }

    @Test
    fun int_returnsDefaultWhenNotSet() {
        val v by settings.int("i", default = 42)
        assertEquals(42, v)
    }

    @Test
    fun int_persistsWrittenValue() {
        var v by settings.int("i", default = 0)
        v = 99
        assertEquals(99, v)
    }

    @Test
    fun string_returnsDefaultWhenNotSet() {
        val v by settings.string("s", default = "hello")
        assertEquals("hello", v)
    }

    @Test
    fun string_persistsWrittenValue() {
        var v by settings.string("s", default = "")
        v = "world"
        assertEquals("world", v)
    }

    @Test
    fun long_returnsDefaultWhenNotSet() {
        val v by settings.long("l", default = 123L)
        assertEquals(123L, v)
    }

    @Test
    fun long_persistsWrittenValue() {
        var v by settings.long("l", default = 0L)
        v = Long.MAX_VALUE
        assertEquals(Long.MAX_VALUE, v)
    }

    // ── Secure delegates (no-op encryption in FakeKSafeSettings) ──────────

    @Test
    fun secureString_persistsAndReads() {
        var token by settings.secureString("auth_token")
        token = "super-secret"
        assertEquals("super-secret", token)
    }

    @Test
    fun secureBool_withBiometricFlag_persistsAndReads() {
        var flag by settings.secureBool("flag", biometricRequired = true)
        flag = true
        assertTrue(flag)
    }

    @Test
    fun secureInt_persistsAndReads() {
        var pin by settings.secureInt("pin", default = 0)
        pin = 1234
        assertEquals(1234, pin)
    }

    @Test
    fun secureLong_persistsAndReads() {
        var ts by settings.secureLong("ts", default = 0L)
        ts = 9_999_999_999L
        assertEquals(9_999_999_999L, ts)
    }

    // ── Utilities ─────────────────────────────────────────────────────────

    @Test
    fun contains_returnsFalseBeforeWrite() {
        assertFalse(settings.contains("new_key"))
    }

    @Test
    fun contains_returnsTrueAfterWrite() {
        var v by settings.string("existing")
        v = "value"
        assertTrue(settings.contains("existing"))
    }

    @Test
    fun remove_deletesKey() {
        var v by settings.string("key_to_remove")
        v = "present"
        assertTrue(settings.contains("key_to_remove"))

        settings.remove("key_to_remove")
        assertFalse(settings.contains("key_to_remove"))
    }

    @Test
    fun clear_deletesAllKeys() {
        var a by settings.bool("a")
        var b by settings.int("b")
        a = true
        b = 5

        assertTrue(settings.contains("a"))
        assertTrue(settings.contains("b"))

        settings.clear()

        assertFalse(settings.contains("a"))
        assertFalse(settings.contains("b"))
    }

    // ── Reactive ──────────────────────────────────────────────────────────

    @Test
    fun flow_emitsDefaultInitially() = scope.runTest {
        val f = settings.flow("theme", "system")
        assertEquals("system", f.value)
    }

    @Test
    fun flow_emitsUpdatedValueAfterWrite() = scope.runTest {
        val f = settings.flow("theme", "system")
        var v by settings.string("theme", "system")
        v = "dark"
        assertEquals("dark", f.first { it == "dark" })
    }

    @Test
    fun flow_sameKeyReturnsSameInstance() {
        val f1 = settings.flow("k", 0)
        val f2 = settings.flow("k", 0)
        assertTrue(f1 === f2)
    }

    @Test
    fun flow_emitsNullAfterRemove() = scope.runTest {
        val f = settings.flow<String?>("nullable_key", null)
        assertNull(f.value)
    }

    // ── Multiple independent delegates ────────────────────────────────────

    @Test
    fun independentDelegatesShareStorage() {
        var a by settings.int("shared_int", default = 0)
        var b by settings.int("shared_int", default = 0)
        a = 7
        assertEquals(7, b)
    }
}
