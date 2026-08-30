package com.sandeep.aijobapplicationtracker.di

import com.sandeep.aijobapplicationtracker.data.repository.PlaceholderRepositoryImpl
import com.sandeep.aijobapplicationtracker.domain.repository.PlaceholderRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindPlaceholderRepository(
        impl: PlaceholderRepositoryImpl
    ): PlaceholderRepository
}
