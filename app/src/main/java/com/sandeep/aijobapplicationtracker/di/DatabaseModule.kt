package com.sandeep.aijobapplicationtracker.di

import android.content.Context
import androidx.room.Room
import com.sandeep.aijobapplicationtracker.data.local.AppDatabase
import com.sandeep.aijobapplicationtracker.utils.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            Constants.DATABASE_NAME
        ).build()
    }

    // TODO: Add your DAOs here
    @Provides
    @Singleton
    fun providePlaceholderDao(db: AppDatabase) = db.placeholderDao
}
