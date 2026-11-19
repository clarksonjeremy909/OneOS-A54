package com.fahadali.oneos.ui

import android.content.Context
import android.content.res.Configuration
import com.fahadali.oneos.session.HostSuspendActions
import com.fahadali.oneos.session.Readiness
import java.util.Locale

class OneOsApplication : android.app.Application() {
    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(turkish(base))
    }

    override fun onCreate() {
        Locale.setDefault(Locale.forLanguageTag("tr"))
        super.onCreate()
        androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode(androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        HostSuspendActions.restoreOnStart(this) { Readiness.tick(this) }
    }

    companion object {
        fun turkish(base: Context): Context {
            val locale = Locale.forLanguageTag("tr")
            Locale.setDefault(locale)
            val config = Configuration(base.resources.configuration)
            config.setLocale(locale)
            return base.createConfigurationContext(config)
        }
    }
}
