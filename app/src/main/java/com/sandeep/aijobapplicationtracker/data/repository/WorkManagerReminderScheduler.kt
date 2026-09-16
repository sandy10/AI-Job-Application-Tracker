package com.sandeep.aijobapplicationtracker.data.repository

import android.content.Context
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.sandeep.aijobapplicationtracker.domain.repository.ReminderScheduler
import com.sandeep.aijobapplicationtracker.utils.ReminderWorker
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class WorkManagerReminderScheduler @Inject constructor(
    @ApplicationContext private val context: Context
) : ReminderScheduler {

    override fun scheduleReminder(
        title: String,
        message: String,
        targetDateTime: String,
        delayMinutes: Long
    ) {
        try {
            val inputData = Data.Builder()
                .putString("title", title)
                .putString("message", message)
                .build()

            val format = SimpleDateFormat("yyyy-MM-dd hh:mm a", Locale.getDefault())
            val interviewDate = format.parse(targetDateTime)
            if (interviewDate != null) {
                val reminderTimeMillis = interviewDate.time - (delayMinutes * 60 * 1000)
                val initialDelayMillis = reminderTimeMillis - System.currentTimeMillis()

                if (initialDelayMillis > 0) {
                    val workRequest = OneTimeWorkRequestBuilder<ReminderWorker>()
                        .setInitialDelay(initialDelayMillis, TimeUnit.MILLISECONDS)
                        .setInputData(inputData)
                        .build()

                    WorkManager.getInstance(context).enqueue(workRequest)
                }
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to schedule reminder")
        }
    }
}
