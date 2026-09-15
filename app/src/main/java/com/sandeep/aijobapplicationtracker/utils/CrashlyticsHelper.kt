package com.sandeep.aijobapplicationtracker.utils

import com.google.firebase.crashlytics.FirebaseCrashlytics
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Centralized helper for Firebase Crashlytics.
 *
 * Use this to:
 * - Log non-fatal exceptions so they appear in the Crashlytics dashboard.
 * - Attach custom keys (userId, screen, feature) to every crash report for context.
 * - Log breadcrumb messages that show up in the crash log trail.
 *
 * Inject this into any ViewModel or Repository where you catch exceptions.
 */
@Singleton
class CrashlyticsHelper @Inject constructor() {

    private val crashlytics = FirebaseCrashlytics.getInstance()

    // ---- User Context ----

    /**
     * Set the current user ID so every crash is linked to a user.
     * Call this right after a successful login.
     */
    fun setUserId(uid: String) {
        crashlytics.setUserId(uid)
        Timber.d("Crashlytics: userId set to $uid")
    }

    /**
     * Clear the user ID on logout to stop associating crashes to the old user.
     */
    fun clearUserId() {
        crashlytics.setUserId("")
    }

    // ---- Custom Keys ----

    /**
     * Tag the current screen so crashes show which screen the user was on.
     * Call this at the top of each ViewModel init{} or when navigation changes.
     */
    fun setCurrentScreen(screenName: String) {
        crashlytics.setCustomKey(CrashlyticsKeys.SCREEN, screenName)
    }

    /**
     * Tag the AI feature being used when an AI call fails.
     */
    fun setCurrentFeature(featureName: String) {
        crashlytics.setCustomKey(CrashlyticsKeys.FEATURE, featureName)
    }

    /**
     * Attach the current job ID context so we know which application triggered a crash.
     */
    fun setCurrentJobId(jobId: String) {
        crashlytics.setCustomKey(CrashlyticsKeys.JOB_ID, jobId)
    }

    // ---- Logging ----

    /**
     * Log a breadcrumb message that appears in the crash timeline.
     * Use for important state transitions (e.g. "Interview started", "AI request sent").
     */
    fun log(message: String) {
        crashlytics.log(message)
        Timber.d("Crashlytics breadcrumb: $message")
    }

    /**
     * Report a non-fatal exception to Crashlytics.
     * Use this inside every catch{} block in Repositories and ViewModels.
     *
     * @param throwable The caught exception
     * @param message   A human-readable context message e.g. "Failed to generate follow-up email"
     */
    fun recordException(throwable: Throwable, message: String = "") {
        if (message.isNotBlank()) {
            crashlytics.log(message)
        }
        crashlytics.recordException(throwable)
        Timber.e(throwable, "Crashlytics non-fatal: $message")
    }
}

/**
 * Keys for Crashlytics custom key-value pairs.
 * Centralized here to avoid typos across the codebase.
 */
object CrashlyticsKeys {
    const val SCREEN = "screen"
    const val FEATURE = "feature"
    const val JOB_ID = "job_id"
    const val USER_ID = "user_id"
}
