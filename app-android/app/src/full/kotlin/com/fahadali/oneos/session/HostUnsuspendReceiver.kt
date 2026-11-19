package com.fahadali.oneos.session

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * Thaw One UI apps left frozen after a guest session when One UI boots or
 * when this app is updated. [OneOsApplication] also starts the same restore
 * when the process starts; this receiver covers the case where the user
 * does not open the app after a return or an update.
 */
class HostUnsuspendReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        when (intent?.action) {
            Intent.ACTION_BOOT_COMPLETED,
            Intent.ACTION_MY_PACKAGE_REPLACED -> {
                val pending = goAsync()
                HostSuspendActions.restoreOnStart(context.applicationContext) {
                    pending.finish()
                }
            }
        }
    }
}
