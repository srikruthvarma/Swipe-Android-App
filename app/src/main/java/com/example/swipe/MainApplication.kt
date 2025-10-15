package com.example.swipe

import android.app.Application
import com.example.swipe.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // This is the most important step: Starting Koin on app launch
        startKoin {
            // Provide the Android context to Koin
            androidContext(this@MainApplication)
            // Load the module definitions
            modules(appModule)
        }
    }
}
