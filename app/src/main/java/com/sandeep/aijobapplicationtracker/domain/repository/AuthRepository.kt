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
     * Verifies the user session by forcing a token reload.
     * Returns false if the user was deleted or disabled on the backend.
     */
    suspend fun verifySession(): Boolean

    /**
     * Sign in with email and password.
     */
    suspend fun login(email: String, password: String): Result<Unit>

    /**
     * Sign up with email and password.
     */
    suspend fun signUp(email: String, password: String): Result<Unit>

    /**
     * Send password reset email.
     */
    suspend fun sendPasswordResetEmail(email: String): Result<Unit>

    /**
     * Sign in with a Google ID token obtained from Credential Manager.
     */
    suspend fun loginWithGoogle(): Result<Unit>

    /**
     * Sign in with a Google ID token credential.
     * This is the real Google Sign-In flow using Firebase Auth.
     */
    suspend fun signInWithGoogleIdToken(idToken: String): Result<Unit>

    /**
     * Returns the current user's display name from Firebase Auth, or null.
     */
    fun getCurrentUserName(): String?

    /**
     * Sign out the current user.
     */
    suspend fun logout()
}
