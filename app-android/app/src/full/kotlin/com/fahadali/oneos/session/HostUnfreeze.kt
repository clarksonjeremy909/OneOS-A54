package com.fahadali.oneos.session

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Handler
import android.os.Looper
import com.fahadali.oneos.ui.MainActivity
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Thaw every suspended package on the phone (hotfix 0.5.24.2). Opening
 * OneOS, opening the home screen, tapping Unfreeze, or installing an in-app
 * update all run the same root loop: `pm unsuspend` on every package, with
 * `cmd package unsuspend` as a fallback. This is broader than the recorded
 * host-suspend list so apps that stayed grey after a return (Termux,
 * KernelSU Manager, stores) come back even if the list was lost.
 */
object HostUnfreeze {
    private val running = AtomicBoolean(false)
    private val startedThisProcess = AtomicBoolean(false)
    private val completedOk = AtomicBoolean(false)
    private val main = Handler(Looper.getMainLooper())

    const val SCRIPT = "for p in \$(pm list packages | cut -d: -f2); do " +
        "pm unsuspend \"\$p\" >/dev/null 2>&1 " +
        "|| pm unsuspend --user 0 \"\$p\" >/dev/null 2>&1 " +
        "|| cmd package unsuspend \"\$p\" >/dev/null 2>&1 " +
        "|| cmd package unsuspend --user 0 \"\$p\" >/dev/null 2>&1 " +
        "|| true; done"

    fun install(app: Application) {
        if (startedThisProcess.compareAndSet(false, true)) {
            unsuspendAll(app)
        }
        app.registerActivityLifecycleCallbacks(object : Application.ActivityLifecycleCallbacks {
            override fun onActivityResumed(activity: Activity) {
                if (activity is MainActivity) unsuspendAll(activity.applicationContext)
            }
            override fun onActivityCreated(activity: Activity, savedInstanceState: android.os.Bundle?) {}
            override fun onActivityStarted(activity: Activity) {}
            override fun onActivityPaused(activity: Activity) {}
            override fun onActivityStopped(activity: Activity) {}
            override fun onActivitySaveInstanceState(activity: Activity, outState: android.os.Bundle) {}
            override fun onActivityDestroyed(activity: Activity) {}
        })
    }

    fun unsuspendAll(ctx: Context, after: ((Boolean) -> Unit)? = null) {
        // Opening MainActivity again should not re-run a long unsuspend if one already succeeded.
        // The settings button always passes [after], so it can be tapped again.
        if (after == null && completedOk.get()) return
        if (!running.compareAndSet(false, true)) {
            after?.let { main.post { it(true) } }
            return
        }
        val app = ctx.applicationContext
        Thread({
            val ok = try {
                unsuspendAllBlocking()
            } catch (_: Exception) {
                false
            } finally {
                running.set(false)
            }
            if (ok) {
                completedOk.set(true)
                SessionPrefs.setHostSuspendedList(app, emptyList())
            }
            after?.let { main.post { it(ok) } }
        }, "oneos-unfreeze-all").start()
    }

    /** Worker-thread unsuspend used by the in-app updater before `pm install`. */
    fun unsuspendAllBlocking(): Boolean {
        val proc = ProcessBuilder("su", "-c", SCRIPT).redirectErrorStream(true).start()
        val finished = proc.waitFor(3, TimeUnit.MINUTES)
        if (!finished) {
            proc.destroyForcibly()
            return false
        }
        return proc.exitValue() == 0
    }
}
