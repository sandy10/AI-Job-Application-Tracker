# Kotlin
-keep class kotlin.** { *; }
-keep class kotlinx.** { *; }

# Hilt
-keep class dagger.hilt.** { *; }
-keep @dagger.hilt.android.HiltAndroidApp class * { *; }
-keep @dagger.hilt.android.AndroidEntryPoint class * { *; }

# Room
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-keep @androidx.room.Dao class *

# DataStore
-keep class androidx.datastore.** { *; }

# WorkManager
-keep class * extends androidx.work.Worker
-keep class * extends androidx.work.CoroutineWorker
-keep class androidx.work.** { *; }

# Coil
-keep class coil.** { *; }

# Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}

# Models
# TODO: Keep your data models
-keep class com.sandeep.aijobapplicationtracker.domain.model.** { *; }
