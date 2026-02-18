package com.example.mukmuk.notification

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class LunchReminderWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        NotificationHelper.showLunchReminder(applicationContext)
        return Result.success()
    }
}
