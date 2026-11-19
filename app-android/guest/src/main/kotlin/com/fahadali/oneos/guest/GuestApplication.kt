package com.fahadali.oneos.guest

import android.content.Context
import android.content.res.Configuration
import java.util.Locale

class GuestApplication : android.app.Application() {
    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(turkish(base))
    }

    override fun onCreate() {
        Locale.setDefault(Locale.forLanguageTag("tr"))
        super.onCreate()
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
