package com.fahadali.oneos.session

import android.app.ActivityManager
import android.app.admin.DevicePolicyManager
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.net.VpnService
import android.provider.Telephony
import android.telecom.TelecomManager
import android.view.accessibility.AccessibilityManager
import android.view.inputmethod.InputMethodManager
import com.fahadali.oneos.core.model.HostSuspend

/** Fills [HostSuspend.Facts] from this phone's package manager. */
object HostSuspendApps {

    data class Entry(val pkg: String, val label: String, val facts: HostSuspend.Facts)

    fun entries(ctx: Context): List<Entry> {
        val pm = ctx.packageManager
        val roles = Roles.collect(ctx, pm)
        val flags = PackageManager.MATCH_UNINSTALLED_PACKAGES or PackageManager.MATCH_DISABLED_COMPONENTS
        return pm.getInstalledApplications(flags).map { ai ->
            val pkg = ai.packageName
            Entry(pkg, label(pm, ai), roles.facts(ai, pkg))
        }.sortedBy { it.label.lowercase() }
    }

    /** Third-party host apps that pass the exclusion rules and are not on the allowlist. */
    fun eligible(ctx: Context): List<String> {
        val allow = SessionPrefs.hostSuspendAllowlist(ctx)
        return entries(ctx).mapNotNull { e ->
            e.pkg.takeIf { HostSuspend.maySuspend(it, e.facts, allow) }
        }
    }

    /** User-installed apps the owner can tick to keep running (system and hard excludes stay hidden). */
    fun allowlistCandidates(ctx: Context): List<Entry> {
        return entries(ctx).filter { e ->
            val hard = HostSuspend.excludeReason(e.pkg, e.facts.copy(foreground = false), allowlist = emptySet())
            hard == null || hard == HostSuspend.Reason.ALLOWLIST
        }
    }

    private fun label(pm: PackageManager, ai: ApplicationInfo): String =
        try {
            pm.getApplicationLabel(ai).toString().ifBlank { ai.packageName }
        } catch (_: Exception) {
            ai.packageName
        }

    private class Roles(
        val launcher: String?,
        val dialer: String?,
        val sms: String?,
        val ime: Set<String>,
        val accessibility: Set<String>,
        val deviceAdmin: Set<String>,
        val vpn: Set<String>,
        val foreground: Set<String>,
    ) {
        fun facts(ai: ApplicationInfo, pkg: String): HostSuspend.Facts {
            val system = (ai.flags and (ApplicationInfo.FLAG_SYSTEM or ApplicationInfo.FLAG_UPDATED_SYSTEM_APP)) != 0
            return HostSuspend.Facts(
                system = system,
                privileged = privileged(ai),
                launcher = pkg == launcher,
                dialer = pkg == dialer,
                sms = pkg == sms,
                ime = pkg in ime,
                accessibility = pkg in accessibility,
                deviceAdmin = pkg in deviceAdmin,
                vpn = pkg in vpn,
                foreground = pkg in foreground,
            )
        }

        companion object {
            fun collect(ctx: Context, pm: PackageManager): Roles {
                val home = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME)
                val launcher = try {
                    pm.resolveActivity(home, PackageManager.MATCH_DEFAULT_ONLY)?.activityInfo?.packageName
                } catch (_: Exception) {
                    null
                }
                val dialer = try {
                    ctx.getSystemService(TelecomManager::class.java)?.defaultDialerPackage
                } catch (_: Exception) {
                    null
                }
                val sms = try {
                    Telephony.Sms.getDefaultSmsPackage(ctx)
                } catch (_: Exception) {
                    null
                }
                val ime = try {
                    ctx.getSystemService(InputMethodManager::class.java)
                        ?.enabledInputMethodList
                        ?.mapNotNull { it.packageName }
                        ?.toSet()
                        .orEmpty()
                } catch (_: Exception) {
                    emptySet()
                }
                val accessibility = try {
                    ctx.getSystemService(AccessibilityManager::class.java)
                        ?.getEnabledAccessibilityServiceList(-1)
                        ?.mapNotNull { it.resolveInfo?.serviceInfo?.packageName }
                        ?.toSet()
                        .orEmpty()
                } catch (_: Exception) {
                    emptySet()
                }
                val deviceAdmin = try {
                    ctx.getSystemService(DevicePolicyManager::class.java)
                        ?.activeAdmins
                        ?.map { it.packageName }
                        ?.toSet()
                        .orEmpty()
                } catch (_: Exception) {
                    emptySet()
                }
                val vpn = try {
                    pm.queryIntentServices(Intent(VpnService.SERVICE_INTERFACE), PackageManager.GET_META_DATA)
                        .mapNotNull { it.serviceInfo?.packageName }
                        .toSet()
                } catch (_: Exception) {
                    emptySet()
                }
                return Roles(launcher, dialer, sms, ime, accessibility, deviceAdmin, vpn, foreground(ctx))
            }

            private fun foreground(ctx: Context): Set<String> {
                val out = linkedSetOf<String>()
                out += ctx.packageName
                try {
                    val am = ctx.getSystemService(ActivityManager::class.java) ?: return out
                    am.appTasks?.forEach { task ->
                        task.taskInfo?.topActivity?.packageName?.let { out += it }
                        task.taskInfo?.baseActivity?.packageName?.let { out += it }
                    }
                    am.runningAppProcesses?.forEach { proc ->
                        if (proc.importance <= ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                            proc.pkgList?.forEach { out += it }
                        }
                    }
                } catch (_: Exception) {
                    // Keep at least this app itself.
                }
                return out
            }

            private fun privileged(ai: ApplicationInfo): Boolean =
                try {
                    ApplicationInfo::class.java.getMethod("isPrivilegedApp").invoke(ai) as Boolean
                } catch (_: Exception) {
                    try {
                        val f = ApplicationInfo::class.java.getDeclaredField("privateFlags")
                        f.isAccessible = true
                        (f.getInt(ai) and 0x00000008) != 0
                    } catch (_: Exception) {
                        false
                    }
                }
        }
    }
}
