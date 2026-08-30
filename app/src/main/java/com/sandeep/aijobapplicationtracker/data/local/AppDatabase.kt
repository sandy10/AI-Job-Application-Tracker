package com.sandeep.aijobapplicationtracker.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.sandeep.aijobapplicationtracker.data.local.dao.PlaceholderDao
import com.sandeep.aijobapplicationtracker.data.local.entity.PlaceholderEntity

/**
 * Room App Database.
 * TODO: Add your entities here
 */
@Database(
    entities = [PlaceholderEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract val placeholderDao: PlaceholderDao
}
