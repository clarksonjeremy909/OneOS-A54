package com.fahadali.oneos.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.preference.Preference
import androidx.preference.PreferenceCategory
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import androidx.appcompat.app.AlertDialog
import com.fahadali.oneos.session.RemoveJob
import com.fahadali.oneos.R
import com.fahadali.oneos.session.LogJob
import com.fahadali.oneos.session.HostUnfreeze
import com.fahadali.oneos.session.SessionPrefs
import com.fahadali.oneos.session.AppUpdater
import com.fahadali.oneos.session.BugLogJob
import com.fahadali.oneos.BuildConfig
import dev.oneuiproject.oneui.R as OuiIcons

/** SESL9 preference rows, the same settings component used by Twidget. */
class SettingsPreferenceFragment : InsetPreferenceFragment() {
    override fun onResume() { super.onResume(); onCreatePreferences(null, null) }

    // Remove OneOS data says while it runs and how it ended (0.4.43, UX audit: no feedback at all before).
    private var unsubscribe: (() -> Unit)? = null
    private var lastRemove: RemoveJob.Phase? = null
    override fun onStart() {
        super.onStart()
        lastRemove = RemoveJob.state.phase
        unsubscribe = RemoveJob.observe { st ->
            if (!isAdded) return@observe
            val ended = lastRemove == RemoveJob.Phase.REMOVING && (st.phase == RemoveJob.Phase.DONE || st.phase == RemoveJob.Phase.FAILED)
            lastRemove = st.phase
            onCreatePreferences(null, null)
            if (ended) st.message?.let { msg ->
                AlertDialog.Builder(requireContext()).setTitle(R.string.remove_title).setMessage(msg).setPositiveButton(android.R.string.ok, null).show()
            }
        }
    }
    override fun onStop() { unsubscribe?.invoke(); unsubscribe = null; super.onStop() }
    override fun onCreatePreferences(state: Bundle?, rootKey: String?) {
        val ctx = preferenceManager.context
        preferenceScreen = preferenceManager.createPreferenceScreen(ctx)
        fun category(title: Int) = PreferenceCategory(ctx).apply { setTitle(title); preferenceScreen.addPreference(this) }
        fun row(group: PreferenceCategory, title: Int, summary: Int? = null, icon: Int = 0, target: Class<*>) {
            group.addDescribedPreference(Preference(ctx).apply {
                key = "setting_$title"
                setTitle(title)
                if (summary != null) setSummary(summary)
                isIconSpaceReserved = icon != 0
                if (icon != 0) setIcon(icon)
                setOnPreferenceClickListener { startActivity(Intent(ctx, target)); true }
            })
        }
        val setup = category(R.string.advanced_card_system)
        row(setup, R.string.image_title, R.string.advanced_file_summary, OuiIcons.drawable.ic_oui_folder_open, ImageActivity::class.java)
        row(setup, R.string.space_title, R.string.advanced_space_summary, OuiIcons.drawable.ic_oui_manage_storage_outline, OldStagesActivity::class.java)
        row(setup, R.string.prepare_title, R.string.advanced_setup_summary, OuiIcons.drawable.ic_oui_refresh, PrepareActivity::class.java)
        setup.addDescribedPreference(Preference(ctx).apply {
            key = "remove_data"
            val rs = RemoveJob.state
            setTitle(if (rs.busy) R.string.remove_running else R.string.remove_title)
            if (!rs.busy && rs.message != null) summary = rs.message else setSummary(R.string.remove_summary)
            isEnabled = !rs.busy
            setIcon(OuiIcons.drawable.ic_oui_delete_outline)
            setOnPreferenceClickListener {
                if (!RemoveJob.canStart()) android.widget.Toast.makeText(ctx, R.string.remove_busy, android.widget.Toast.LENGTH_LONG).show()
                else AlertDialog.Builder(ctx).setTitle(R.string.remove_confirm_title).setMessage(R.string.remove_confirm_message)
                    .setPositiveButton(R.string.remove_confirm_run) { _, _ -> activity?.let { SystemManage.removeAll(it) } }
                    .setNegativeButton(android.R.string.cancel, null).show()
                true
            }
        })
        fun toggle(group: PreferenceCategory, title: Int, summary: Int, value: Boolean, save: (Boolean) -> Unit) {
            group.addDescribedPreference(SwitchPreferenceCompat(ctx).apply {
                key = "setting_$title"
                setTitle(title); setSummary(summary); isIconSpaceReserved = false; isPersistent = false; isChecked = value
                setOnPreferenceChangeListener { _, next -> save(next as Boolean); true }
            })
        }
        val sessions = category(R.string.advanced_card_sessions)
        row(sessions, R.string.last_title, target = LastSessionActivity::class.java)
        toggle(sessions, R.string.advanced_heat_cap_title, R.string.advanced_heat_cap_summary, SessionPrefs.heatCap(ctx)) { SessionPrefs.setHeatCap(ctx, it) }
        toggle(sessions, R.string.advanced_host_suspend_title, R.string.advanced_host_suspend_summary, SessionPrefs.hostSuspend(ctx)) { SessionPrefs.setHostSuspend(ctx, it) }
        sessions.addDescribedPreference(Preference(ctx).apply {
            key = "setting_host_unsuspend"
            setTitle(R.string.advanced_host_suspend_unsuspend)
            setSummary(R.string.advanced_host_suspend_unsuspend_summary)
            isIconSpaceReserved = false
            setOnPreferenceClickListener {
                Toast.makeText(ctx, R.string.advanced_host_suspend_working, Toast.LENGTH_SHORT).show()
                HostUnfreeze.unsuspendAll(ctx) { ok ->
                    if (!isAdded) return@unsuspendAll
                    Toast.makeText(
                        ctx,
                        if (ok) R.string.advanced_host_suspend_unsuspend_done
                        else R.string.advanced_host_suspend_unsuspend_fail,
                        Toast.LENGTH_SHORT,
                    ).show()
                }
                true
            }
        })
        row(sessions, R.string.advanced_host_suspend_allow_title, R.string.advanced_host_suspend_allow_row_summary, target = HostSuspendAllowActivity::class.java)
        toggle(sessions, R.string.advanced_guest_perf_title, R.string.advanced_guest_perf_summary, SessionPrefs.guestPerf(ctx)) { SessionPrefs.setGuestPerf(ctx, it) }
        toggle(sessions, R.string.advanced_guest_beast_title, R.string.advanced_guest_beast_summary, SessionPrefs.guestBeast(ctx)) { SessionPrefs.setGuestBeast(ctx, it) }
        sessions.addDescribedPreference(Preference(ctx).apply {
            key = "setting_guest_refresh"
            setTitle(R.string.advanced_guest_refresh_title)
            summary = ctx.getString(R.string.advanced_guest_refresh_summary) + "\n" + GuestSessionOptions.label(ctx)
            isIconSpaceReserved = false
            setOnPreferenceClickListener {
                GuestSessionOptions.pick(requireContext()) { onCreatePreferences(null, null) }
                true
            }
        })
        toggle(sessions, R.string.advanced_full_logs_title, R.string.advanced_full_logs_summary, SessionPrefs.fullLogs(ctx)) { SessionPrefs.setFullLogs(ctx, it) }
        toggle(sessions, R.string.advanced_share_files_title, R.string.advanced_share_files_summary, SessionPrefs.shareFiles(ctx)) { SessionPrefs.setShareFiles(ctx, it) }
        toggle(sessions, R.string.usage_count_title, R.string.usage_count_summary, SessionPrefs.usageCount(ctx)) { SessionPrefs.setUsageCount(ctx, it) }
        val experimental = category(R.string.settings_experimental)
        toggle(experimental, R.string.advanced_sec_ims_title, R.string.advanced_sec_ims_summary, SessionPrefs.secIms(ctx)) { SessionPrefs.setSecIms(ctx, it) }
        toggle(experimental, R.string.advanced_edm_stub_title, R.string.advanced_edm_stub_summary, SessionPrefs.edmStub(ctx)) { SessionPrefs.setEdmStub(ctx, it) }
        toggle(experimental, R.string.verbose_boot_title, R.string.verbose_boot_summary, SessionPrefs.verboseBoot(ctx)) { SessionPrefs.setVerboseBoot(ctx, it) }
        val risky = category(R.string.advanced_card_risky)
        // 0.5.17 (owner): turning it on asks first, in red, with a 5 s wait (LockRiskDialog); off needs no question.
        risky.addDescribedPreference(SwitchPreferenceCompat(ctx).apply {
            key = "setting_${R.string.advanced_allow_lock_title}"
            setTitle(R.string.advanced_allow_lock_title); setSummary(R.string.advanced_allow_lock_summary)
            isIconSpaceReserved = false; isPersistent = false; isChecked = SessionPrefs.allowScreenLock(ctx)
            setOnPreferenceChangeListener { pref, next ->
                if (next != true) { SessionPrefs.setAllowScreenLock(ctx, false); return@setOnPreferenceChangeListener true }
                LockRiskDialog.show(requireActivity()) { yes ->
                    if (yes) { SessionPrefs.setAllowScreenLock(ctx, true); (pref as SwitchPreferenceCompat).isChecked = true }
                }
                false
            }
        })
        LogJob.latestZip(ctx)?.let { zip ->
            sessions.addDescribedPreference(Preference(ctx).apply {
                key = "session_logs"
                setTitle(R.string.logs_title)
                setSummary(R.string.advanced_logs_summary)
                isIconSpaceReserved = false
                setOnPreferenceClickListener {
                    startActivity(Intent(ctx, FindingsActivity::class.java).putExtra(FindingsActivity.EXTRA_ZIP, zip.path)); true
                }
            })
        }
        if (SessionRestore.offered(ctx)) {
            sessions.addDescribedPreference(Preference(ctx).apply {
                setTitle(R.string.last_restore_title); isIconSpaceReserved = false
                setOnPreferenceClickListener { activity?.let { SessionRestore.confirm(it) }; true }
            })
        }
        val help = category(R.string.settings_help)
        help.addDescribedPreference(Preference(ctx).apply {
            key = "setting_check_updates"
            setTitle(R.string.settings_check_updates)
            setSummary(R.string.settings_check_updates_summary)
            isIconSpaceReserved = false
            setOnPreferenceClickListener {
                if (AppUpdater.state.busy) {
                    Toast.makeText(ctx, R.string.settings_check_updates_busy, Toast.LENGTH_SHORT).show()
                    return@setOnPreferenceClickListener true
                }
                Toast.makeText(ctx, R.string.settings_check_updates_checking, Toast.LENGTH_SHORT).show()
                AppUpdater.check(ctx, force = true)
                var unsub: (() -> Unit)? = null
                unsub = AppUpdater.observe { s ->
                    if (s.phase == AppUpdater.Phase.CHECKING) return@observe
                    unsub?.invoke()
                    val act = activity ?: return@observe
                    val msg = when (s.phase) {
                        AppUpdater.Phase.CURRENT -> act.getString(R.string.settings_check_updates_current, BuildConfig.VERSION_NAME)
                        AppUpdater.Phase.AVAILABLE -> act.getString(R.string.settings_check_updates_available, s.update?.versionName.orEmpty())
                        AppUpdater.Phase.FAILED -> s.message ?: act.getString(R.string.update_error_generic, "no answer")
                        AppUpdater.Phase.DOWNLOADING, AppUpdater.Phase.INSTALLING, AppUpdater.Phase.CONFIRM -> return@observe
                        else -> return@observe
                    }
                    AlertDialog.Builder(act).setTitle(R.string.settings_check_updates).setMessage(msg)
                        .setPositiveButton(android.R.string.ok, null).show()
                }
                true
            }
        })

        help.addDescribedPreference(Preference(ctx).apply {
            key = "setting_bug_log"
            setTitle(R.string.bug_log_title)
            setSummary(R.string.bug_log_summary)
            isIconSpaceReserved = false
            isEnabled = !BugLogJob.state.busy
            setOnPreferenceClickListener {
                if (BugLogJob.state.busy) return@setOnPreferenceClickListener true
                Toast.makeText(ctx, R.string.bug_log_working, Toast.LENGTH_SHORT).show()
                BugLogJob.start(ctx)
                var unsub: (() -> Unit)? = null
                unsub = BugLogJob.observe { s ->
                    if (s.busy) return@observe
                    unsub?.invoke()
                    val act = activity ?: return@observe
                    val msg = if (s.ok && s.fileName != null) act.getString(R.string.bug_log_ok, s.fileName)
                    else act.getString(R.string.bug_log_failed, s.message ?: "no answer")
                    AlertDialog.Builder(act).setTitle(R.string.bug_log_title).setMessage(msg)
                        .setPositiveButton(android.R.string.ok, null).show()
                }
                true
            }
        })

        row(help, R.string.about_title, target = AboutActivity::class.java)
        row(help, R.string.team_title, R.string.advanced_team_summary, target = DebugSettingsActivity::class.java)
        preferenceScreen.addBottomInset()
    }
}
