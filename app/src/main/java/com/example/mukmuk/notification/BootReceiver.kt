package com.example.mukmuk.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.mukmuk.data.repository.SettingsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return

        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val settingsRepository = SettingsRepository(context)
                val enabled = settingsRepository.notificationEnabled.first()
                if (enabled) {
                    val hour = settingsRepository.notificationHour.first()
                    val minute = settingsRepository.notificationMinute.first()
                    NotificationScheduler.scheduleLunchReminder(context, hour, minute)
                }
            } finally {
                pendingResult.finish()
            }
        }
    }
}
