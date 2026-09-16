package com.sandeep.aijobapplicationtracker

import android.app.Application
import com.sandeep.aijobapplicationtracker.BuildConfig
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

/**
 * Main application class.
 */
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory

import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import javax.inject.Inject

@HiltAndroidApp
class MainApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()

        // C2 Fix: Only plant Timber debug tree in debug builds
        // to prevent PII (AI responses, user data) from leaking to logcat in production
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        
        FirebaseApp.initializeApp(this)

        // C1 Fix: Use PlayIntegrity for release builds, Debug provider only for debug
        // DebugAppCheckProviderFactory bypasses attestation — must never ship to production
        val firebaseAppCheck = FirebaseAppCheck.getInstance()
        if (BuildConfig.DEBUG) {
            firebaseAppCheck.installAppCheckProviderFactory(
                DebugAppCheckProviderFactory.getInstance()
            )
        } else {
            firebaseAppCheck.installAppCheckProviderFactory(
                PlayIntegrityAppCheckProviderFactory.getInstance()
            )
        }
    }
}
