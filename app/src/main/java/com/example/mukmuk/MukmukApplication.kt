package com.example.mukmuk

import android.app.Application
import com.example.mukmuk.di.AppContainer
import com.example.mukmuk.notification.NotificationHelper

class MukmukApplication : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
        NotificationHelper.createNotificationChannel(this)
    }
}
