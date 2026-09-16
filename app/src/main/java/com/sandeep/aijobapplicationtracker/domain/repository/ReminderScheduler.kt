package com.sandeep.aijobapplicationtracker.domain.repository

/**
 * Interface for scheduling reminders.
 * Abstracts the Android-specific implementation (e.g., WorkManager) from the domain/presentation layers.
 */
interface ReminderScheduler {
    
    /**
     * Schedules a reminder notification.
     * @param title The title of the reminder.
     * @param message The body message of the reminder.
     * @param targetDateTime The target date and time string in "yyyy-MM-dd hh:mm a" format.
     * @param delayMinutes How many minutes before the target time the reminder should trigger.
     */
    fun scheduleReminder(
        title: String,
        message: String,
        targetDateTime: String,
        delayMinutes: Long
    )
}
