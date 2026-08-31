package com.sandeep.aijobapplicationtracker.domain.repository

import com.sandeep.aijobapplicationtracker.domain.model.UserProfileModel
import kotlinx.coroutines.flow.Flow

/**
 * Profile Repository Interface.
 */
interface ProfileRepository {
    
    /**
     * Emits the current user profile, or null if not setup yet.
     */
    fun getProfile(): Flow<UserProfileModel?>

    /**
     * Saves the user profile.
     */
    suspend fun saveProfile(profile: UserProfileModel)
}
