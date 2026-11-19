package com.fahadali.oneos.core.model

/**
 * OneOS A54: which host (One UI) apps may be reversibly frozen with
 * `pm suspend --user 0` while a guest session runs. Package-manager facts
 * (system, IME, launcher, …) are supplied by the app; this object is the
 * pure exclusion and bookkeeping used by the unit tests and the root verb.
 */
object HostSuspend {

    val PACKAGE = BringApps.PACKAGE

    /** OneOS itself: never suspended. */
    val OWN_PACKAGES: Set<String> = setOf(
        "com.fahadali.oneos",
        "com.fahadali.oneos.guest",
        "com.fahadali.oneos.probe",
    )

    /** Prefixes that are never touched, including every sub-package. */
    val FORBIDDEN_PREFIXES: List<String> = listOf(
        "com.samsung.",
        "com.sec.",
        "com.android.",
    )

    /** Exact Play services / framework packages, plus any sub-package. */
    val GMS_PACKAGES: Set<String> = setOf(
        "com.google.android.gms",
        "com.google.android.gsf",
    )

    val GMS_PREFIXES: List<String> = listOf(
        "com.google.android.gms.",
        "com.google.android.gsf.",
    )

    /** Root managers already known to OneOS, plus a few well-known forks. */
    val ROOT_MANAGER_PACKAGES: Set<String> =
        RootRules.MANAGER_PACKAGES.keys + setOf(
            // Extra Magisk / KernelSU / APatch forks and common temp-root UIs.
            "io.github.vvb2060.magisk",
            "io.github.huskydg.magisk",
            "me.garfieldhan.apatch.next",
        )

    /** Why a package must not be suspended. Null = it may be frozen. */
    enum class Reason {
        INVALID,
        OWN,
        PREFIX,
        GMS,
        ROOT_MANAGER,
        SYSTEM,
        PRIVILEGED,
        LAUNCHER,
        DIALER,
        SMS,
        IME,
        ACCESSIBILITY,
        DEVICE_ADMIN,
        VPN,
        FOREGROUND,
        ALLOWLIST,
    }

    /**
     * Package-manager facts the app fills in. Name-based rules (OneOS, prefixes,
     * GMS, known root managers) are applied here even when every flag is false.
     */
    data class Facts(
        val system: Boolean = false,
        val privileged: Boolean = false,
        val launcher: Boolean = false,
        val dialer: Boolean = false,
        val sms: Boolean = false,
        val ime: Boolean = false,
        val accessibility: Boolean = false,
        val deviceAdmin: Boolean = false,
        val vpn: Boolean = false,
        val foreground: Boolean = false,
    )

    fun excludeReason(pkg: String, facts: Facts = Facts(), allowlist: Set<String> = emptySet()): Reason? {
        if (!PACKAGE.matches(pkg)) return Reason.INVALID
        if (pkg in OWN_PACKAGES || pkg.startsWith("com.fahadali.oneos.")) return Reason.OWN
        if (FORBIDDEN_PREFIXES.any { pkg.startsWith(it) }) return Reason.PREFIX
        if (pkg in GMS_PACKAGES || GMS_PREFIXES.any { pkg.startsWith(it) }) return Reason.GMS
        if (pkg in ROOT_MANAGER_PACKAGES) return Reason.ROOT_MANAGER
        if (facts.system) return Reason.SYSTEM
        if (facts.privileged) return Reason.PRIVILEGED
        if (facts.launcher) return Reason.LAUNCHER
        if (facts.dialer) return Reason.DIALER
        if (facts.sms) return Reason.SMS
        if (facts.ime) return Reason.IME
        if (facts.accessibility) return Reason.ACCESSIBILITY
        if (facts.deviceAdmin) return Reason.DEVICE_ADMIN
        if (facts.vpn) return Reason.VPN
        if (facts.foreground) return Reason.FOREGROUND
        if (pkg in allowlist) return Reason.ALLOWLIST
        return null
    }

    fun maySuspend(pkg: String, facts: Facts = Facts(), allowlist: Set<String> = emptySet()): Boolean =
        excludeReason(pkg, facts, allowlist) == null

    /** Valid unique package names, first-seen order; other lines are dropped. */
    fun parseList(text: String): List<String> {
        val out = LinkedHashSet<String>()
        for (raw in text.splitToSequence('\n', '\r')) {
            val line = raw.trim()
            if (line.isEmpty()) continue
            if (PACKAGE.matches(line)) out += line
        }
        return out.toList()
    }

    /** One package per line, trailing newline when not empty. */
    fun formatList(packages: Collection<String>): String {
        val names = parseList(packages.joinToString("\n"))
        if (names.isEmpty()) return ""
        return names.joinToString("\n", postfix = "\n")
    }

    fun merge(existing: Collection<String>, added: Collection<String>): List<String> =
        parseList((existing + added).joinToString("\n"))

    fun minus(existing: Collection<String>, removed: Collection<String>): List<String> {
        val drop = parseList(removed.joinToString("\n")).toSet()
        return parseList(existing.joinToString("\n")).filterNot { it in drop }
    }
}
