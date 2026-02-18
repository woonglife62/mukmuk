package com.example.mukmuk

import android.app.Application
import com.example.mukmuk.di.AppContainer
import com.example.mukmuk.notification.NotificationHelper
import com.example.mukmuk.notification.NotificationScheduler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MukmukApplication : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
        NotificationHelper.createNotificationChannel(this)
        rescheduleNotificationsIfEnabled()
    }

    private fun rescheduleNotificationsIfEnabled() {
        CoroutineScope(Dispatchers.IO).launch {
            val enabled = container.settingsRepository.notificationEnabled.first()
            if (enabled) {
                val hour = container.settingsRepository.notificationHour.first()
                val minute = container.settingsRepository.notificationMinute.first()
                NotificationScheduler.scheduleLunchReminder(this@MukmukApplication, hour, minute)
            }
        }
    }
}
