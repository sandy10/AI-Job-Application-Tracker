package com.sandeep.aijobapplicationtracker.utils

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.sandeep.aijobapplicationtracker.utils.Constants.Analytics
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Centralized helper for all Firebase Analytics event logging.
 *
 * All event names and parameter keys are pulled from [Constants.Analytics]
 * to prevent typos and ensure consistency across the codebase.
 *
 * Inject this class into any ViewModel to track events.
 */
@Singleton
class AnalyticsHelper @Inject constructor(
    private val firebaseAnalytics: FirebaseAnalytics
) {

    /**
     * Track when a user successfully logs in.
     * @param method The sign-in method used (e.g. "email", "google")
     */
    fun trackLogin(method: String) {
        val bundle = Bundle().apply {
            putString(Analytics.PARAM_METHOD, method)
        }
        firebaseAnalytics.logEvent(Analytics.EVENT_LOGIN, bundle)
        Timber.d("Analytics: ${Analytics.EVENT_LOGIN} method=$method")
    }

    /**
     * Track when a user logs out.
     */
    fun trackLogout() {
        firebaseAnalytics.logEvent(Analytics.EVENT_LOGOUT, null)
        Timber.d("Analytics: ${Analytics.EVENT_LOGOUT}")
    }

    /**
     * Track when a new job application is created.
     * @param source Where the job was sourced (e.g. "LinkedIn", "Manual")
     * @param method How it was created (e.g. "manual", "ai_analyzer")
     * @param experienceLevel The user's experience level from their profile
     */
    fun trackApplicationCreated(
        source: String,
        method: String,
        experienceLevel: String
    ) {
        val bundle = Bundle().apply {
            putString(Analytics.PARAM_SOURCE, source)
            putString(Analytics.PARAM_METHOD, method)
            putString(Analytics.PARAM_EXPERIENCE_LEVEL, experienceLevel)
        }
        firebaseAnalytics.logEvent(Analytics.EVENT_APPLICATION_CREATED, bundle)
        Timber.d("Analytics: ${Analytics.EVENT_APPLICATION_CREATED} source=$source method=$method")
    }

    /**
     * Track when an application status changes.
     * @param jobId The Firestore document ID of the job
     * @param fromStatus The previous status
     * @param toStatus The new status
     */
    fun trackStatusChanged(
        jobId: String,
        fromStatus: String,
        toStatus: String
    ) {
        val bundle = Bundle().apply {
            putString(Analytics.PARAM_JOB_ID, jobId)
            putString(Analytics.PARAM_FROM_STATUS, fromStatus)
            putString(Analytics.PARAM_TO_STATUS, toStatus)
        }
        firebaseAnalytics.logEvent(Analytics.EVENT_STATUS_CHANGED, bundle)
        Timber.d("Analytics: ${Analytics.EVENT_STATUS_CHANGED} $fromStatus → $toStatus")
    }

    /**
     * Track when an AI feature is used.
     * @param featureName Use constants from [Constants.Analytics] e.g. FEATURE_JOB_ANALYZER
     * @param jobId Optional: the associated job's Firestore ID
     */
    fun trackAiFeatureUsed(featureName: String, jobId: String = "") {
        val bundle = Bundle().apply {
            putString(Analytics.PARAM_FEATURE_NAME, featureName)
            if (jobId.isNotBlank()) putString(Analytics.PARAM_JOB_ID, jobId)
        }
        firebaseAnalytics.logEvent(Analytics.EVENT_AI_FEATURE_USED, bundle)
        Timber.d("Analytics: ${Analytics.EVENT_AI_FEATURE_USED} feature=$featureName")
    }

    /**
     * Track when a Video AI Interview session starts.
     * @param jobId The Firestore document ID of the associated job
     */
    fun trackVideoInterviewStarted(jobId: String) {
        val bundle = Bundle().apply {
            putString(Analytics.PARAM_JOB_ID, jobId)
        }
        firebaseAnalytics.logEvent(Analytics.EVENT_VIDEO_INTERVIEW_STARTED, bundle)
        Timber.d("Analytics: ${Analytics.EVENT_VIDEO_INTERVIEW_STARTED} jobId=$jobId")
    }

    /**
     * Track when a Video AI Interview session completes.
     * @param jobId The Firestore document ID of the associated job
     * @param score The AI evaluation score (0–10)
     */
    fun trackVideoInterviewCompleted(jobId: String, score: Int) {
        val bundle = Bundle().apply {
            putString(Analytics.PARAM_JOB_ID, jobId)
            putInt(Analytics.PARAM_SCORE, score)
        }
        firebaseAnalytics.logEvent(Analytics.EVENT_VIDEO_INTERVIEW_COMPLETED, bundle)
        Timber.d("Analytics: ${Analytics.EVENT_VIDEO_INTERVIEW_COMPLETED} jobId=$jobId score=$score")
    }

    /**
     * Track manual screen views (supplements the automatic Firebase screen tracking).
     * @param screenName A human-readable screen name e.g. "ApplicationDetail"
     */
    fun trackScreenView(screenName: String) {
        val bundle = Bundle().apply {
            putString(Analytics.PARAM_SCREEN_NAME, screenName)
            putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
        }
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle)
        Timber.d("Analytics: screen_view screen=$screenName")
    }
}
