package com.sandeep.aijobapplicationtracker.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.sandeep.aijobapplicationtracker.domain.repository.AuthRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject

/**
 * Firebase implementation of [AuthRepository].
 * Handles user authentication using Firebase Auth.
 * Google Sign-In credential is obtained via Credential Manager in the UI
 * and passed here as a Google ID token.
 */
class FirebaseAuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : AuthRepository {

    /**
     * Emits true if a Firebase user is currently signed in, false otherwise.
     * Uses callbackFlow to listen for real-time auth state changes.
     */
    override fun isLoggedIn(): Flow<Boolean> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            trySend(auth.currentUser != null)
        }
        firebaseAuth.addAuthStateListener(listener)
        awaitClose { firebaseAuth.removeAuthStateListener(listener) }
    }

    override suspend fun verifySession(): Boolean {
        val user = firebaseAuth.currentUser ?: return false
        return try {
            user.reload().await()
            true
        } catch (e: Exception) {
            Timber.e(e, "Session verification failed (user deleted or disabled)")
            firebaseAuth.signOut()
            false
        }
    }

    /**
     * Signs in with email and password using Firebase Auth.
     */
    override suspend fun login(email: String, password: String): Result<Unit> {
        return try {
            firebaseAuth.signInWithEmailAndPassword(email, password).await()
            Timber.d("Firebase login successful for: $email")
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Firebase login failed")
            Result.failure(e)
        }
    }

    /**
     * Signs up with email and password using Firebase Auth.
     */
    override suspend fun signUp(email: String, password: String): Result<Unit> {
        return try {
            firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            Timber.d("Firebase signup successful for: $email")
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Firebase signup failed")
            Result.failure(e)
        }
    }

    /**
     * Sends a password reset email using Firebase Auth.
     */
    override suspend fun sendPasswordResetEmail(email: String): Result<Unit> {
        return try {
            firebaseAuth.sendPasswordResetEmail(email).await()
            Timber.d("Password reset email sent to: $email")
            Result.success(Unit)
        } catch (e: Exception) {
            val message = if (e.message?.contains("no user record") == true) {
                "No account found with this email"
            } else {
                e.message ?: "Password reset failed"
            }
            Timber.e(e, "Password reset failed")
            Result.failure(Exception(message))
        }
    }

    /**
     * No-op placeholder. The actual Google Sign-In flow goes through
     * [signInWithGoogleIdToken] which receives the token from the UI layer.
     */
    override suspend fun loginWithGoogle(): Result<Unit> {
        return Result.success(Unit)
    }

    /**
     * Signs in to Firebase using a Google ID token obtained from Credential Manager.
     * The token is exchanged for a Firebase credential which authenticates the user.
     */
    override suspend fun signInWithGoogleIdToken(idToken: String): Result<Unit> {
        return try {
            // Create a Firebase credential from the Google ID token
            val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
            // Sign in to Firebase with the Google credential
            firebaseAuth.signInWithCredential(firebaseCredential).await()
            Timber.d("Firebase Google Sign-In successful for: ${firebaseAuth.currentUser?.email}")
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Firebase Google Sign-In failed")
            Result.failure(e)
        }
    }

    /**
     * Returns the display name from Firebase Auth (e.g. from Google Sign-In).
     */
    override fun getCurrentUserName(): String? {
        return firebaseAuth.currentUser?.displayName
    }

    /**
     * Signs out the current Firebase user.
     */
    override suspend fun logout() {
        firebaseAuth.signOut()
        Timber.d("Firebase logout successful")
    }
}
