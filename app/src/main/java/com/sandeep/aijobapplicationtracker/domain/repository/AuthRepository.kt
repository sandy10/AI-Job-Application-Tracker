package com.sandeep.aijobapplicationtracker.domain.repository

import kotlinx.coroutines.flow.Flow

/**
 * Authentication Repository interface.
 */
interface AuthRepository {
    
    /**
     * Emits the current authentication state. True if logged in, false otherwise.
     */
    fun isLoggedIn(): Flow<Boolean>

    /**
     * Simulate a login request.
     */
    suspend fun login(email: String, password: String): Result<Unit>

    /**
     * Simulate a Google sign-in request.
     */
    suspend fun loginWithGoogle(): Result<Unit>

    /**
     * Sign out the current user.
     */
    suspend fun logout()
}
