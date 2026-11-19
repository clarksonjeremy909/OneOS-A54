package com.fahadali.oneos.ui

import android.content.Context
import android.content.res.Configuration
import com.fahadali.oneos.BuildConfig
import com.fahadali.oneos.session.HostUnfreeze
import com.fahadali.oneos.session.Readiness
import java.util.Locale

class OneOsApplication : android.app.Application() {
    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(if (BuildConfig.FORCE_TURKISH) turkish(base) else base)
    }

    override fun onCreate() {
        if (BuildConfig.FORCE_TURKISH) Locale.setDefault(Locale.forLanguageTag("tr"))
        super.onCreate()
        androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode(androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        HostUnfreeze.install(this)
        // install() already starts the unsuspend-all; tick readiness shortly after.
        android.os.Handler(android.os.Looper.getMainLooper()).post { Readiness.tick(this) }
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
