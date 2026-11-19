package com.fahadali.oneos.core.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class HostSuspendTest {

    private val ok = "com.example.chat"

    private fun reason(pkg: String, facts: HostSuspend.Facts = HostSuspend.Facts(), allow: Set<String> = emptySet()) =
        HostSuspend.excludeReason(pkg, facts, allow)

    @Test
    fun thirdPartyUserAppMayBeSuspended() {
        assertNull(reason(ok))
        assertTrue(HostSuspend.maySuspend(ok))
    }

    @Test
    fun invalidNamesAreRejected() {
        assertEquals(HostSuspend.Reason.INVALID, reason(""))
        assertEquals(HostSuspend.Reason.INVALID, reason("com"))
        assertEquals(HostSuspend.Reason.INVALID, reason("1bad.pkg"))
        assertEquals(HostSuspend.Reason.INVALID, reason("com.example.chat;pm"))
        assertEquals(HostSuspend.Reason.INVALID, reason("com.example.chat extra"))
    }

    @Test
    fun neverTouchesOneOsOrItsSiblings() {
        assertEquals(HostSuspend.Reason.OWN, reason("com.fahadali.oneos"))
        assertEquals(HostSuspend.Reason.OWN, reason("com.fahadali.oneos.guest"))
        assertEquals(HostSuspend.Reason.OWN, reason("com.fahadali.oneos.probe"))
        assertEquals(HostSuspend.Reason.OWN, reason("com.fahadali.oneos.extra"))
    }

    @Test
    fun neverTouchesSamsungSecAndroidPrefixes() {
        assertEquals(HostSuspend.Reason.PREFIX, reason("com.samsung.android.app"))
        assertEquals(HostSuspend.Reason.PREFIX, reason("com.sec.android.app.launcher"))
        assertEquals(HostSuspend.Reason.PREFIX, reason("com.android.systemui"))
        assertEquals(HostSuspend.Reason.PREFIX, reason("com.android.phone"))
    }

    @Test
    fun neverTouchesGmsAndGsf() {
        assertEquals(HostSuspend.Reason.GMS, reason("com.google.android.gms"))
        assertEquals(HostSuspend.Reason.GMS, reason("com.google.android.gsf"))
        assertEquals(HostSuspend.Reason.GMS, reason("com.google.android.gms.unstable"))
        assertEquals(HostSuspend.Reason.GMS, reason("com.google.android.gsf.login"))
        assertNull(reason("com.google.android.youtube"))
    }

    @Test
    fun neverTouchesKnownRootManagers() {
        for (pkg in listOf(
            "me.weishu.kernelsu",
            "com.rifsxd.ksunext",
            "com.sukisu.ultra",
            "app.kitsusu.manager",
            "com.topjohnwu.magisk",
            "io.github.vvb2060.magisk",
            "io.github.huskydg.magisk",
            "me.bmax.apatch",
            "me.garfieldhan.apatch.next",
        )) {
            assertEquals(pkg, HostSuspend.Reason.ROOT_MANAGER, reason(pkg))
        }
    }

    @Test
    fun systemAndPrivilegedAreExcluded() {
        assertEquals(HostSuspend.Reason.SYSTEM, reason(ok, HostSuspend.Facts(system = true)))
        assertEquals(HostSuspend.Reason.PRIVILEGED, reason(ok, HostSuspend.Facts(privileged = true)))
    }

    @Test
    fun defaultRolesAndServicesAreExcluded() {
        assertEquals(HostSuspend.Reason.LAUNCHER, reason(ok, HostSuspend.Facts(launcher = true)))
        assertEquals(HostSuspend.Reason.DIALER, reason(ok, HostSuspend.Facts(dialer = true)))
        assertEquals(HostSuspend.Reason.SMS, reason(ok, HostSuspend.Facts(sms = true)))
        assertEquals(HostSuspend.Reason.IME, reason(ok, HostSuspend.Facts(ime = true)))
        assertEquals(HostSuspend.Reason.ACCESSIBILITY, reason(ok, HostSuspend.Facts(accessibility = true)))
        assertEquals(HostSuspend.Reason.DEVICE_ADMIN, reason(ok, HostSuspend.Facts(deviceAdmin = true)))
        assertEquals(HostSuspend.Reason.VPN, reason(ok, HostSuspend.Facts(vpn = true)))
        assertEquals(HostSuspend.Reason.FOREGROUND, reason(ok, HostSuspend.Facts(foreground = true)))
    }

    @Test
    fun userAllowlistIsHonoured() {
        assertEquals(HostSuspend.Reason.ALLOWLIST, reason(ok, allow = setOf(ok)))
        assertNull(reason(ok, allow = setOf("com.other.app")))
    }

    @Test
    fun nameRulesWinOverAnAllowlist() {
        assertEquals(HostSuspend.Reason.OWN, reason("com.fahadali.oneos", allow = setOf("com.fahadali.oneos")))
        assertEquals(HostSuspend.Reason.PREFIX, reason("com.samsung.foo", allow = setOf("com.samsung.foo")))
        assertEquals(HostSuspend.Reason.GMS, reason("com.google.android.gms", allow = setOf("com.google.android.gms")))
        assertEquals(HostSuspend.Reason.ROOT_MANAGER, reason("com.topjohnwu.magisk", allow = setOf("com.topjohnwu.magisk")))
    }

    @Test
    fun parseListKeepsValidUniqueNamesInOrderAndDropsJunk() {
        val text = """
            com.example.one
            not-a-package
            com.example.two
            com.example.one

            com.example.three extra
            com.example.three
        """.trimIndent()
        assertEquals(
            listOf("com.example.one", "com.example.two", "com.example.three"),
            HostSuspend.parseList(text),
        )
    }

    @Test
    fun formatListIsStableAndRoundTrips() {
        assertEquals("", HostSuspend.formatList(emptyList()))
        val text = HostSuspend.formatList(listOf("com.b.app", "bad", "com.a.app", "com.b.app"))
        assertEquals("com.b.app\ncom.a.app\n", text)
        assertEquals(listOf("com.b.app", "com.a.app"), HostSuspend.parseList(text))
    }

    @Test
    fun mergeAndMinusKeepTheExactRestoreBookkeeping() {
        val first = HostSuspend.merge(emptyList(), listOf("com.a.app", "com.b.app"))
        assertEquals(listOf("com.a.app", "com.b.app"), first)
        val again = HostSuspend.merge(first, listOf("com.b.app", "com.c.app"))
        assertEquals(listOf("com.a.app", "com.b.app", "com.c.app"), again)
        val after = HostSuspend.minus(again, listOf("com.b.app", "not-a-package", "com.missing.app"))
        assertEquals(listOf("com.a.app", "com.c.app"), after)
        // Restore must unsuspend exactly the persisted list, never a freshly scanned one.
        val persisted = HostSuspend.formatList(first)
        assertEquals(first, HostSuspend.parseList(persisted))
        assertFalse("com.c.app" in HostSuspend.parseList(persisted))
    }
}
