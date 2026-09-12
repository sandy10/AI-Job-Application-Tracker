package com.sandeep.aijobapplicationtracker.di

import com.sandeep.aijobapplicationtracker.data.repository.FirebaseAuthRepositoryImpl
import com.sandeep.aijobapplicationtracker.data.repository.FirestoreJobApplicationRepositoryImpl
import com.sandeep.aijobapplicationtracker.data.repository.FirestoreProfileRepositoryImpl
import com.sandeep.aijobapplicationtracker.data.repository.PlaceholderRepositoryImpl
import com.sandeep.aijobapplicationtracker.domain.repository.AuthRepository
import com.sandeep.aijobapplicationtracker.domain.repository.ProfileRepository
import com.sandeep.aijobapplicationtracker.domain.repository.JobApplicationRepository
import com.sandeep.aijobapplicationtracker.domain.repository.PlaceholderRepository
import com.sandeep.aijobapplicationtracker.domain.repository.AiAnalyzerRepository
import com.sandeep.aijobapplicationtracker.data.repository.GeminiAiAnalyzerRepositoryImpl
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

    /** Binds the real Firebase Auth implementation. */
    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        impl: FirebaseAuthRepositoryImpl
    ): AuthRepository

    /** Binds the real Firestore profile implementation. */
    @Binds
    @Singleton
    abstract fun bindProfileRepository(
        impl: FirestoreProfileRepositoryImpl
    ): ProfileRepository

    /** Binds the real Firestore job application implementation. */
    @Binds
    @Singleton
    abstract fun bindJobApplicationRepository(
        impl: FirestoreJobApplicationRepositoryImpl
    ): JobApplicationRepository

    @Binds
    @Singleton
    abstract fun bindAiAnalyzerRepository(
        impl: GeminiAiAnalyzerRepositoryImpl
    ): AiAnalyzerRepository

    @Binds
    @Singleton
    abstract fun bindResumeRepository(
        impl: com.sandeep.aijobapplicationtracker.data.repository.FirestoreResumeRepositoryImpl
    ): com.sandeep.aijobapplicationtracker.domain.repository.ResumeRepository
}
