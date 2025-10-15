package com.example.swipe

import android.app.Application
import com.example.swipe.di.appModule
import com.example.swipe.util.createNotificationChannel
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class SwipeApp : Application() {
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel(this)
        startKoin {
            androidLogger()
            androidContext(this@SwipeApp)
            modules(appModule)
        }
    }
}