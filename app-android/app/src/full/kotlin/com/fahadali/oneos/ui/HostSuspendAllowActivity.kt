package com.fahadali.oneos.ui

import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import com.fahadali.oneos.R
import com.fahadali.oneos.session.HostSuspendApps
import com.fahadali.oneos.session.SessionPrefs

/** Basic allowlist: ticked apps stay running on One UI while the guest is open. */
class HostSuspendAllowActivity : ToolbarActivity() {
    override val screenTitle: CharSequence get() = getString(R.string.advanced_host_suspend_allow_title)

    override val preferenceInsets = true

    override fun createFragment(): Fragment = HostSuspendAllowFragment()

    override val layoutRes: Int get() = R.layout.activity_systems
}

class HostSuspendAllowFragment : CardsFragment() {
    override fun build(column: LinearLayout) {
        val ctx = column.context
        val apps = HostSuspendApps.allowlistCandidates(ctx)
        val selected = SessionPrefs.hostSuspendAllowlist(ctx).toMutableSet()
        val card = Cards.section(column, getString(R.string.advanced_host_suspend_allow_card), rows = true)
        CardRows.text(card, getString(R.string.advanced_host_suspend_allow_summary), inList = true)
        if (apps.isEmpty()) {
            CardRows.text(card, getString(R.string.advanced_host_suspend_allow_empty), inList = true)
            return
        }
        for (e in apps) {
            CardRows.tick(card, e.label, e.pkg, e.pkg in selected) { on ->
                if (on) selected += e.pkg else selected -= e.pkg
                SessionPrefs.setHostSuspendAllowlist(ctx, selected)
            }
        }
    }
}
