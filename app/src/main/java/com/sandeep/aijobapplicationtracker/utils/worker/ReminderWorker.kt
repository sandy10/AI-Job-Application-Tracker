package com.sandeep.aijobapplicationtracker.utils.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.sandeep.aijobapplicationtracker.domain.repository.JobApplicationRepository
import com.sandeep.aijobapplicationtracker.utils.NotificationHelper
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.firstOrNull
import timber.log.Timber

@HiltWorker
class ReminderWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val repository: JobApplicationRepository
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        Timber.d("ReminderWorker started")
        try {
            val applications = repository.getApplications().firstOrNull() ?: return Result.success()
            
            var followUpCount = 0
            var interviewCount = 0

            applications.forEach { app ->
                val status = app.status.lowercase()
                if (status == "applied") {
                    followUpCount++
                } else if (status == "interview") {
                    interviewCount++
                }
            }

            if (interviewCount > 0) {
                NotificationHelper.showNotification(
                    context,
                    notificationId = 1001,
                    title = "Upcoming Interviews",
                    message = "You have $interviewCount upcoming interview(s) to prepare for."
                )
            }

            if (followUpCount > 0) {
                NotificationHelper.showNotification(
                    context,
                    notificationId = 1002,
                    title = "Follow-ups Needed",
                    message = "You have $followUpCount application(s) that might need a follow-up."
                )
            }

            return Result.success()
        } catch (e: Exception) {
            Timber.e(e, "Error in ReminderWorker")
            return Result.retry()
        }
    }
}
