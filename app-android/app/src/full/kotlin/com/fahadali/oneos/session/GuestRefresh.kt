package com.fahadali.oneos.session

import android.content.Context
import android.hardware.display.DisplayManager
import android.view.Display
import kotlin.math.roundToInt

/**
 * Guest refresh-rate choices. `off` leaves the guest's own rate (and clears a
 * leftover min=peak pin). `auto` or a listed Hz sets only peak_refresh_rate
 * to a physical mode of this panel; 90 Hz is not assumed on a 60/120 panel
 * such as the Galaxy A54.
 */
object GuestRefresh {
    const val OFF = "off"
    const val AUTO = "auto"
    private val ALLOWED = Regex("off|auto|([3-9][0-9]|1[01][0-9]|120)")

    fun normalize(value: String?): String {
        val v = value?.trim().orEmpty()
        return if (ALLOWED.matches(v)) v else OFF
    }

    /** Distinct physical modes in 30-120 Hz; empty when the display cannot be read. */
    fun availableHz(ctx: Context): List<Int> {
        val display = display(ctx) ?: return emptyList()
        return display.supportedModes.map { it.refreshRate.roundToInt() }
            .filter { it in 30..120 }
            .distinct()
            .sorted()
    }

    private fun display(ctx: Context): Display? {
        val dm = ctx.getSystemService(DisplayManager::class.java)
        return ctx.display ?: dm?.getDisplay(Display.DEFAULT_DISPLAY)
    }
}
