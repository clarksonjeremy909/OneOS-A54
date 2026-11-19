package com.fahadali.oneos.session

import android.content.Context
import android.os.Handler
import android.os.Looper
import com.fahadali.oneos.core.model.HostSuspend
import com.fahadali.oneos.core.root.RootScript
import com.fahadali.oneos.core.root.RootShell
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Talks to the `host-suspend` / `host-unsuspend` root verbs. The list of
 * packages that were actually frozen is kept in the stage (outside guest
 * userdata) and mirrored in [SessionPrefs] so a crash, a One UI return,
 * a boot, or opening the app can still unsuspend them. The unsuspend
 * verb also thaws any package still marked suspended by this app or by
 * `android` / shell (the identity `pm` records when it runs as root).
 */
object HostSuspendActions {
    @Volatile var busy: Boolean = false
        private set

    private val restoreStarted = AtomicBoolean(false)
    private val main = Handler(Looper.getMainLooper())

    /** True when a previous session left a list that still needs restoring. */
    fun pending(ctx: Context): Boolean = SessionPrefs.hostSuspendedList(ctx).isNotEmpty()

    /**
     * Crash / boot / launch recovery. Always asks the root script to thaw the
     * saved list plus any leftover packages suspended by this app or by
     * android/shell, even when the saved list is empty. Starts at most once
     * per process; runs off the main thread.
     */
    fun restoreOnStart(ctx: Context, after: (() -> Unit)? = null) {
        val app = ctx.applicationContext
        if (!restoreStarted.compareAndSet(false, true)) {
            after?.let { main.post(it) }
            return
        }
        unsuspend(app, after)
    }

    /**
     * Called from [LaunchJob] on its worker thread just before a real switch.
     * Failures are returned, never thrown: the switch still goes ahead.
     */
    fun suspendEligible(ctx: Context): String? {
        if (!SessionPrefs.hostSuspend(ctx)) return null
        val pkgs = HostSuspendApps.eligible(ctx)
        if (pkgs.isEmpty()) return null
        // Persist the intended list first so a crash mid-verb can still restore it.
        val already = SessionPrefs.hostSuspendedList(ctx)
        SessionPrefs.setHostSuspendedList(ctx, HostSuspend.merge(already, pkgs))
        val body = HostSuspend.formatList(pkgs).toByteArray(Charsets.UTF_8)
        return try {
            val reply = RootVerbs.run(
                ctx, RootScript.Verb.HOST_SUSPEND, listOf(body.size.toString()),
                feed = RootShell.Feed { it.write(body) },
                silenceMs = 60_000L,
            )
            val fromRoot = prefixed(reply.reply.results, "host_suspend ")
            val kept = if (fromRoot.isNotEmpty()) HostSuspend.merge(already, fromRoot) else SessionPrefs.hostSuspendedList(ctx)
            SessionPrefs.setHostSuspendedList(ctx, kept)
            if (!reply.ok) reply.problem ?: "host-suspend refused" else null
        } catch (e: Exception) {
            e.message ?: e.javaClass.simpleName
        }
    }

    fun unsuspend(ctx: Context, after: (() -> Unit)? = null) {
        val app = ctx.applicationContext
        busy = true
        Thread({
            try {
                runUnsuspend(app)
            } finally {
                busy = false
                after?.let { main.post(it) }
            }
        }, "oneos-host-unsuspend").start()
    }

    /** Worker-thread unsuspend for [LaunchJob] when a switch is refused. */
    fun unsuspendBlocking(ctx: Context) {
        runUnsuspend(ctx.applicationContext)
    }

    private fun prefixed(results: List<String>, prefix: String): List<String> =
        HostSuspend.parseList(results.joinToString("\n") { it.removePrefix(prefix).let { n -> if (n == it) "" else n } })

    private fun runUnsuspend(ctx: Context) {
        val backup = SessionPrefs.hostSuspendedList(ctx)
        val body = HostSuspend.formatList(backup).toByteArray(Charsets.UTF_8)
        try {
            val reply = RootVerbs.run(
                ctx, RootScript.Verb.HOST_UNSUSPEND, listOf(body.size.toString()),
                feed = RootShell.Feed { it.write(body) }, silenceMs = 60_000L,
            )
            val left = prefixed(reply.reply.results, "host_unsuspend_left ")
            when {
                reply.ok -> SessionPrefs.setHostSuspendedList(ctx, left)
                left.isNotEmpty() -> SessionPrefs.setHostSuspendedList(ctx, left)
                else -> SessionPrefs.setHostSuspendedList(ctx, backup)
            }
        } catch (_: Exception) {
            // Leave the list in prefs so the next start can try again.
        }
    }
}
