package com.sandeep.aijobapplicationtracker

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

/**
 * Main application class.
 */
@HiltAndroidApp
class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize Timber for logging
        Timber.plant(Timber.DebugTree())
    }
}
