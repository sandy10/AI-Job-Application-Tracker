package com.sandeep.aijobapplicationtracker.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import com.sandeep.aijobapplicationtracker.domain.repository.AuthRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Mock implementation of AuthRepository that uses DataStore to persist a simulated login state.
 * This is used for MVP as requested in AGENTS.md (No Firebase unless explicitly asked).
 */
class MockAuthRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : AuthRepository {

    private object PreferencesKeys {
        val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
    }

    override fun isLoggedIn(): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            preferences[PreferencesKeys.IS_LOGGED_IN] ?: false
        }
    }

    override suspend fun login(email: String, password: String): Result<Unit> {
        // Simulate network delay
        delay(1500)
        
        // Basic validation for mock purposes
        if (email.isBlank() || password.isBlank()) {
            return Result.failure(Exception("Email and password cannot be empty"))
        }

        dataStore.edit { preferences ->
            preferences[PreferencesKeys.IS_LOGGED_IN] = true
        }
        return Result.success(Unit)
    }

    override suspend fun loginWithGoogle(): Result<Unit> {
        // Simulate network delay
        delay(1500)

        dataStore.edit { preferences ->
            preferences[PreferencesKeys.IS_LOGGED_IN] = true
        }
        return Result.success(Unit)
    }

    override suspend fun logout() {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.IS_LOGGED_IN] = false
        }
    }

    override fun getCurrentUserName(): String? {
        return "Mock User"
    }

    override suspend fun signInWithGoogleIdToken(idToken: String): Result<Unit> {
        // Mock implementation — not used when Firebase is active
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.IS_LOGGED_IN] = true
        }
        return Result.success(Unit)
    }
}
