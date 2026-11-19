package com.fahadali.oneos.guest

import android.content.Context
import android.content.res.Configuration
import java.util.Locale

class GuestApplication : android.app.Application() {
    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(if (BuildConfig.FORCE_TURKISH) turkish(base) else base)
    }

    override fun onCreate() {
        if (BuildConfig.FORCE_TURKISH) Locale.setDefault(Locale.forLanguageTag("tr"))
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
