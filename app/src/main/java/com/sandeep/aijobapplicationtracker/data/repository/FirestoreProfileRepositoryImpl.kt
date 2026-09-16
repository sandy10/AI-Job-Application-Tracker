package com.sandeep.aijobapplicationtracker.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.sandeep.aijobapplicationtracker.domain.model.UserProfileModel
import com.sandeep.aijobapplicationtracker.domain.repository.ProfileRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject

/**
 * Firebase Firestore implementation of [ProfileRepository].
 * User profile is stored at: users/{uid}/profile
 */
class FirestoreProfileRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth
) : ProfileRepository {

    /** Returns the Firestore document reference for the current user's profile. */
    private fun profileDocument() = firestore
        .collection("users")
        .document(firebaseAuth.currentUser?.uid ?: "anonymous")
        .collection("profile")
        .document("data")

    /**
     * Emits the current user profile from Firestore in real time.
     * Returns null if no profile document exists yet.
     */
    override fun getProfile(): Flow<UserProfileModel?> = callbackFlow {
        val uid = firebaseAuth.currentUser?.uid
        if (uid == null) {
            trySend(null)
            close()
            return@callbackFlow
        }

        val listener = profileDocument().addSnapshotListener { snapshot, error ->
            if (error != null) {
                Timber.e(error, "Firestore getProfile error")
                trySend(null)
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                val profile = UserProfileModel(
                    name = snapshot.getString("name") ?: "",
                    targetRole = snapshot.getString("targetRole") ?: "",
                    experienceLevel = snapshot.getString("experienceLevel") ?: "",
                    yearsExperience = snapshot.getString("yearsExperience") ?: "",
                    location = snapshot.getString("location") ?: "",
                    workPreference = snapshot.getString("workPreference") ?: "",
                    currentCtc = snapshot.getString("currentCtc") ?: "",
                    expectedCtc = snapshot.getString("expectedCtc") ?: "",
                    noticePeriod = snapshot.getString("noticePeriod") ?: "",
                    skills = (snapshot.get("skills") as? List<*>)
                        ?.filterIsInstance<String>() ?: emptyList()
                )
                trySend(profile)
            } else {
                // No profile yet — try to pre-fill from Firebase Auth display name
                val displayName = firebaseAuth.currentUser?.displayName ?: ""
                if (displayName.isNotBlank()) {
                    trySend(UserProfileModel(
                        name = displayName,
                        targetRole = "",
                        experienceLevel = "",
                        yearsExperience = "",
                        location = "",
                        workPreference = ""
                    ))
                } else {
                    trySend(null)
                }
            }
        }

        awaitClose { listener.remove() }
    }

    /**
     * Saves the user profile to Firestore.
     */
    override suspend fun saveProfile(profile: UserProfileModel): Result<Unit> {
        return try {
            val uid = firebaseAuth.currentUser?.uid
                ?: throw IllegalStateException("User not logged in")

            firestore.collection("users").document(uid)
                .collection("profile").document("data")
                .set(mapOf(
                    "name" to profile.name,
                    "targetRole" to profile.targetRole,
                    "experienceLevel" to profile.experienceLevel,
                    "yearsExperience" to profile.yearsExperience,
                    "location" to profile.location,
                    "workPreference" to profile.workPreference,
                    "currentCtc" to profile.currentCtc,
                    "expectedCtc" to profile.expectedCtc,
                    "noticePeriod" to profile.noticePeriod,
                    "skills" to profile.skills
                ), com.google.firebase.firestore.SetOptions.merge())
                .await()

            Timber.d("Profile saved for user: $uid")
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Failed to save profile")
            Result.failure(e)
        }
    }
}
