package com.fahadali.oneos.ui

import android.content.Context
import android.view.View
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.fahadali.oneos.R
import com.fahadali.oneos.session.GuestRefresh
import com.fahadali.oneos.session.SessionPrefs

/**
 * Refresh-rate picker and Canavar toggle for every guest system (the
 * Environment page and the Switch confirm). Choices are stored in
 * [SessionPrefs] and applied at launch for any ROM.
 */
internal object GuestSessionOptions {
    fun label(ctx: Context): String {
        val v = SessionPrefs.guestRefresh(ctx)
        return when (v) {
            GuestRefresh.OFF -> ctx.getString(R.string.advanced_guest_refresh_off)
            GuestRefresh.AUTO -> ctx.getString(R.string.advanced_guest_refresh_auto)
            else -> ctx.getString(R.string.advanced_guest_refresh_hz, v.toIntOrNull() ?: 0)
        }
    }

    fun choices(ctx: Context): List<Pair<String, String>> {
        val rates = GuestRefresh.availableHz(ctx)
        return buildList {
            add(GuestRefresh.OFF to ctx.getString(R.string.advanced_guest_refresh_off))
            add(GuestRefresh.AUTO to ctx.getString(R.string.advanced_guest_refresh_auto))
            for (hz in rates) add(hz.toString() to ctx.getString(R.string.advanced_guest_refresh_hz, hz))
        }
    }

    fun pick(ctx: Context, onPicked: () -> Unit = {}) {
        val items = choices(ctx)
        val current = SessionPrefs.guestRefresh(ctx)
        val checked = items.indexOfFirst { it.first == current }.coerceAtLeast(0)
        AlertDialog.Builder(ctx)
            .setTitle(R.string.advanced_guest_refresh_title)
            .setSingleChoiceItems(items.map { it.second }.toTypedArray(), checked) { dialog, which ->
                SessionPrefs.setGuestRefresh(ctx, items[which].first)
                dialog.dismiss()
                onPicked()
            }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }

    /** Adds the refresh picker and the Canavar toggle to [parent] (every guest). */
    fun bind(ctx: Context, parent: LinearLayout) {
        val density = ctx.resources.displayMetrics.density
        fun dp(v: Int) = (v * density + .5f).toInt()
        val title = TextView(ctx, null, android.R.attr.textAppearanceListItem).apply {
            text = ctx.getString(R.string.advanced_guest_refresh_title)
            textSize = 16f
            setPadding(0, 0, 0, dp(6))
        }
        parent.addView(title, LinearLayout.LayoutParams(-1, -2))
        val group = RadioGroup(ctx).apply {
            orientation = LinearLayout.VERTICAL
            id = View.generateViewId()
        }
        val current = SessionPrefs.guestRefresh(ctx)
        var matched = false
        for ((value, text) in choices(ctx)) {
            val button = RadioButton(ctx).apply {
                this.text = text
                tag = value
                isChecked = value == current
                if (isChecked) matched = true
                setOnClickListener { SessionPrefs.setGuestRefresh(ctx, value) }
            }
            group.addView(button, LinearLayout.LayoutParams(-2, -2).apply { marginEnd = dp(8) })
        }
        if (!matched) {
            val first = group.getChildAt(0) as? RadioButton
            first?.isChecked = true
            SessionPrefs.setGuestRefresh(ctx, GuestRefresh.OFF)
        }
        parent.addView(group, LinearLayout.LayoutParams(-1, -2))
        parent.addView(TextView(ctx).apply {
            text = ctx.getString(R.string.guest_refresh_hint)
            textSize = 13f
            setPadding(0, dp(6), 0, dp(8))
        }, LinearLayout.LayoutParams(-1, -2))
        CardRows.toggle(
            parent,
            ctx.getString(R.string.advanced_guest_beast_title),
            ctx.getString(R.string.advanced_guest_beast_summary),
            SessionPrefs.guestBeast(ctx),
            enabled = SessionPrefs.guestPerf(ctx),
        ) { on -> SessionPrefs.setGuestBeast(ctx, on) }
    }
}
